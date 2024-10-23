package fi.re.firebackend.service.login;

import fi.re.firebackend.dao.login.MemberDao;
import fi.re.firebackend.dto.login.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.jwt.dto.TokenDto;
import fi.re.firebackend.security.SecurityUser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@PropertySource({"classpath:/application.properties"})
public class NaverLoginService {

    private final MemberDao memberDao;
    private final JwtTokenProvider jwtTokenProvider;

    public NaverLoginService( MemberDao memberDao, JwtTokenProvider jwtTokenProvider) {

        this.memberDao = memberDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Value("${naver.client_id}")
    private String CLIENT_ID;

    @Value("${naver.client_secret}")
    private String CLIENT_SECRET;


    // token 받아오기
    public NaverTokenDto getNaverToken(String code, String state) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://nid.naver.com/oauth2.0/token";

        String result = null;

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + CLIENT_ID);
            sb.append("&client_secret=" + CLIENT_SECRET);
            sb.append("&code=" + code);
            sb.append("&state=" + state);
            bw.write(sb.toString());
            bw.flush();

            //응답 코드가 200이면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode={}", responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 Read
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            log.info("response body={}", result);

            // Gson 라이브러리에 포함된 클래스로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("네이버 access token={}", access_Token);
            log.info("네이버 refresh token={}", refresh_Token);

            br.close();
            bw.close();
            conn.disconnect();

        }catch (Exception e){
            e.printStackTrace();
        }

        NaverTokenDto naverTokenDto = NaverTokenDto.builder()
                .accessToken(access_Token)
                .refreshToken(refresh_Token)
                .build();

        return naverTokenDto;
    }



    public TokenDto loginWithNaver(NaverTokenDto naverTokenDto) throws IOException {

        //회원 정보 요청 url
        String reqURL = "https://openapi.naver.com/v1/nid/me";

        //accessToken 통한 사용자 정보 조회
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + naverTokenDto.getAccessToken()); //전송할 header 작성, access_token전송

        //결과 코드가 200이면 성공
        int responseCode = conn.getResponseCode();
        log.info("responseCode={}", responseCode);

        //요청을 통해 얻은 JSON타입의 Response 메세지 Read
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        log.info("response body={}", result);

        //Gson 라이브러리로 JSON파싱
        JsonElement element = JsonParser.parseString(result);
        String id = element.getAsJsonObject().get("response").getAsJsonObject().get("id").getAsString();
        String name = element.getAsJsonObject().get("response").getAsJsonObject().get("nickname").getAsString();

        // DB에 해당 이름이 없을 경우 회원 가입 로직 실행
        if (!memberDao.ExistByName(name)) { // memberRepository -> memberDao
            SecurityUser member = SecurityUser.builder() // Member는 SecurityUser
                    .password("") // 비밀번호는 비워둡니다
                    .username(id) // username을 id로 설정
                    .name(name)
                    .auth("ROLE_USER") // auth 필드에 ROLE_USER 설정
                    .build();

            MemberDto memberDto = new MemberDto();
            memberDto.setUsername(id);      // Set username as id
            memberDto.setPlatform("Naver"); // Set the platform, assuming 'platform' is defined in your context
            memberDto.setNickname(name);     // Set nickname as name
            memberDto.setExp(0);
            memberDao.save(member);
            memberDao.memberSave(memberDto);


        }

        // DB에 해당 이름의 회원 정보 있을 경우 JWT 토큰 생성해서 리턴
        SecurityUser member = memberDao.findByName(name); // Optional을 사용하지 않고 직접 호출
        if (member == null) { // 회원이 존재하지 않을 경우 처리
            throw new RuntimeException("User not found");
        }
        TokenDto tokenDto = jwtTokenProvider.createToken(member.getUsername(), Collections.singletonList(member.getAuth()));
        tokenDto.setGrantType(member.getAuth());

        log.info("tokenDto=" + tokenDto);
        return tokenDto;
    }

    // 닉네임 찾기
    public String findName(String username){
        return memberDao.findName(username);
    }
}
