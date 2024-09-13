package fi.re.firebackend.util.goldPredict;

import fi.re.firebackend.dto.gold.GoldDiff;
import fi.re.firebackend.dto.gold.GoldInfo;
import fi.re.firebackend.dto.gold.LSTMData;
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
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class goldPriceExpectation {

    private static int size = 0;
    private static int trainSize = 0;
    private static int testSize = 0;
    private static final int N_EPOCHS = 50;
    private static final double LEARNING_RATED = 0.0015;
    private static final double MOMENTUM = 0.9;
    private static final int SEED = 1000;
    private static final long TODAY_PRICE = 108200;
    private static final Date TODAY = new Date();

    static int curdate = 20240901;
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
//        List<GoldDiff> goldTrain = resultData.getTrainData();
//        List<GoldDiff> goldTest = resultData.getTestData();
//        List<GoldDiff> goldLearn = resultData.getLearnData();
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
////        List<GoldDiff> learn = result(predicted, goldInfoPerDay, trainSize);
//        for (int i = 0; i < goldLearn.size(); i++) {
//            System.out.println("goldlearn[" + i + "]: " + goldLearn.get(i));
//        }
//
//    }

    public static LSTMData lstm(List<GoldInfo> goldInfoPerDay) throws Exception {

        long startTime = System.currentTimeMillis();

        //데이터 분류
        size = goldInfoPerDay.size();
        trainSize = ((size * 70) / 100);
        testSize = size - trainSize;
        System.out.println(size + "-" + trainSize + "-" + testSize);

        //경사에 필요한 데이터 세트
        DataSet trainData = getTrainingData(goldInfoPerDay);
        // 테스트에 필요한 데이터 세트
        DataSet testData = getTestData(goldInfoPerDay);

        // 0~1 사이로 스케일링
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fitLabel(true);
        // 척도를 위한 데이터 세트 선택
        normalizer.fit(trainData);
        // 스케일링 프로세스가 진행됩니다.
        normalizer.transform(trainData);
        normalizer.transform(testData);

        //모델 config
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder().seed(SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(LEARNING_RATED, MOMENTUM))
                .list()
                .layer(0, new LSTM.Builder().activation(Activation.TANH).nIn(1).nOut(10).build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
//                        .activation(Activation.SOFTMAX)
                        .nIn(10).nOut(1).build())
                .build();

        //모델 생성
        MultiLayerNetwork network = new MultiLayerNetwork(configuration);
        network.init();

        //에폭 수 만큼 학습 진행
        for (int i = 1; i <= N_EPOCHS; i++) {
            network.fit(trainData);
//            System.out.print(i);
            System.out.print(".");
        }
        System.out.println();

        // 학습 데이터와 예측 테스트 데이터로 rrnTimeStep를 초기화합니다.
        network.rnnTimeStep(testData.getFeatures());
        INDArray predicted = network.rnnTimeStep(testData.getFeatures());
        // Revert data back to original values for plotting

        normalizer.revert(trainData);
        normalizer.revert(testData);
        normalizer.revertLabels(predicted);
        System.out.println("current price: " + TODAY_PRICE);

        List<GoldDiff> train = trainResult(trainData.getFeatures(), goldInfoPerDay, 0);
        List<GoldDiff> test = trainResult(testData.getFeatures(), goldInfoPerDay, trainSize);
        List<GoldDiff> learn = learnResult(predicted, trainSize);
        //포장
        LSTMData result = new LSTMData(train, test, learn);

        //실행 시간 로깅
        long endTime = System.currentTimeMillis();
        long estimatedTime = endTime - startTime;
        double seconds = (double) estimatedTime / 1000;
        System.out.println("Processing Time: " + seconds);

        return result;
    }

    private static DataSet getTrainingData(List<GoldInfo> goldInfoPerDay) {
        int size = trainSize + 1;
        double[] seq = new double[size];
        double[] out = new double[size];

        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                seq[i] = goldInfoPerDay.get(i).getClpr();
                out[i] = goldInfoPerDay.get(i).getClpr();
            } else {
                seq[i] = goldInfoPerDay.get(i).getClpr();
                out[i] = goldInfoPerDay.get(i + 1).getClpr();
            }
        }

        INDArray seqNDArray = Nd4j.create(seq, new int[]{size, 1});
        INDArray inputNDArray = Nd4j.zeros(1, 1, size);
        inputNDArray.putRow(0, seqNDArray.transpose());

        INDArray outNDArray = Nd4j.create(out, new int[]{size, 1});
        INDArray outputNDArray = Nd4j.zeros(1, 1, size);
        outputNDArray.putRow(0, outNDArray.transpose());

        DataSet dataSet = new DataSet(inputNDArray, outputNDArray);
        return dataSet;
    }

    private static DataSet getTestData(List<GoldInfo> countryAllDayData) {
        int size = testSize;
        double[] seq = new double[size];
        double[] out = new double[size];

        for (int i = 0, j = trainSize; i < size; i++, j++) {
            if (i == size - 1) {
                seq[i] = countryAllDayData.get(j).getClpr();
                out[i] = countryAllDayData.get(j).getClpr();
            } else {
                seq[i] = countryAllDayData.get(j).getClpr();
                out[i] = countryAllDayData.get(j + 1).getClpr();
            }
        }

        INDArray seqNDArray = Nd4j.create(seq, new int[]{size, 1});
        INDArray inputNDArray = Nd4j.zeros(1, 1, size);
        inputNDArray.putRow(0, seqNDArray.transpose());

        INDArray outNDArray = Nd4j.create(out, new int[]{size, 1});
        INDArray outputNDArray = Nd4j.zeros(1, 1, size);
        outputNDArray.putRow(0, outNDArray.transpose());

        DataSet dataSet = new DataSet(inputNDArray, outputNDArray);
        return dataSet;
    }

