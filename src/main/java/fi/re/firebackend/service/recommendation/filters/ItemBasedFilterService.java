package fi.re.firebackend.service.recommendation.filters;

import fi.re.firebackend.dto.recommendation.filtering.DepositVo;
import fi.re.firebackend.dto.recommendation.FundEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ItemBasedFilterService {

    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP); // Precision for BigDecimal

    // Cosine Similarity calculation using BigDecimal
    private BigDecimal cosineSimilarity(BigDecimal[] vec1, BigDecimal[] vec2) {
        BigDecimal dotProduct = BigDecimal.ZERO;
        BigDecimal normA = BigDecimal.ZERO;
        BigDecimal normB = BigDecimal.ZERO;

        // Calculate dot product and norms
        for (int i = 0; i < vec1.length; i++) {
            dotProduct = dotProduct.add(vec1[i].multiply(vec2[i], MATH_CONTEXT));
            normA = normA.add(vec1[i].pow(2), MATH_CONTEXT);
            normB = normB.add(vec2[i].pow(2), MATH_CONTEXT);
        }

        // Calculate square root of norms
        BigDecimal normASqrt = normA.sqrt(MATH_CONTEXT);
        BigDecimal normBSqrt = normB.sqrt(MATH_CONTEXT);

        // Denominator of cosine similarity
        BigDecimal denominator = normASqrt.multiply(normBSqrt, MATH_CONTEXT);

        // Return 0 if the denominator is 0 to avoid division by zero
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate and return cosine similarity
        return dotProduct.divide(denominator, MATH_CONTEXT);
    }

    // Recommend the most similar Deposit based on cosine similarity
    public DepositVo recmdDeposits(DepositVo targetProduct, List<DepositVo> allProducts) {
        DepositVo bestMatch = null;
        BigDecimal highestSimilarity = BigDecimal.valueOf(-1); // Initialize with a low value

        for (DepositVo product : allProducts) {
            if (!product.equals(targetProduct)) { // Skip comparison with itself
                BigDecimal similarity = cosineSimilarity(
                        new BigDecimal[] {
                                product.getDepositEntity().getMinRate(),
                                product.getDepositEntity().getMaxRate()
                        },
                        new BigDecimal[] {
                                targetProduct.getDepositEntity().getMinRate(),
                                targetProduct.getDepositEntity().getMaxRate()
                        }
                );

                // Update the best match if a higher similarity is found
                if (similarity.compareTo(highestSimilarity) > 0) {
                    highestSimilarity = similarity;
                    bestMatch = product;
                }
            }
        }

        return bestMatch; // Return the best matching deposit
    }

    // Recommend the most similar Fund based on cosine similarity
    public FundEntity recmdFund(FundEntity targetFund, List<FundEntity> allFunds) {
        FundEntity bestMatch = null;
        BigDecimal highestSimilarity = BigDecimal.valueOf(-1); // Initialize with a low value

        for (FundEntity fund : allFunds) {
            if (!fund.equals(targetFund)) { // Skip comparison with itself
                BigDecimal similarity = cosineSimilarity(
                        new BigDecimal[] {
                                fund.getRate(),
                                BigDecimal.valueOf(fund.getDngrGrade())
                        },
                        new BigDecimal[] {
                                targetFund.getRate(),
                                BigDecimal.valueOf(targetFund.getDngrGrade())
                        }
                );

                // Update the best match if a higher similarity is found
                if (similarity.compareTo(highestSimilarity) > 0) {
                    highestSimilarity = similarity;
                    bestMatch = fund;
                }
            }
        }

        return bestMatch; // Return the best matching fund
    }
}
