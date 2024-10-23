package fi.re.firebackend.dto.gold;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GoldPredicted {
    private String pBasDt;
    private long dayPrc;
}