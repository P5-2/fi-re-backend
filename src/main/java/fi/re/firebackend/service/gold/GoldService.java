package fi.re.firebackend.service.gold;

import fi.re.firebackend.dto.gold.GoldInfo;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface GoldService {
    void setDataFromAPI() throws ParseException;

    //현재 날짜로부터 1년 전까지의 데이터를 받아오는 함수
    //매개변수로 받는 endBasDt와 OpenApi의 endBasDt는 다름
    List<GoldInfo> getGoldInfoInPeriod(String endBasDt, int days) throws ParseException;

    //GoldRate 객체로 미래 이율 계산
    double rate();
}
