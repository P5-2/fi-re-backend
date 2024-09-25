package fi.re.firebackend.service.financeWord;

import fi.re.firebackend.dao.financeWord.FinanceWordDao;
import fi.re.firebackend.dto.financeWord.FinanceWordDto;
import fi.re.firebackend.util.api.FinanceWordApi;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Service
public class FinanceWordService {
    FinanceWordApi financeWordApi;
    FinanceWordDao financeWordDao;

    public FinanceWordService(FinanceWordApi financeWordApi, FinanceWordDao financeWordDao) throws IOException, ParserConfigurationException, SAXException {
        this.financeWordApi = financeWordApi;
        this.financeWordDao = financeWordDao;
        System.out.println("financeword 수: "+financeWordDao.count());
        //finacneWord DB 초기화
        if(financeWordDao.count() == 0){ //db데이터가 비어있으면 데이터 넣기
            for(FinanceWordDto financeWord : this.financeWordApi.getFinanceWordData()){
                System.out.println(financeWord.getFnceDictNm());
                this.financeWordDao.insertFinanceWord(financeWord);
            }
        }
    }

    public FinanceWordDto getFinanceWord(int id){
        return financeWordDao.getFinanceWord(id);
    }
}
