package fi.re.firebackend.dto.exp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitedDto {
    private String username;
    private String page;
    private String visitDate;

}
