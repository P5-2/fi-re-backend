package fi.re.firebackend.dto.gold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoldInfo {
    private int basDt;
    private int clpr;
    private int vs;
    private double fltRt;
    private int mkp;
    private int hipr;
    private int lopr;
    private int trqu;
}
