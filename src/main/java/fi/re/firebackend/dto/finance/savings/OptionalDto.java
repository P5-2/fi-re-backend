package fi.re.firebackend.dto.finance.savings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionalDto {

    @JsonProperty("intr_rate_type_nm")
    private String intrRateTypeNm; //저축 금리 유형명(단리 or 복리)

    @JsonProperty("save_trm")
    private String saveTrm; //저축 기간

    @JsonProperty("intr_rate")
    private String intrRate; //minRate 기본금리

    @JsonProperty("intr_rate2")
    private String intrRate2; //maxRate 최고금리

    @JsonProperty("rsrv_type")
    private String rsrvType; // S(정액적립식) or F(자유적립식)

}
    //SavingsDepositWithOptionsDto 객체의 리스트로 변환하는 로직 필요
//    transform_api_response 함수를 통해 구현
//    1.API 응답 JSON을 파싱
//    2.응답의 'data' 키에 있는 각 항목에 대해 반복
//    3.각 항목의 'options' 배열을 OptionalDTO 객체 리스트로 변환
//    4.항목의 나머지 필드들을 사용하여 SavingsDepositWithOptionsDto 객체를 생성
//    5.생성된 SavingsDepositWithOptionsDto 객체를 리스트에 추가

