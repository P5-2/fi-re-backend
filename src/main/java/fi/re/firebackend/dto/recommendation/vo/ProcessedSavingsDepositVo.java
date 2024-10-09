package fi.re.firebackend.dto.recommendation.vo;

import fi.re.firebackend.dto.recommendation.SavingsDepositEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedSavingsDepositVo {

    private String finPrdtCd; //Financial Product Code
    private String korCoNm; //Bank Name
    private String finPrdtNm; //Financial Product Name
    private String joinWay; //가입 방법
    private List<String> keywords; // 추출된 키워드 리스트
    private double maxLimit; //최대한도
    private String intrRateTypeNm; //저축 금리 유형명(단리 or 복리)
    private int saveTrm; //저축 기간 (숫자로 변환)
    private double intrRate; // 기본 금리
    private double intrRate2; // 최고 금리
    private int selectCount; //선택 횟수

    // 생성자에서 SavingsDepositEntity -> ProcessedSavingsDepositVo
    public ProcessedSavingsDepositVo(SavingsDepositEntity entity) {
        this.finPrdtCd = entity.getFinPrdtCd();
        this.korCoNm = entity.getKorCoNm();
        this.finPrdtNm = entity.getFinPrdtNm();
        this.joinWay = entity.getJoinWay();
        this.maxLimit = entity.getMaxLimit();
        this.intrRateTypeNm = entity.getIntrRateTypeNm();
        this.saveTrm = Integer.parseInt(entity.getSaveTrm());
        this.intrRate = entity.getIntrRate() != null ? Double.parseDouble(entity.getIntrRate()) : 0.0; // 기본값 0.0
        this.intrRate2 = entity.getIntrRate2() != null ? Double.parseDouble(entity.getIntrRate2()) : 0.0; // 기본값 0.0
        this.selectCount = entity.getSelectCount();
        // 키워드 추출
        this.keywords = extractKeywords(entity);
    }

    // 키워드 추출 메서드: spclCnd, joinMember, etcNote에서 키워드 추출
    private List<String> extractKeywords(SavingsDepositEntity entity) {
        List<String> keywordList = new ArrayList<>();

        // spclCnd에서 키워드 추출
        if (entity.getSpclCnd() != null && !entity.getSpclCnd().isEmpty()) {
            keywordList.addAll(extractKeywords(entity.getSpclCnd()));
        }

        // joinMember에서 키워드 추출
        if (entity.getJoinMember() != null && !entity.getJoinMember().isEmpty()) {
            keywordList.addAll(extractKeywords(entity.getJoinMember()));
        }

        // etcNote에서 키워드 추출
        if (entity.getEtcNote() != null && !entity.getEtcNote().isEmpty()) {
            keywordList.addAll(extractKeywords(entity.getEtcNote()));
        }

        if(this.saveTrm < 12){
            keywordList.add("단기");
        }else{
            keywordList.add("장기");
        }

        return keywordList;
    }

    // 키워드 추출 메서드
    private static List<String> extractKeywords(String text) {
        List<String> keywordList = new ArrayList<>();

        // 찾고자 하는 키워드 정규 표현식
        String regex = "(만기|우대이율|개인|법인|여성|영업점|인터넷|스마트폰|소액|최초|매주|디지털|모바일|복리|단리|차등금리|최소가입금액|납입한도|비대면|최저가입금액|최고우대금리|자동만기연장)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 키워드가 발견될 때마다 리스트에 추가
        while (matcher.find()) {
            String keyword = matcher.group();
            if (!keywordList.contains(keyword)) { // 중복 키워드 추가 방지
                keywordList.add(keyword);
            }
        }
        return keywordList;
    }

}
