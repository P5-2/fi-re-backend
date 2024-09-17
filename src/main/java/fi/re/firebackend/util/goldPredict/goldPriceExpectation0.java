package fi.re.firebackend.util.goldPredict;

import fi.re.firebackend.dao.gold.GoldDao;
import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.dto.gold.GoldInfo;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class goldPriceExpectation0 {
    @Autowired
    private static GoldDao goldDao;

    private static int size = 0;
    private static int trainSize = 0;
    private static int testSize = 0;
    private static final int N_EPOCHS = 50;
    private static final double LEARNING_RATE = 0.0015;
    private static final double MOMENTUM = 0.9;
    private static final int SEED = 1000;


//
//    public static void main(String[] args) throws Exception {
//        //main은 현재 service의 역할
//
//        //오늘의 금 값
////        TODAY_PRICE = 108200;
//        //데이터 삽입해주는 역할
//        // 데이터 생성
//        List<GoldInfo> goldInfoList = new ArrayList<>();
//        //데이터 선언
//        for (int i = 0; i < N_EPOCHS; i++) {
//            GoldInfo info = generateGoldInfo();
//            goldInfoList.add(info);
//        }
//
//        for (int i = 0; i < goldInfoList.size(); i++) {
//            GoldInfo info = goldInfoList.get(i);
//            System.out.println(info);
//        }
//
//        //데이터 삽입
//        LSTMData resultData = lstm(goldInfoList);
//        List<GoldPredicted> goldTrain = resultData.getTrainData();
//        List<GoldPredicted> goldTest = resultData.getTestData();
//        List<GoldPredicted> goldLearn = resultData.getLearnData();
//
//        for (int i = 0; i < goldTrain.size(); i++) {
//            System.out.println("goldtrain[" + i + "]: " + goldTrain.get(i));
//        }
//        for (int i = 0; i < goldTest.size(); i++) {
//            System.out.println("goldtest[" + i + "]: " + goldTest.get(i));
//        }
////        for(int i = 0; i<goldLearn.size(); i++){
////            System.out.println("goldlearn["+i+"]: "+goldLearn.get(i));
////        }
//        System.out.println("success");
//
//
//        //결과 출력
////        List<GoldPredicted> learn = result(predicted, goldInfoPerDay, trainSize);
//        for (int i = 0; i < goldLearn.size(); i++) {
//            System.out.println("goldlearn[" + i + "]: " + goldLearn.get(i));
//        }
//
//    }

    public static List<GoldPredicted> lstm(List<GoldInfo> goldInfoPerDay) throws Exception {
        long startTime = System.currentTimeMillis();

        // 데이터 분류
        int size = goldInfoPerDay.size();
        int trainSize = (int) (size * 0.7); // 70% 데이터는 훈련용
        int testSize = size - trainSize;
        System.out.println(size + "-" + trainSize + "-" + testSize);
        // 학습 및 테스트 데이터 생성
        DataSet trainData = getTrainingData(goldInfoPerDay, trainSize, 1);
        DataSet testData = getTestData(goldInfoPerDay, testSize, 1);


        // 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        scaler.fit(trainData);
        scaler.transform(trainData);
        scaler.transform(testData);

        // 모델 구성
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(LEARNING_RATE, MOMENTUM))
                .list()
                .layer(0, new LSTM.Builder()
                        .activation(Activation.TANH)
                        .nIn(7)  // input features (number of features used)
                        .nOut(10)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(10)
                        .nOut(1)
                        .build())
                .build();

        // 모델 생성 및 학습
        MultiLayerNetwork network = new MultiLayerNetwork(configuration);
        network.init();

        // 모델 학습
        for (int i = 1; i <= N_EPOCHS; i++) {
            network.fit(trainData);
            System.out.print(".");
        }
        System.out.println();

        // 예측
        INDArray predicted = network.rnnTimeStep(testData.getFeatures());

//        // 정규화 되돌리기
//        scaler.revert(trainData);
//        scaler.revert(testData);
//        scaler.revertLabels(predicted);

        // 예측 결과
        List<GoldPredicted> GoldPredicteds = learnResult(predicted, trainSize);

        // 실행 시간 로깅
        long endTime = System.currentTimeMillis();
        long estimatedTime = endTime - startTime;
        double seconds = (double) estimatedTime / 1000;
        System.out.println("Processing Time: " + seconds);

        return GoldPredicteds;
    }

