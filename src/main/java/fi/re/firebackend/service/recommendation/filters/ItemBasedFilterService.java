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

    // 코사인 유사도를 구하는 메서드
    public BigDecimal cosineSimilarity(BigDecimal[] vec1, BigDecimal[] vec2) {
        if (vec1 == null || vec2 == null || vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must not be null and must have the same length");
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

        return (denominator.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO : dotProduct.divide(denominator, MATH_CONTEXT);
    }

    // selectCount 상위 10개 상품을 기준으로 예적금 유사도 계산
    public List<DepositVo> recmdDepositsBySelectCount(List<DepositVo> allProducts) {
        List<DepositVo> topProducts = allProducts.stream()
                .sorted((p1, p2) -> p2.getDepositEntity().getSelectCount() - p1.getDepositEntity().getSelectCount())
                .limit(10)
                .collect(Collectors.toList());

        return recmdDeposits(topProducts, allProducts);
    }

    // selectCount 상위 10개 펀드를 기준으로 유사도 계산
    public List<FundVo> recmdFundsBySelectCount(List<FundVo> allFunds) {
        // 수익률 기준으로 상위 10개 펀드 선택
        List<FundVo> topFunds = allFunds.stream()
                .sorted((f1, f2) -> f2.getRate().compareTo(f1.getRate())) // BigDecimal 비교는 compareTo 사용
                .limit(10)
                .collect(Collectors.toList());

        // 선택된 상위 10개 펀드와 전체 펀드를 비교하여 유사한 펀드 추천
        return recmdFunds(topFunds, allFunds);
    }


    // 이율 및 금액을 이용한 예적금 유사도 계산
    public List<DepositVo> recmdDeposits(List<DepositVo> targetProducts, List<DepositVo> allProducts) {
        List<Similarity<DepositVo>> similarities = new ArrayList<>();

        for (DepositVo targetProduct : targetProducts) {
            for (DepositVo product : allProducts) {
                boolean alreadyAdded = similarities.stream()
                        .anyMatch(similarity -> similarity.getItem().equals(product));

                if (!alreadyAdded) { // 이미 포함된 항목은 pass
                    BigDecimal similarity = calculateDepositSimilarity(product, targetProduct);

                    // 유사도 정보를 저장
                    similarities.add(new Similarity<>(product, similarity));
                }
            }
        }


        // 유사도 높은 순서로 정렬
        similarities.sort((s1, s2) -> s2.getSimilarity().compareTo(s1.getSimilarity()));

        // 추천된 예적금 리스트 생성
        return similarities.stream()
                .map(Similarity::getItem)
                .collect(Collectors.toList());
    }

    // 이율 및 금액을 기반으로 예적금 유사도 계산
    private BigDecimal calculateDepositSimilarity(DepositVo product1, DepositVo product2) {
        // 이율 유사도 계산
        BigDecimal rateSimilarity = cosineSimilarity(
                new BigDecimal[]{
                        product1.getDepositEntity().getMinRate(),
                        product1.getDepositEntity().getMaxRate()
                },
                new BigDecimal[]{
                        product2.getDepositEntity().getMinRate(),
                        product2.getDepositEntity().getMaxRate()
                }
        );

        // 최소 및 최대 금액 유사도 계산
        BigDecimal amountSimilarity = cosineSimilarity(
                new BigDecimal[]{
                        product1.getMinAmount(),
                        product1.getMaxAmount()
                },
                new BigDecimal[]{
                        product2.getMinAmount(),
                        product2.getMaxAmount()
                }
        );

        // 유사도 평균 계산
        return rateSimilarity.add(amountSimilarity).divide(BigDecimal.valueOf(2), MATH_CONTEXT);
    }

    // 수익률 및 위험 등급을 이용한 펀드 유사도 계산
    public List<FundVo> recmdFunds(List<FundVo> targetFunds, List<FundVo> allFunds) {
        // 유사도를 저장할 리스트 초기화
        List<Similarity<FundVo>> similarities = new ArrayList<>();

        for (FundVo targetFund : targetFunds) {
            for (FundVo fund : allFunds) {
                if (!fund.equals(targetFund)) {
                    // 펀드 간 유사도 계산
                    BigDecimal similarity = calculateFundSimilarity(fund, targetFund);

                    // 유사도 정보 저장
                    similarities.add(new Similarity<>(fund, similarity));
                }
            }
        }

        // 정렬
        similarities.sort((s1, s2) -> s2.getSimilarity().compareTo(s1.getSimilarity()));

        return similarities.stream()
                .map(Similarity::getItem)
                .collect(Collectors.toList());
    }

    // 수익률 및 위험 등급을 기반으로 펀드 유사도 계산
    private BigDecimal calculateFundSimilarity(FundVo fund1, FundVo fund2) {
        return cosineSimilarity(
                new BigDecimal[]{
                        fund1.getRate(),
                        BigDecimal.valueOf(fund1.getDngrGrade())
                },
                new BigDecimal[]{
                        fund2.getRate(),
                        BigDecimal.valueOf(fund2.getDngrGrade())
                }
        );
    }

    // 제네릭 유사도 클래스로 중복 코드 감소
    private static class Similarity<T> {
        private final T item;
        private final BigDecimal similarity;

        public Similarity(T item, BigDecimal similarity) {
            this.item = item;
            this.similarity = similarity;
        }

        public T getItem() {
            return item;
        }

        public BigDecimal getSimilarity() {
            return similarity;
        }
    }
}
