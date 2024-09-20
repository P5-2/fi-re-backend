package fi.re.firebackend.service.gold;

import fi.re.firebackend.dto.gold.GoldPredicted;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoldPredictionService {

    //예측된 금 값(test데이터만큼 반환)
    List<GoldPredicted> getFutureGoldPrice(String today) throws Exception;

    //도래한 날짜 삭제 및 예측되지 않은 미래의 금 시세 예측
    void goldPredictUpdate() throws Exception;
}
