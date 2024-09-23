package fi.re.firebackend.dto.forex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
//api를 받아오기 위한 dto
public class ForexDto {
    @JsonProperty("result")
    private int result;

    @JsonProperty("cur_unit")
    private String curUnit;

    @JsonProperty("ttb")
    private String ttb;

    @JsonProperty("tts")
    private String tts;

    @JsonProperty("deal_bas_r")
    private String dealBasR;

    @JsonProperty("bkpr")
    private String bkpr;

    @JsonProperty("yy_efee_r")
    private String yyEfeeR;

    @JsonProperty("ten_dd_efee_r")
    private String tenDdEfeeR;

    @JsonProperty("kftc_bkpr")
    private String kftcBkpr;

    @JsonProperty("kftc_deal_bas_r")
    private String kftcDealBasR;

    @JsonProperty("cur_nm")
    private String curNm;

}
