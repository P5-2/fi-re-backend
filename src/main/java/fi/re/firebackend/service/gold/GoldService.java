package fi.re.firebackend.service.gold;

import fi.re.firebackend.dto.gold.GoldPredicted;
import fi.re.firebackend.dto.gold.GoldInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoldService {
    //최근 업데이트 최신화 날짜
//    int getLastUpdateDate();

    int setDataFromAPI();

    //현재 날짜로부터 1년 전까지의 데이터를 받아오는 함수
    //매개변수로 받는 endBasDt와 OpenApi의 endBasDt는 다름
    List<GoldInfo> getGoldInfoInPeriod(String endBasDt, int days);

    //예측된 금 값(1년 뒤 금 값까지 반환)
    List<GoldPredicted> getFutureGoldPrice() throws Exception;
}
