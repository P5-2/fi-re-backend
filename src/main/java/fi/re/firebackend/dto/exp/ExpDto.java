package fi.re.firebackend.dto.exp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpDto {
    private String username;
    private int expAmount;

}
