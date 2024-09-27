package fi.re.firebackend.util.recommendUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordExtractor {

    // 키워드 패턴
    private static final String KEYWORD_PATTERN = "(청년|중장기|자산형성|금융 상품|정부 기여금|비과세 혜택|적립식 상품|주식|채권|투자|저축|신탁|보험|재테크|이자|수익|목표|변동성|위험|기회|대출|상환|보장|자산|포트폴리오|연금|세금|혜택|신규|지원|지속가능성|가계부채|금리|환율|국내외 경제|리스크|부동산|금융자산|소득|경비|비용|재산|계획|대안|연체|자산관리|\\d{1,2}세|만 \\d{1,2}세|\\d{1,2}~\\d{1,2}세|청년층|중년층|장년층)";

    // 키워드 추출 및 연령대 분류 메서드
    public List<String> extractKeywords(String description) {
        Set<String> uniqueKeywords = new HashSet<>();
        String ageGroup = "나이제한없음"; // 기본 값

        Pattern pattern = Pattern.compile(KEYWORD_PATTERN);
        Matcher matcher = pattern.matcher(description);

        while (matcher.find()) {
            String keyword = matcher.group().trim(); // 발견된 키워드를 가져오고 공백 제거
            uniqueKeywords.add(keyword); // Set에 추가하여 중복 제거
        }

        // 연령대 분류
        for (String keyword : uniqueKeywords) {
            if (keyword.contains("청년") || keyword.contains("청년층") || keyword.matches("2[0-9]세|3[0-9]세")) {
                ageGroup = "청년";
                break; // 청년으로 분류되면 더 이상 확인할 필요 없음
            } else if (keyword.contains("중년") || keyword.contains("중년층") || keyword.matches("4[0-9]세")) {
                ageGroup = "중년";
            } else if (keyword.contains("노년") || keyword.contains("노년층") || keyword.matches("6[6-9]세|[7-9][0-9]세|100세")) {
                ageGroup = "노년";
            }
        }

        // 연령대도 추가
        uniqueKeywords.add(ageGroup); // 연령대 정보를 리스트에 추가

        return new ArrayList<>(uniqueKeywords); // List로 변환하여 반환
    }
}


