package fi.re.firebackend.dto.forex;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ForexResponseDto {
    private String curUnit;        // 통화 코드
    private String curNm;       // 국가/통화명
    private double dealBasR;       // 매매 기준율(은행과 거래할 때 기준이 되는 금액)
    private double ttb;            // 전신환(송금) 받으실때
    private double tts;            // 전신환(송금) 보내실때
    private int tenDdEfeeR;        // 10일환가료율
}
