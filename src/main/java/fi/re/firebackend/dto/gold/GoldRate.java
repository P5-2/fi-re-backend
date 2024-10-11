package fi.re.firebackend.dto.gold;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GoldRate {
    private int basDt;
    private int clpr;
    private String pBasDt;
    private long dayPrc;
}