//    private static DataSet getTrainingData(List<GoldInfo> goldInfos, int trainSize) {
//        int numFeatures = 7; // 사용할 필드 수
//        int dataSize = trainSize;
//
//        if (dataSize == 0) {
//            throw new IllegalArgumentException("No training data available.");
//        }
//
//        INDArray input = Nd4j.zeros(dataSize, numFeatures);
//        INDArray output = Nd4j.zeros(dataSize, 1);
//
//        for (int i = 0; i < dataSize; i++) {
//            GoldInfo goldInfo = goldInfos.get(i);
//            input.putScalar(new int[]{i, 0}, goldInfo.getClpr());
//            input.putScalar(new int[]{i, 1}, goldInfo.getVs());
//            input.putScalar(new int[]{i, 2}, goldInfo.getFltRt());
//            input.putScalar(new int[]{i, 3}, goldInfo.getMkp());
//            input.putScalar(new int[]{i, 4}, goldInfo.getHipr());
//            input.putScalar(new int[]{i, 5}, goldInfo.getLopr());
//            input.putScalar(new int[]{i, 6}, goldInfo.getTrqu());
//
//            if (i < dataSize - 1) {
//                output.putScalar(i, goldInfos.get(i + 1).getClpr());
//            } else {
//                output.putScalar(i, goldInfo.getClpr()); // 마지막 값의 경우 현재 값으로 설정
//            }
//        }
//
//        // 데이터 정규화
//        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
//        DataSet dataSet = new DataSet(input, output);
//        scaler.fit(dataSet);
//        scaler.transform(dataSet);
//
//        System.out.println("getTrainingData: " + dataSet);
//        return dataSet;
//    }
//    private static DataSet getTestData(List<GoldInfo> goldInfos, int testSize) {
//        int numFeatures = 7; // 사용할 필드 수
//        int dataSize = testSize;
//
//        if (dataSize == 0) {
//            throw new IllegalArgumentException("No test data available.");
//        }
//
//        INDArray input = Nd4j.zeros(dataSize, numFeatures);
//        INDArray output = Nd4j.zeros(dataSize, 1);
//
//        for (int i = 0; i < dataSize; i++) {
//            GoldInfo goldInfo = goldInfos.get(trainSize + i); // 학습 데이터 이후의 데이터 사용
//
//            input.putScalar(new int[]{i, 0}, goldInfo.getClpr());
//            input.putScalar(new int[]{i, 1}, goldInfo.getVs());
//            input.putScalar(new int[]{i, 2}, goldInfo.getFltRt());
//            input.putScalar(new int[]{i, 3}, goldInfo.getMkp());
//            input.putScalar(new int[]{i, 4}, goldInfo.getHipr());
//            input.putScalar(new int[]{i, 5}, goldInfo.getLopr());
//            input.putScalar(new int[]{i, 6}, goldInfo.getTrqu());
//
//            if (i < dataSize - 1) {
//                output.putScalar(i, goldInfos.get(trainSize + i + 1).getClpr());
//            } else {
//                output.putScalar(i, goldInfo.getClpr()); // 마지막 값의 경우 현재 값으로 설정
//            }
//        }
//
//        // 데이터 정규화
//        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
//        DataSet dataSet = new DataSet(input, output);
//        scaler.fit(dataSet);
//        scaler.transform(dataSet);
//
//        System.out.println("getTestData: " + dataSet);
//        return dataSet;
//    }

    private static DataSet getTrainingData(List<GoldInfo> goldInfos, int trainSize, int timeSeriesLength) {
        int numFeatures = 7; // 사용할 필드 수
        int dataSize = trainSize - timeSeriesLength;

        if (dataSize <= 0) {
            throw new IllegalArgumentException("Not enough data to create training sequences.");
        }

        INDArray input = Nd4j.create(dataSize, timeSeriesLength, numFeatures);
        INDArray output = Nd4j.create(dataSize, numFeatures);

        for (int i = 0; i < dataSize; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                GoldInfo goldInfo = goldInfos.get(i + j);
                input.putScalar(new int[]{i, j, 0}, goldInfo.getClpr());
                input.putScalar(new int[]{i, j, 1}, goldInfo.getVs());
                input.putScalar(new int[]{i, j, 2}, goldInfo.getFltRt());
                input.putScalar(new int[]{i, j, 3}, goldInfo.getMkp());
                input.putScalar(new int[]{i, j, 4}, goldInfo.getHipr());
                input.putScalar(new int[]{i, j, 5}, goldInfo.getLopr());
                input.putScalar(new int[]{i, j, 6}, goldInfo.getTrqu());
            }
            if (i + timeSeriesLength < trainSize) {
                output.putScalar(i, goldInfos.get(i + timeSeriesLength).getClpr());
            } else {
                output.putScalar(i, goldInfos.get(i + timeSeriesLength - 1).getClpr());
            }
        }

        // 데이터 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        DataSet dataSet = new DataSet(input, output);
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        System.out.println("getTrainingData: " + dataSet);
        return dataSet;
    }

    private static DataSet getTestData(List<GoldInfo> goldInfos, int testSize, int timeSeriesLength) {
        int numFeatures = 7; // 사용할 필드 수
        int dataSize = testSize - timeSeriesLength + 1;

        if (dataSize <= 0) {
            throw new IllegalArgumentException("Not enough data to create test sequences.");
        }

        INDArray input = Nd4j.create(numFeatures, timeSeriesLength, dataSize);
        INDArray output = Nd4j.create(dataSize, 1);

        for (int i = 0; i < dataSize; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                GoldInfo goldInfo = goldInfos.get(i + j);
                input.putScalar(new int[]{i, j, 0}, goldInfo.getClpr());
                input.putScalar(new int[]{i, j, 1}, goldInfo.getVs());
                input.putScalar(new int[]{i, j, 2}, goldInfo.getFltRt());
                input.putScalar(new int[]{i, j, 3}, goldInfo.getMkp());
                input.putScalar(new int[]{i, j, 4}, goldInfo.getHipr());
                input.putScalar(new int[]{i, j, 5}, goldInfo.getLopr());
                input.putScalar(new int[]{i, j, 6}, goldInfo.getTrqu());
            }
            if (i + timeSeriesLength < testSize) {
                output.putScalar(i, goldInfos.get(i + timeSeriesLength).getClpr());
            } else {
                output.putScalar(i, goldInfos.get(i + timeSeriesLength - 1).getClpr());
            }
        }

        // 데이터 정규화
        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        DataSet dataSet = new DataSet(input, output);
        scaler.fit(dataSet);
        scaler.transform(dataSet);

        System.out.println("getTestData: " + dataSet);
        return dataSet;
    }


    private static List<GoldPredicted> learnResult(INDArray predicted, int dataStart) throws ParseException {
        List<GoldPredicted> GoldPredicteds = new ArrayList<>();

        // 오늘 날짜를 "yyyyMMdd" 형식으로 가져오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayString = sdf.format(Calendar.getInstance().getTime());
        Date today = sdf.parse(todayString); // String을 Date 객체로 변환


        for (int i = 0; i < predicted.length(); i++) {
            long predictedPrice = (long) predicted.getDouble(i);  // 예측된 금 시세

            // 날짜 계산 (오늘부터 i일 뒤)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_MONTH, i);  // i일 뒤의 날짜로 설정
            Date futureDate = calendar.getTime();    // 예측 날짜

            // GoldPredicted 객체 생성 및 리스트에 추가
//            GoldPredicted GoldPredicted = new GoldPredicted(futureDate, predictedPrice);
//            GoldPredicteds.add(GoldPredicted);
        }

        return GoldPredicteds;
    }


//
//    private static List<GoldPredicted> trainResult(INDArray result, List<GoldInfo> data, int dataStart) {
//        List<GoldPredicted> GoldPredicteds = new ArrayList<>();
//        for (int i = 0, j = dataStart; i < result.length(); i++, j++) {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//            Date date = new Date();
//            try {
//                // 문자열을 Date 객체로 변환
//                date = formatter.parse(Integer.toString(data.get(j).getBasDt()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            long currentPrice = data.get(j).getClpr();
//            long predictedPrice = (long) result.getDouble(i);
//            long priceDiff = predictedPrice - currentPrice;
//            System.out.println("day : " + data.get(j).getBasDt() + " curPrice : " + currentPrice + " predictPrice : " + predictedPrice);
//            GoldPredicted GoldPredicted = new GoldPredicted(date, predictedPrice, priceDiff);
//            GoldPredicteds.add(GoldPredicted);
//        }
//
//        return GoldPredicteds;
//    }


    // DB에서 날짜 범위 내의 금값 데이터를 가져와서 반환하는 함수
    public List<GoldInfo> generateGoldInfo(String startDate, String endDate) {
        return goldDao.getGoldInfoInPeriod(startDate, endDate);
    }

}


