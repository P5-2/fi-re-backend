package fi.re.firebackend.dto.finance.savings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingsDepositDto {

    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd;//Financial Product Code

    @JsonProperty("kor_co_nm")
    private String korCoNm;

    @JsonProperty("fin_prdt_nm")//bankName
    private String finPrdtNm;

    @JsonProperty("join_way")//Financial Product Name
    private String joinWay; // 가입방법

    @JsonProperty("spcl_cnd")
    private String spclCnd; //우대조건

    @JsonProperty("join_member")
    private String joinMember; //가입대상

    @JsonProperty("etc_note")
    private String etcNote; //기타 유의사항

    @JsonProperty("max_limit")
    private long maxLimit; //최대한도

    @JsonProperty("intr_rate_type_nm")
    private String intrRateTypeNm; //저축 금리 유형명(단리 or 복리)

    @JsonProperty("save_trm")
    private String saveTrm; //저축 기간

    @JsonProperty("intr_rate")
    private String intrRate; //minRate 기본금리

    @JsonProperty("intr_rate2")
    private String intrRate2; //maxRate 최고금리

}


