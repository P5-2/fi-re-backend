package fi.re.firebackend.dto.finance.savings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingsDepositDto {

    @JsonProperty("fin_prdt_cd")
    private String finPrdtCd; //Financial Product Code

    @JsonProperty("kor_co_nm")
    private String korCoNm; //bankName

    @JsonProperty("fin_prdt_nm")
    private String finPrdtNm; //Financial Product Name

    @JsonProperty("join_way")
    private String joinWay; // 가입방법

    @JsonProperty("spcl_cnd")
    private String spclCnd; //우대조건

    @JsonProperty("join_member")
    private String joinMember; //가입대상

    @JsonProperty("etc_note")
    private String etcNote; //기타 유의사항

    @JsonProperty("max_limit")
    private Long maxLimit; //최대한도

    //maxLimit이 null인 경우 처리
    //maxLimit이 null인 경우 처리를 위해 SavingsDepositDto의 maxLimit 필드 타입을 Long으로 변경하고 (primitive type long 대신)

    @JsonProperty("prdt_div")
    private String prdtDiv; // "S" 또는 "D" savings or deposit 구분

    // private String productType; // "SAVINGS" 또는 "DEPOSIT"
    // savings or deposit 구분 어떻게 할건지 생각해서 처리
}



