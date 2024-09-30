package fi.re.firebackend.dto.tip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipDto {
    private int id;
    private String name;
    private String type;
    private String path;
    private int count;
}
