package fi.re.firebackend.dto.gold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoldDiff {
    private Date date;
    private long dayPrice;
    private long diff;
}
