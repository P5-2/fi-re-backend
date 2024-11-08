package fi.re.firebackend.dto.finance.savings;

//import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
@ToString
public class AllPageListDto {
    private int pageNumber;
    private String prdtDiv;

}
