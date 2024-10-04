package fi.re.firebackend.util.goldPredict;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.GoldPredicted;
import org.apache.commons.lang3.time.DateUtils;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.layers.recurrent.SimpleRnn;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
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

    //    @Autowired
    private GoldDao goldDao;

    private static final int N_EPOCHS = 300;
    private static final double LEARNING_RATE = 0.005;
    private static final double MOMENTUM = 0.9;
    private static final int SEED = 1000;
    private static final int NUM_FEATURES = 7;

    GoldPriceExpectation(GoldDao goldDao) {
        this.goldDao = goldDao;
    }

    public List<GoldPredicted> lstm(List<GoldInfo> goldInfoPerDay) throws Exception {
        long startTime = System.currentTimeMillis();

        // 전체 데이터 크기 확인
        int size = goldInfoPerDay.size();
        // 최근 1년 데이터의 크기
        int trainSize = Math.min(size, 365); // 데이터가 365일 미만일 경우를 고려
        System.out.println("Total size: " + size + ", Training size: " + trainSize);

        // 최근 1년의 데이터만 사용
        List<GoldInfo> recentGoldData = goldInfoPerDay.subList(size - trainSize, size);

        // 학습 및 테스트 데이터 생성
        DataSet trainData = getTrainingData(recentGoldData, trainSize, 1);
        int testSize = Math.max(0, size - trainSize); // 테스트 데이터 크기 계산
        DataSet testData = getTestData(goldInfoPerDay, testSize, 1); // 테스트 데이터는 전체에서 가져옴

        // 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        scaler.fitLabel(true);
        scaler.fit(trainData);
        scaler.transform(trainData);
        scaler.transform(testData);

        // 모델 구성
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(LEARNING_RATE, MOMENTUM))
                .list()
                .layer(0, new GravesLSTM.Builder()
                        .activation(Activation.SIGMOID)
                        .nIn(NUM_FEATURES)
                        .nOut(100) // 증가된 노드 수
                        .build())
                .layer(1, new GravesLSTM.Builder() // 추가 LSTM 레이어
                        .activation(Activation.SIGMOID)
                        .nIn(100)
                        .nOut(50)
                        .build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(50)
                        .nOut(NUM_FEATURES)
                        .build())
                .build();

        // 모델 생성 및 학습
        MultiLayerNetwork network = new MultiLayerNetwork(config);
        network.init();

        // 모델 학습
        for (int i = 1; i <= N_EPOCHS; i++) {
            for (int j = 0; j < trainData.numExamples(); j++) {
                // 3D 배열에서 2D 배열로 변환
                INDArray currentInput = trainData.getFeatures().get(NDArrayIndex.point(j), NDArrayIndex.all(), NDArrayIndex.all());
                INDArray currentLabel = trainData.getLabels().get(NDArrayIndex.point(j), NDArrayIndex.all(), NDArrayIndex.point(0));

                // 3D 배열로 변환
                INDArray input3D = currentInput.reshape(1, NUM_FEATURES, 1); // (1, 피처 수, 1)
                INDArray label3D = currentLabel.reshape(1, NUM_FEATURES, 1); // (1, 피처 수, 1)

                // 손실을 계산
                double loss = network.score(new DataSet(input3D, label3D));

                // 모델 학습
                network.fit(new DataSet(input3D, label3D));
            }
            System.out.print(".");
        }
        System.out.println();

        // 예측
        INDArray predicted = network.rnnTimeStep(testData.getFeatures());

        // 예측 결과 역변환
        scaler.revert(trainData);
        scaler.revert(testData);
        scaler.revertLabels(predicted);

        // 예측 결과
        List<GoldPredicted> GoldPredicteds = learnResult(predicted, testSize);

        // 실행 시간 로깅
        long endTime = System.currentTimeMillis();
        long estimatedTime = endTime - startTime;
        double seconds = (double) estimatedTime / 1000;
        System.out.println("Processing Time: " + seconds);

        return GoldPredicteds;
    }

    private DataSet getTrainingData(List<GoldInfo> goldInfos, int trainSize, int timeSeriesLength) {
        int numFeatures = NUM_FEATURES; // 사용할 필드 수
        int dataSize = trainSize; // dataSize는 trainSize와 같음

        if (dataSize <= 0 || dataSize <= timeSeriesLength) {
            throw new IllegalArgumentException("Not enough data to create training sequences.");
        }

        INDArray input = Nd4j.create(dataSize, numFeatures, timeSeriesLength);
        INDArray output = Nd4j.create(dataSize, numFeatures, timeSeriesLength);

        for (int i = 0; i < dataSize; i++) {
            if (i + timeSeriesLength - 1 >= goldInfos.size()) {
                throw new IllegalArgumentException("Not enough data to create training sequences.");
            }

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
            output.putScalar(new int[]{i, 0, 0}, goldInfos.get(i + timeSeriesLength - 1).getClpr()); // 수정: -1로 변경
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
        int dataSize = testSize; // dataSize는 testSize와 같음

        if (dataSize <= 0 || dataSize <= timeSeriesLength) {
            throw new IllegalArgumentException("Not enough data to create test sequences.");
        }

        INDArray input = Nd4j.create(dataSize, numFeatures, timeSeriesLength);
        INDArray output = Nd4j.create(dataSize, numFeatures, timeSeriesLength);

        for (int i = 0; i < dataSize; i++) {
            if (i + timeSeriesLength - 1 >= goldInfos.size()) {
                throw new IllegalArgumentException("Not enough data to create test sequences.");
            }

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
            output.putScalar(new int[]{i, 0, 0}, goldInfos.get(i + timeSeriesLength - 1).getClpr()); // 예측할 값
        }

        // 데이터 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        DataSet dataSet = new DataSet(input, output);
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        return dataSet;
    }

    private List<GoldPredicted> learnResult(INDArray predicted, int futureDays) throws ParseException {
        List<GoldPredicted> GoldPredicteds = new ArrayList<>();

        // 오늘 날짜를 "yyyyMMdd" 형식으로 가져오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayString = sdf.format(Calendar.getInstance().getTime());
        Date today = sdf.parse(todayString); // String을 Date 객체로 변환

        for (int i = 0; i < futureDays; i++) {
            // 예측된 값이 배열의 범위를 초과하지 않도록 확인
            if (i >= predicted.size(0)) {
                throw new IllegalArgumentException("Predicted array index out of bounds.");
            }

            long predictedValue = (long) predicted.getDouble(i); // 예측된 값
            String predictionDate = sdf.format(DateUtils.addDays(today, i + 1)); // 예측 날짜

            GoldPredicteds.add(new GoldPredicted(predictionDate, predictedValue));
        }

        return GoldPredicteds;
    }
}
