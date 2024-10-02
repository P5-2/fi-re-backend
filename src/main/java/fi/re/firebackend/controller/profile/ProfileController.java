package fi.re.firebackend.controller.profile;


import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.dto.news.NewsDto;
import fi.re.firebackend.dto.profile.MemberSavingsRequestDto;
import fi.re.firebackend.dto.profile.MemberSavingsResponseDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.service.news.NewsService;
import fi.re.firebackend.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final NewsService newsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ProfileController(ProfileService profileService, JwtTokenProvider jwtTokenProvider, NewsService newsService) {
        this.profileService = profileService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<MemberDto> getProfile(HttpServletRequest request) {

        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtTokenProvider.getUserInfo(token);
        System.out.println(username);
        MemberDto memberDto = profileService.getProfile(username);
        if (memberDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(memberDto);

    }

    @GetMapping("/news")
    public ResponseEntity<NewsDto> getNews(@RequestParam String query) {
        NewsDto newsDto = newsService.getNews(query);

        // 에러 메시지가 있는 경우
        if (newsDto.getErrorMessage() != null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(newsDto); // 에러 메시지 포함
        }

        return ResponseEntity.ok(newsDto);
    }

    // 현재 모으고 있는 예적금 리스트
    // 모으는 목표 이름 얼마나 모았는지, 이번달 얼마나 넣었는지, 종류가 뭔지, 언제 시작했는지, 언제 끝나는지
    @GetMapping("/goal")
    public ResponseEntity<List<MemberSavingsResponseDto>> getMemberSavings(HttpServletRequest request) {

        // 토큰 확인
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 토큰에서 사용자 정보 추출
        String username = jwtTokenProvider.getUserInfo(token);

        // 해당 사용자의 예적금 리스트 조회
        List<MemberSavingsResponseDto> userSavings = profileService.getMemberSavings(username);

        if (userSavings.isEmpty()) {
            // 204 No Content 반환
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok(userSavings);
    }


    // MemberSavings 테이블에 상품을 추가
    @PostMapping("/setgoal")
    public ResponseEntity<String> addMemberSavings(@RequestBody MemberSavingsRequestDto memberSavingsRequestDto, HttpServletRequest request) {
        //상품 코드로 받아서 엔티티를 채우기
        // 토큰 확인
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 사용자 정보 추출
        String username = jwtTokenProvider.getUserInfo(token);

        // 예적금 추가 로직 실행
        int result = profileService.addMemberSavings(username, memberSavingsRequestDto);

        if (result > 0) {
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding savings");
        }
    }


    // MemberSavings 테이블의 row를 삭제
    @DeleteMapping("/goal/{prdNo}")
    public ResponseEntity<String> deleteMemberSavings(@PathVariable("prdNo") String prdNo, HttpServletRequest request) {

        // 토큰 확인
        String token = request.getHeader(JwtTokenProvider.httpHeaderKey);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 사용자 정보 추출
        String username = jwtTokenProvider.getUserInfo(token);

        // 해당 상품 삭제 로직 실행
        boolean result = profileService.deleteMemberSavings(username, prdNo);
        if (result) {
            return ResponseEntity.ok("Deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting savings");
        }
    }


}
