package fi.re.firebackend.util.dateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    // 주어진 날짜가 일요일이면 2일을 빼고, 토요일이면 1일을 빼서 가장 최근의 평일 날짜 반환
    public static String getMostRecentWeekday(String dateStr) throws ParseException {
        // 날짜 형식을 지정
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        // String 형식을 Date 형식으로 변환
        Date date = sdf.parse(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // 요일을 확인
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        // 일요일이면 2일 빼기 (금요일로 조정)
        if (dayOfWeek == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, -2);
        }
        // 토요일이면 1일 빼기 (금요일로 조정)
        else if (dayOfWeek == Calendar.SATURDAY) {
            cal.add(Calendar.DATE, -1);
        }

        // 최근의 평일 날짜를 반환
        return sdf.format(cal.getTime());
    }

    // 날짜 차이 계산 함수
    public static int calcDateDiff(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            long diffInMillies = end.getTime() - start.getTime();
            return (int) (diffInMillies / (1000 * 60 * 60 * 24));  // 일 수로 변환
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
