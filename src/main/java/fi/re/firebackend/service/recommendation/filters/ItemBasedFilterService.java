package fi.re.firebackend.service.recommendation.filters;

import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.filtering.FundVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemBasedFilterService {
    //https://commons.apache.org/sandbox/commons-text/jacoco/org.apache.commons.text.similarity/CosineSimilarity.java.html
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

    // 두 벡터의 코사인 유사도를 구하는 메서드
    public BigDecimal cosineSimilarity(BigDecimal[] vec1, BigDecimal[] vec2) {
        if (vec1 == null || vec2 == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }

        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        BigDecimal dotProduct = BigDecimal.ZERO;
        BigDecimal normA = BigDecimal.ZERO;
        BigDecimal normB = BigDecimal.ZERO;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct = dotProduct.add(vec1[i].multiply(vec2[i], MATH_CONTEXT));
            normA = normA.add(vec1[i].pow(2, MATH_CONTEXT), MATH_CONTEXT);
            normB = normB.add(vec2[i].pow(2, MATH_CONTEXT), MATH_CONTEXT);
        }

        BigDecimal normASqrt = normA.sqrt(MATH_CONTEXT);
        BigDecimal normBSqrt = normB.sqrt(MATH_CONTEXT);
        BigDecimal denominator = normASqrt.multiply(normBSqrt, MATH_CONTEXT);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return dotProduct.divide(denominator, MATH_CONTEXT);
    }

    // 이율을 이용한 유사도 구하기(추후 딥러닝으로 대체해야할듯)
    public List<DepositVo> recmdDeposits(List<DepositVo> targetProducts, List<DepositVo> allProducts) {
        List<DepositSimilarity> similarities = new ArrayList<>();

        for (DepositVo targetProduct : targetProducts) {
            for (DepositVo product : allProducts) {
                if (!product.equals(targetProduct)) { // 자기 자신과 비교 pass
                    BigDecimal similarity = cosineSimilarity(
                            new BigDecimal[]{
                                    product.getDepositEntity().getMinRate(),
                                    product.getDepositEntity().getMaxRate()
                            },
                            new BigDecimal[]{
                                    targetProduct.getDepositEntity().getMinRate(),
                                    targetProduct.getDepositEntity().getMaxRate()
                            }
                    );

                    // 유사도 정보를 저장
                    similarities.add(new DepositSimilarity(product, similarity));
                }
            }
        }

        // 유사도 높은 순서로 정렬
        similarities.sort((s1, s2) -> s2.getSimilarity().compareTo(s1.getSimilarity()));

        // 추천된 예적금 리스트 생성
        return similarities.stream()
                .map(DepositSimilarity::getDepositVo)
                .collect(Collectors.toList());
    }


    // 수익률을 이용한 유사도 구하기(추후 딥러닝으로 대체해야할듯)
    public List<FundVo> recmdFunds(List<FundVo> targetFunds, List<FundVo> allFunds) {
        List<FundSimilarity> similarities = new ArrayList<>();

        for (FundVo targetFund : targetFunds) {
            for (FundVo fund : allFunds) {
                if (!fund.equals(targetFund)) { // 자기 자신과 비교 pass
                    BigDecimal similarity = cosineSimilarity(
                            new BigDecimal[]{
                                    fund.getRate(),
                                    BigDecimal.valueOf(fund.getDngrGrade())
                            },
                            new BigDecimal[]{
                                    targetFund.getRate(),
                                    BigDecimal.valueOf(targetFund.getDngrGrade())
                            }
                    );

                    // 유사도 정보를 FundSimilarity에 저장
                    similarities.add(new FundSimilarity(fund, similarity));
                }
            }
        }

        // 유사도 높은 순서로 정렬
        similarities.sort((s1, s2) -> s2.getSimilarity().compareTo(s1.getSimilarity()));

        // 추천된 펀드 리스트 생성
        return similarities.stream()
                .map(FundSimilarity::getFundVo)
                .collect(Collectors.toList());
    }


    // 유사도 정보를 저장할 예적금 유사도 내부 클래스
    private static class DepositSimilarity {
        private final DepositVo depositVo;
        private final BigDecimal similarity;

        public DepositSimilarity(DepositVo depositVo, BigDecimal similarity) {
            this.depositVo = depositVo;
            this.similarity = similarity;
        }

        public DepositVo getDepositVo() {
            return depositVo;
        }

        public BigDecimal getSimilarity() {
            return similarity;
        }
    }

    // 유사도 정보를 저장할 펀드 유사도 내부 클래스
    private static class FundSimilarity {
        private final FundVo fundVo;
        private final BigDecimal similarity;

        public FundSimilarity(FundVo fundVo, BigDecimal similarity) {
            this.fundVo = fundVo;
            this.similarity = similarity;
        }

        public FundVo getFundVo() {
            return fundVo;
        }

        public BigDecimal getSimilarity() {
            return similarity;
        }
    }

}
