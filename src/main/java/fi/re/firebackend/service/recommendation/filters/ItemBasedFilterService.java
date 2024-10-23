package fi.re.firebackend.service.recommendation.filters;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.recommendation.vo.ProcessedSavingsDepositVo;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//https://commons.apache.org/sandbox/commons-text/jacoco/org.apache.commons.text.similarity/CosineSimilarity.java.html
@Component
public class ItemBasedFilterService {
    // 코사인 유사도를 구하는 메서드
    public double cosineSimilarity(final double[] vec1, final double[] vec2) {
        if (vec1 == null || vec2 == null || vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vectors must not be null and must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);

        return denominator == 0 ? 0.0 : dotProduct / denominator;
    }

    // Deposit에 대한 유사도 추천
    public List<ProcessedSavingsDepositVo> filteringSavingsDeposits(final List<ProcessedSavingsDepositVo> topThreeProducts, final List<ProcessedSavingsDepositVo> allProducts) {
        return calculateSimilarities(topThreeProducts, allProducts, this::calculateDepositSimilarity);
    }

    // Fund에 대한 유사도 추천
    public List<FundDto> filteredFunds(final List<FundDto> topThreeProducts, final List<FundDto> allFunds) {
        return calculateSimilarities(topThreeProducts, allFunds, this::calculateFundSimilarity);
    }

    private <T> List<T> calculateSimilarities(List<T> targetProducts, List<T> allProducts, SimilarityCalculator<T> calculator) {
        List<Similarity<T>> similarities = new ArrayList<>();

        for (T targetProduct : targetProducts) {
            for (T product : allProducts) {
                if (!product.equals(targetProduct)) {
                    double similarity = calculator.calculate(targetProduct, product);
                    similarities.add(new Similarity<>(product, similarity));
                }
            }
        }

        similarities.sort((s1, s2) -> Double.compare(s2.getSimilarity(), s1.getSimilarity()));

        return similarities.parallelStream()
                .map(Similarity::getItem)
                .collect(Collectors.toList());
    }

    private double calculateDepositSimilarity(final ProcessedSavingsDepositVo product1, final ProcessedSavingsDepositVo product2) {
        return calculateSimilarity(product1.getIntrRate(), product1.getIntrRate2(),
                product2.getIntrRate(), product2.getIntrRate2());
    }

    private double calculateFundSimilarity(final FundDto fund1, final FundDto fund2) {
        return calculateSimilarity(
                fund1.getRate(),
                fund1.getDngrGrade(),
                fund2.getRate(),
                fund2.getDngrGrade(),
                fund1.getSixMRate(),
                fund1.getOneYRate(),
                fund2.getSixMRate(),
                fund2.getOneYRate()
        );
    }

    private double calculateSimilarity(double... values) {
        return cosineSimilarity(new double[]{values[0], values[1]}, new double[]{values[2], values[3]});
    }

    @Getter
    private static class Similarity<T> {
        private final T item;
        private final double similarity;

        public Similarity(T item, double similarity) {
            this.item = item;
            this.similarity = similarity;
        }
    }

    @FunctionalInterface
    private interface SimilarityCalculator<T> {
        double calculate(T product1, T product2);
    }
}
