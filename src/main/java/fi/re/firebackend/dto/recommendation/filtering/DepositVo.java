package fi.re.firebackend.dto.recommendation.filtering;

import fi.re.firebackend.dto.recommendation.DepositEntity;
import fi.re.firebackend.util.recommendUtil.KeywordExtractor;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositVo {
    private DepositEntity depositEntity; // DepositEntity 조합
    private BigDecimal minAmount;         // 최소 가입금액
    private BigDecimal maxAmount;         // 최대 가입금액

    public DepositVo(DepositEntity depositEntity) {
        this.depositEntity = depositEntity;
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        parseSubAmount(depositEntity.getSubAmount()); // subAmount를 기반으로 minAmount와 maxAmount 파싱
        parseKeywords(depositEntity.getKeyword()); // String 으로 되어있는 키워드 분리
        // description에서 키워드 추출
        List<String> extractedKeywords = keywordExtractor.extractKeywords(depositEntity.getDescription());
        List<String> ageKeywordAddList = new ArrayList<>(depositEntity.getKeywordList());
        ageKeywordAddList.addAll(extractedKeywords);
        depositEntity.setKeywordList(ageKeywordAddList);
    }

    private void parseSubAmount(String subAmount) {
        // subAmount 범위를 파싱하여 최소 및 최대 금액을 설정
        BigDecimal[] range = parseSubAmountToMinMax(subAmount);
        this.minAmount = range[0];
        this.maxAmount = range[1];
    }

    private BigDecimal[] parseSubAmountToMinMax(String subAmount) {
        BigDecimal minAmount = BigDecimal.ZERO;
        BigDecimal maxAmount = BigDecimal.valueOf(Long.MAX_VALUE); // 기본값을 제한 없음으로 설정

        // 정규 표현식 패턴
        String regex = "(\\d+)백만원|((\\d+)만원)\\s*(이상|이하|부터|제한없음)?|((\\d+)만원)\\s*이상\\s*(\\d+)백만원|(\\d+)만원부터\\s*(\\d+)만원까지";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(subAmount);

        while (matcher.find()) {
            // 백만원 처리
            if (matcher.group(1) != null) {
                minAmount = new BigDecimal(matcher.group(1)).multiply(BigDecimal.valueOf(1000000)); // 백만원 단위 변환
            }
            // 만원 처리
            if (matcher.group(3) != null) {
                BigDecimal amount = new BigDecimal(matcher.group(3)).multiply(BigDecimal.valueOf(10000)); // 만원 단위 변환
                String qualifier = matcher.group(4); // '이상', '이하', '부터', '제한없음'

                if ("이상".equals(qualifier) || "부터".equals(qualifier)) {
                    minAmount = amount; // 최소 금액
                } else if ("이하".equals(qualifier)) {
                    maxAmount = amount; // 최대 금액
                } else if ("제한없음".equals(qualifier)) {
                    minAmount = amount; // 제한 없음인 경우 최소 금액으로 설정
                    maxAmount = BigDecimal.valueOf(Long.MAX_VALUE); // 최대 금액은 제한 없음
                } else {
                    minAmount = amount; // 아무런 제한이 없을 경우
                }
            }
            // 만원 처리 (이하)
            if (matcher.group(6) != null) {
                maxAmount = new BigDecimal(matcher.group(6)).multiply(BigDecimal.valueOf(10000)); // 만원 단위 변환
            }
            // 범위 처리 (이상 백만원)
            if (matcher.group(7) != null) {
                minAmount = new BigDecimal(matcher.group(7)).multiply(BigDecimal.valueOf(10000)); // 만원 단위 변환
                maxAmount = new BigDecimal(matcher.group(8)).multiply(BigDecimal.valueOf(1000000)); // 백만원 단위 변환
            }
            // 만원 범위 처리 (시작 및 끝)
            if (matcher.group(9) != null && matcher.group(10) != null) {
                minAmount = new BigDecimal(matcher.group(9)).multiply(BigDecimal.valueOf(10000)); // 시작 만원 단위 변환
                maxAmount = new BigDecimal(matcher.group(10)).multiply(BigDecimal.valueOf(10000)); // 끝 만원 단위 변환
            }
        }

        return new BigDecimal[]{minAmount, maxAmount};
    }

    private void parseKeywords(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            this.depositEntity.setKeywordList(Arrays.asList(keyword.split(",")));
        }
    }

}
