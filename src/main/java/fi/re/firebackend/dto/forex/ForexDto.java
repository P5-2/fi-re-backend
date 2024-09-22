package fi.re.firebackend.dto.forex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForexDto {
    private LocalDate searchDate;  // 날짜
    private String curUnit;        // 통화 코드
    private int result;            // 결과 코드
    private double ttb;            // 매도 환율
    private double tts;            // 매수 환율
    private double dealBasR;       // 기준 환율
    private int bkpr;              // 은행 환율
    private int yyEfeeR;           // 연간 수수료율
    private int tenDdEfeeR;        // 10년 수수료율
    private int kftcBkpr;          // KFTC 은행 환율
    private double kftcDealBasR;   // KFTC 기준 환율
}
