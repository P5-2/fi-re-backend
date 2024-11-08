package fi.re.firebackend.util.goldPredict;

import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class GoldPriceExpectation {

    private static final int N_EPOCHS = 100;
    private static final double LEARNING_RATE = 0.002;
    private static final double MOMENTUM = 0.9;
    private static final int SEED = 1000;
    private static final int NUM_FEATURES = 7;
    private static final int TIME_SERIES_LENGTH = 365;
    private static final Logger log = Logger.getLogger(GoldPriceExpectation.class);

    private MultiLayerNetwork network;
    private NormalizerMinMaxScaler scaler;

    @PostConstruct
    public void init() {
        log.info("Initializing LSTM model...");
        this.network = createModel();
        this.scaler = new NormalizerMinMaxScaler(0, 1);
        log.info("Model initialized successfully.");
    }

    private MultiLayerNetwork createModel() {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(LEARNING_RATE, MOMENTUM))
                .list()
                .layer(0, new GravesLSTM.Builder()
                        .activation(Activation.TANH)
                        .nIn(NUM_FEATURES)
                        .nOut(10)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.TANH)
                        .nIn(10)
                        .nOut(NUM_FEATURES)
                        .build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        return model;
    }

    @Async
    public List<GoldPredicted> lstm(List<GoldInfo> goldInfoPerDay) throws Exception {
        long startTime = System.currentTimeMillis();

        int size = goldInfoPerDay.size() - TIME_SERIES_LENGTH;
        int trainSize = (int) (size * 0.7);
        int testSize = size - trainSize;

        log.info("Data split: Total=" + size + ", Train=" + trainSize + ", Test=" + testSize);

        DataSet trainData = getData(goldInfoPerDay, 0, trainSize);
        DataSet testData = getData(goldInfoPerDay, trainSize, testSize);

        scaler.fitLabel(true);
        scaler.fit(trainData);
        scaler.transform(trainData);
        scaler.transform(testData);

        trainModel(trainData);

        INDArray predicted = network.rnnTimeStep(testData.getFeatures());
        scaler.revertLabels(predicted);

        List<GoldPredicted> predictions = createPredictionList(predicted, testSize, goldInfoPerDay);

        logProcessingTime(startTime);
        return predictions;
    }

    private void trainModel(DataSet trainData) {
        for (int i = 1; i <= N_EPOCHS; i++) {
            network.fit(trainData);
        }
        log.info("Model training completed.");
    }

    private DataSet getData(List<GoldInfo> goldInfos, int startIndex, int dataSize) {
        INDArray input = Nd4j.create(dataSize, NUM_FEATURES, TIME_SERIES_LENGTH);
        INDArray output = Nd4j.create(dataSize, NUM_FEATURES, TIME_SERIES_LENGTH);

        for (int i = 0; i < dataSize; i++) {
            for (int j = 0; j < TIME_SERIES_LENGTH; j++) {
                GoldInfo goldInfo = goldInfos.get(startIndex + i + j);
                input.putScalar(new int[]{i, 0, j}, goldInfo.getClpr());
                input.putScalar(new int[]{i, 1, j}, goldInfo.getVs());
                input.putScalar(new int[]{i, 2, j}, goldInfo.getFltRt());
                input.putScalar(new int[]{i, 3, j}, goldInfo.getMkp());
                input.putScalar(new int[]{i, 4, j}, goldInfo.getHipr());
                input.putScalar(new int[]{i, 5, j}, goldInfo.getLopr());
                input.putScalar(new int[]{i, 6, j}, goldInfo.getTrqu());
            }
            output.putScalar(new int[]{i, 0, 0}, goldInfos.get(startIndex + i + TIME_SERIES_LENGTH).getClpr());
        }

        DataSet dataSet = new DataSet(input, output);
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        return dataSet;
    }

    private List<GoldPredicted> createPredictionList(INDArray predicted, int testSize, List<GoldInfo> goldInfoPerDay) throws ParseException {
        List<GoldPredicted> predictions = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date today = sdf.parse(sdf.format(Calendar.getInstance().getTime()));

        long adjust = goldInfoPerDay.subList(goldInfoPerDay.size() - 30, goldInfoPerDay.size())
                .stream()
                .mapToLong(GoldInfo::getClpr)
                .sum() / 30;

        for (int i = 0; i < testSize; i++) {
            long predictedValue = (long) predicted.getDouble(i) + adjust;
            String predictionDate = sdf.format(DateUtils.addDays(today, i + 1));
            predictions.add(new GoldPredicted(predictionDate, predictedValue));
        }

        return predictions;
    }

    private void logProcessingTime(long startTime) {
        double seconds = (System.currentTimeMillis() - startTime) / 1000.0;
        log.info("Processing Time: " + seconds + " seconds");
    }
}