//    private static List<GoldDiff> result(INDArray result, List<GoldInfo> data, int dataStart) {
//        List<GoldDiff> goldDiffs = new ArrayList<>();
//        for (int i = 0,j = dataStart; i < result.length(); i++,j++) {
//            GoldDiff goldDiff = new GoldDiff(data.get(j).getBasDt(), data.get(j).getClpr() ,TODAY_PRICE - (long) result.getDouble(i));
//            goldDiffs.add(goldDiff);
//        }
//        return goldDiffs;
//    }

    private static List<GoldDiff> learnResult(INDArray result, int dataStart) {
        List<GoldDiff> goldDiffs = new ArrayList<>();

        // dataStart부터 예측된 값을 현재 금 시세와 비교하여 차이를 계산합니다.
        for (int i = 0, j = dataStart; i < result.length(); i++, j++) {
            long predictedPrice = (long) result.getDouble(i);  // 예측된 금 시세
//            long currentPrice = data.get(j).getClpr();         // 현재 금 시세
            //현재 금 시세
            long currentPrice = TODAY_PRICE;

            // 예측된 금 시세와 현재 금 시세 간의 차이 계산
            long priceDiff = predictedPrice - currentPrice;

            Date today = TODAY;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date futureDate = calendar.getTime();
            // GoldDiff 객체 생성 및 리스트에 추가
            GoldDiff goldDiff = new GoldDiff(futureDate, predictedPrice, priceDiff);
            goldDiffs.add(goldDiff);
        }

        return goldDiffs;
    }

    private static List<GoldDiff> trainResult(INDArray result, List<GoldInfo> data, int dataStart) {
        List<GoldDiff> goldDiffs = new ArrayList<>();
        for (int i = 0, j = dataStart; i < result.length(); i++, j++) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            try {
                // 문자열을 Date 객체로 변환
                date = formatter.parse(Integer.toString(data.get(j).getBasDt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long currentPrice = data.get(j).getClpr();
            long predictedPrice = (long) result.getDouble(i);
            long priceDiff = predictedPrice - currentPrice;
            System.out.println("day : " + data.get(j).getBasDt() + " curPrice : " + currentPrice + " predictPrice : " + predictedPrice);
            GoldDiff goldDiff = new GoldDiff(date, predictedPrice, priceDiff);
            goldDiffs.add(goldDiff);
        }

        return goldDiffs;
    }


    public static GoldInfo generateGoldInfo() {
        Random random = new Random();
        GoldInfo goldInfo = new GoldInfo();

        // 랜덤 값 생성 범위 설정
//        int baseDt = 20240901 + random.nextInt(30); // 108000에서 108200 사이의 값
        curdate++;
        int baseDt = curdate;
        int clpr = 107000 + random.nextInt(2000);       // 100에서 120 사이의 값
        int vs = -100 + random.nextInt(200);         // 100에서 120 사이의 값
        double fltRt = 0.1 * random.nextDouble() * 10; // 15000에서 16000 사이의 값
        int mkp = 107000 + random.nextInt(2000);   // 107000에서 109000 사이의 값
        int hipr = 108000 + random.nextInt(800);   // 고가 108000~108800
        int lopr = 107500 + random.nextInt(800);   // 저가 107500~108300
        int trqu = 1000 + random.nextInt(10000);  // 거래량 100~11000

        // VO 설정
        goldInfo.setBasDt(baseDt);
        goldInfo.setClpr(clpr);
        goldInfo.setVs(vs);
        goldInfo.setFltRt(fltRt);
        goldInfo.setMkp(mkp);
        goldInfo.setHipr(hipr);
        goldInfo.setLopr(lopr);
        goldInfo.setTrqu(trqu);

        return goldInfo;
    }
}


