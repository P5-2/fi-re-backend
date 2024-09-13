package fi.re.firebackend.dto.gold;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
