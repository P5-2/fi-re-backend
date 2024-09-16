package fi.re.firebackend.dto.gold;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoldInfo {
    private int basDt;
    private int clpr;
    private String srtnCd;
    private int vs;
    private double fltRt;
    private int mkp;
    private int hipr;
    private int lopr;
    private int trqu;
}
