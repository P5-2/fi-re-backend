package fi.re.firebackend.service.login;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nimbusds.jwt.JWT;
import fi.re.firebackend.dao.login.MemberDao;
import fi.re.firebackend.dto.login.KakaoTokenDto;
import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.jwt.dto.TokenDto;
import fi.re.firebackend.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class KakaoLoginService {


    private final MemberDao memberDao;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginService( MemberDao memberDao, JwtTokenProvider jwtTokenProvider) {

        this.memberDao = memberDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")


//    @Value("${application.spring.security.kakao.redirect-uri}")


    // Token 가져오기
    public KakaoTokenDto getKakaoToken(String code) {

        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";
        String rest_api_key="3ac8e4957835e3e46a6bbce90bdee2e7";
        String redirect_uri="http://localhost:5173/kakao/callback";
        String client_secret="RtiowmigZAXeFWRqXdilvNnZLCL0FIyh";

        String result = null;
        String id_token = null;

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + rest_api_key);
            sb.append("&redirect_uri=" + redirect_uri);
            System.out.println("code = " + code);
            sb.append("&code=" + code);
            sb.append("&client_secret=" + client_secret);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode={}", responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 Read
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            // bearer 토큰 값만 추출(log에 찍히는 값의 이름은 id_Token)
            log.info("response body={}", result);
            String[] temp = result.split(",");
            id_token = temp[3].substring(11);
            log.info("idToken={}", id_token);

            // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("카카오 access token={}", access_Token);
            log.info("카카오 refresh token={}", refresh_Token);

            br.close();
            bw.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        KakaoTokenDto kakaoTokenDto = KakaoTokenDto.builder()
                .accessToken(access_Token)
                .refreshToken(refresh_Token)
                .build();

        return kakaoTokenDto;
    }

    public TokenDto loginWithKakao(KakaoTokenDto kakaoTokenDto) throws IOException, IOException {

        //1.유저 정보를 요청할 url
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //2.access_token을 이용하여 사용자 정보 조회
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + kakaoTokenDto.getAccessToken()); //전송할 header 작성, access_token전송

        //결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        log.info("responseCode={}", responseCode);

        //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        log.info("response body={}", result);

        //Gson 라이브러리로 JSON파싱
        JsonElement element = JsonParser.parseString(result);

        String id = element.getAsJsonObject().get("id").getAsString();
        String nickname = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();

        // DB에 해당 이름이 없을 경우 회원 가입 로직 실행
        if (!memberDao.ExistByName(nickname)) { // memberRepository -> memberDao
            SecurityUser member = SecurityUser.builder() // Member는 SecurityUser
                    .password("") // 비밀번호는 비워둡니다
                    .username(id) // username을 id로 설정
                    .name(nickname)
                    .auth("ROLE_USER") // auth 필드에 ROLE_USER 설정
                    .build();

            MemberDto memberDto = new MemberDto();
            memberDto.setUsername(id);      // Set username as id
            memberDto.setPlatform("Kakao"); // Set the platform, assuming 'platform' is defined in your context
            memberDto.setNickname(nickname);     // Set nickname as name

            memberDao.save(member);
            memberDao.memberSave(memberDto);
        }




//        DB에 해당 회원정보 있을경우 JWT Token 생성 후 리턴
        SecurityUser member = memberDao.findByName(nickname); // Optional을 사용하지 않고 직접 호출
        if (member == null) { // 회원이 존재하지 않을 경우 처리
            throw new RuntimeException("User not found");
        }
        TokenDto tokenDto = jwtTokenProvider.createToken(member.getUsername(), Collections.singletonList(member.getAuth()));
        tokenDto.setGrantType(member.getAuth());

        // jwtService.saveRefreshToken(tokenDto); // 필요 시 리프레시 토큰 저장 로직 추가
        System.out.println("tokenDto=" + tokenDto);
        return tokenDto;
    }

    // 닉네임 찾기
    public String findName(String username){
        return memberDao.findName(username);
    }

}
