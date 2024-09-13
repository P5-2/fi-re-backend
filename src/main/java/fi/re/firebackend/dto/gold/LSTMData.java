package fi.re.firebackend.dto.gold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LSTMData {
    List<GoldDiff> trainData;
    List<GoldDiff> testData;
    List<GoldDiff> learnData;
}
