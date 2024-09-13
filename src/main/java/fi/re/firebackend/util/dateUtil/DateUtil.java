package fi.re.firebackend.util.dateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getPastDate(int years, int months) {
        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();

        // 유동적으로 이전 날짜 구하기
        LocalDate pastDate = currentDate.minusYears(years).minusMonths(months);

        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 형식에 맞춰 문자열로 변환
        return pastDate.format(formatter);
    }


}
