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
import org.springframework.stereotype.Component;

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


    public List<GoldPredicted> lstm(List<GoldInfo> goldInfoPerDay) throws Exception {
        long startTime = System.currentTimeMillis();

        // 데이터 분류
        int size = goldInfoPerDay.size() - TIME_SERIES_LENGTH;
        int trainSize = (int) (size * 0.7); // 70% 데이터는 훈련용
        int testSize = size - trainSize;
        GoldPriceExpectation.log.info(size + "-" + trainSize + "-" + testSize);

        // 학습 및 테스트 데이터 생성
        DataSet trainData = getTrainingData(goldInfoPerDay, trainSize, TIME_SERIES_LENGTH);
        DataSet testData = getTestData(goldInfoPerDay, testSize, TIME_SERIES_LENGTH);

        // 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        scaler.fitLabel(true);
        scaler.fit(trainData);
        scaler.transform(trainData);
        scaler.transform(testData);

        // 모델 구성
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder().seed(SEED).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).weightInit(WeightInit.XAVIER).updater(new Nesterovs(LEARNING_RATE, MOMENTUM)).list().layer(0, new GravesLSTM.Builder().activation(Activation.TANH).nIn(NUM_FEATURES).nOut(10).build()).layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.TANH).nIn(10).nOut(NUM_FEATURES).build()).build();

        // 모델 생성 및 학습
        MultiLayerNetwork network = new MultiLayerNetwork(config);
        network.init();

        // 모델 학습
        for (int i = 1; i <= N_EPOCHS; i++) {
            network.fit(trainData);
        }
        log.info("learning done");
        network.rnnTimeStep(testData.getFeatures());
        // 예측
        INDArray predicted = network.rnnTimeStep(testData.getFeatures());

        // 예측 결과 역변환
        scaler.revert(trainData);
        scaler.revert(testData);
        scaler.revertLabels(predicted);

        // 예측 결과
        List<GoldPredicted> GoldPredicteds = learnResult(predicted, testSize, goldInfoPerDay);

        // 실행 시간 로깅
        long endTime = System.currentTimeMillis();
        long estimatedTime = endTime - startTime;
        double seconds = (double) estimatedTime / 1000;
        System.out.println("Processing Time: " + seconds);

        return GoldPredicteds;
    }

    private DataSet getTrainingData(List<GoldInfo> goldInfos, int trainSize, int timeSeriesLength) {
        int numFeatures = NUM_FEATURES; // 사용할 필드 수
        int dataSize = trainSize + 1;

        if (dataSize <= 0) {
            throw new IllegalArgumentException("Not enough data to create training sequences.");
        }

        INDArray input = Nd4j.create(dataSize, numFeatures, timeSeriesLength);
        INDArray output = Nd4j.create(dataSize, numFeatures, timeSeriesLength);

        for (int i = 0; i < dataSize; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                GoldInfo goldInfo = goldInfos.get(i + j);
                input.putScalar(new int[]{i, 0, j}, goldInfo.getClpr());
                input.putScalar(new int[]{i, 1, j}, goldInfo.getVs());
                input.putScalar(new int[]{i, 2, j}, goldInfo.getFltRt());
                input.putScalar(new int[]{i, 3, j}, goldInfo.getMkp());
                input.putScalar(new int[]{i, 4, j}, goldInfo.getHipr());
                input.putScalar(new int[]{i, 5, j}, goldInfo.getLopr());
                input.putScalar(new int[]{i, 6, j}, goldInfo.getTrqu());
            }
            output.putScalar(new int[]{i, 0, 0}, goldInfos.get(i + timeSeriesLength).getClpr());
        }

        // 데이터 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        DataSet dataSet = new DataSet(input, output);
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        return dataSet;
    }

    private DataSet getTestData(List<GoldInfo> goldInfos, int testSize, int timeSeriesLength) {
        int numFeatures = NUM_FEATURES; // 사용할 필드 수
        int dataSize = testSize + 1;

        if (dataSize <= 0) {
            throw new IllegalArgumentException("Not enough data to create test sequences.");
        }

        INDArray input = Nd4j.create(dataSize, numFeatures, timeSeriesLength);
        INDArray output = Nd4j.create(dataSize, numFeatures, timeSeriesLength);
        for (int i = 0; i < dataSize; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                GoldInfo goldInfo = goldInfos.get(i + j);
                input.putScalar(new int[]{i, 0, j}, goldInfo.getClpr());
                input.putScalar(new int[]{i, 1, j}, goldInfo.getVs());
                input.putScalar(new int[]{i, 2, j}, goldInfo.getFltRt());
                input.putScalar(new int[]{i, 3, j}, goldInfo.getMkp());
                input.putScalar(new int[]{i, 4, j}, goldInfo.getHipr());
                input.putScalar(new int[]{i, 5, j}, goldInfo.getLopr());
                input.putScalar(new int[]{i, 6, j}, goldInfo.getTrqu());
            }
            output.putScalar(new int[]{i, 0, 0}, goldInfos.get(i + timeSeriesLength).getClpr());
        }

        // 데이터 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        DataSet dataSet = new DataSet(input, output);
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        return dataSet;
    }

    private List<GoldPredicted> learnResult(INDArray predicted, int futureDays, List<GoldInfo> goldInfoPerDay) throws ParseException {
        List<GoldPredicted> GoldPredicteds = new ArrayList<>();

        // 오늘 날짜를 "yyyyMMdd" 형식으로 가져오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayString = sdf.format(Calendar.getInstance().getTime());
        Date today = sdf.parse(todayString); // String을 Date 객체로 변환
        long adjust = 0;
        for (int i = goldInfoPerDay.size() - 30; i < goldInfoPerDay.size(); i++) {
            adjust += goldInfoPerDay.get(i).getClpr();
        }
        adjust /= 30;

        for (int i = 0; i < futureDays; i++) {
            // 예측된 값에 과거 데이터를 더하기
            long predictedValue = (long) predicted.getDouble(i) + adjust; // 예측값 + 과거 값

            // 예측 날짜 생성
            String predictionDate = sdf.format(DateUtils.addDays(today, i + 1)); // 예측 날짜

            // 예측 결과 추가
            GoldPredicteds.add(new GoldPredicted(predictionDate, predictedValue));
        }

        return GoldPredicteds;
    }
}