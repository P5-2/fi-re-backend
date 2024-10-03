package fi.re.firebackend.service.quiz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Getter
@Setter
@Component
public class QuizNumber {
    private LocalDate date;
    private int number;

    public QuizNumber() {
        this.date = LocalDate.now();
        System.out.println("today: " + this.date);
        Random random = new Random();
        this.number = random.nextInt(42)+1;
    }

    public int getNumber(String getDate){
        LocalDate localDate = LocalDate.parse(getDate);
        System.out.println("가져온 날짜: "+localDate);
        if(localDate.isAfter(this.date)){ //프론트에서 온 날짜가 저장된 날짜보다 최신인 경우 최신 데이터 반영
            int newNumber;
            Random random = new Random();
            do{
                newNumber = random.nextInt(42)+1;
            }while(this.number == newNumber);
            this.number = newNumber;
            this.date = localDate;
        }
        return this.number;
    }
}
