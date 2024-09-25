package fi.re.firebackend.dto.financeWord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceWordDto { //데이터베이스 정보를 받아오기위한 dto
    private int id;
    private String fnceDictNm; //용어 이름
    private String ksdFnceDictDescContent; //용어 설명
}
