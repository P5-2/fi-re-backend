package fi.re.firebackend.service.exp;


import fi.re.firebackend.dao.exp.ExpDao;
import fi.re.firebackend.dto.exp.ExpDto;
import fi.re.firebackend.dto.exp.VisitedDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ExpService {

    private final ExpDao expDao;

    public ExpService(ExpDao expDao) {
        this.expDao = expDao;
    }

    public void checkExp(String username, String page) {
        System.out.println("Checking experience for username: " + username + ", page: " + page);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 오늘 날짜
        VisitedDto visitedDto = new VisitedDto(username, page, today); // visitDate에 오늘 날짜 설정

        // DB에 저장된 날짜 확인
        String lastestDate = expDao.getDate(visitedDto);
        System.out.println("Last visited date from DB: " + lastestDate);

        // 방문 기록이 존재하는지 확인
        boolean exists = expDao.existsVisited(visitedDto);
        System.out.println("Visit record exists: " + exists);

        int expAmount = "quiz".equals(page) ? 3 : 1; // 경험치 결정

        if (!exists) {
            expDao.insertVisited(visitedDto); // 방문 기록 삽입
            expDao.updateExp(new ExpDto(username, expAmount)); // 경험치 업데이트
            expDao.updateDate(visitedDto); // 날짜 갱신
            System.out.println("Inserted new visit record for user: " + username + " on page: " + page);
        } else if (lastestDate == null || !lastestDate.equals(today)) {
            expDao.updateExp(new ExpDto(username, expAmount)); // 경험치 업데이트
            expDao.updateDate(visitedDto); // 날짜 갱신
            System.out.println("Updated visit date for user: " + username + " on page: " + page + " (date changed)");
        } else {
            System.out.println("No updates made for user: " + username + " on page: " + page + " (already visited today)");
        }
    }


}
