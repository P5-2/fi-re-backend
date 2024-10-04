package fi.re.firebackend.dto.forex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexWrapper {
    private List<ForexDto> forexData;
    private LocalDate searchDate;
}
