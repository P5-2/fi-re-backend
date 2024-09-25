package fi.re.firebackend.controller.financeWord;

import fi.re.firebackend.dto.financeWord.FinanceWordDto;
import fi.re.firebackend.service.financeWord.FinanceWordService;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/financeWord")
public class FinanceWordController {
    FinanceWordService financeWordService;

    public FinanceWordController(FinanceWordService financeWordService) {this.financeWordService = financeWordService;}

    @GetMapping("/get")
    public FinanceWordDto getFinanceWord(String date){ //2024-09-24 형태
        int id = 1; //매일 정수값 하나를 고정으로 얻는다.
        try{
            ClassPathResource resource = new ClassPathResource("RandomId.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String readDate = br.readLine(); //날짜 읽어오기
            String readId = br.readLine(); //아이디 읽어오기
            System.out.println(readDate+" "+readId);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); //데이트 포멧팅
            Date clientDate = format.parse(date);
            Date serverDate = format.parse(readDate);
            if(clientDate.after(serverDate)){//클라이언트 날짜가 서버날짜보다 이후인 경우
                //새로운 랜덤값을 생성하여 txt에 저장
                Random random = new Random();
                id = random.nextInt(500)+1;

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resource.getFile())));

                bw.write(date);
                bw.newLine();
                bw.write(String.valueOf(id));
                bw.flush();
                bw.close();
            }else{ //클라이언트 날짜가 서버날짜랑 같거나 이전인 경우
                //txt에서 읽은 id를 제공
                id = Integer.parseInt(readId);
            }
        } catch(IOException e){
            System.out.println(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return financeWordService.getFinanceWord(id);
    }
}
