package fi.re.firebackend.dto.finance.savings;

//import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
@ToString
public class AllPageListDto {
    //@JsonProperty("now_page_no")
    private int pageNumber;
    //@JsonProperty("prdt_div")
    private String prdtDiv;
    
}
