package fi.re.firebackend.controller.profile;


import fi.re.firebackend.dto.member.MemberDto;
import fi.re.firebackend.dto.news.NewsDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.service.news.NewsService;
import fi.re.firebackend.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final NewsService newsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ProfileController( ProfileService profileService, JwtTokenProvider jwtTokenProvider, NewsService newsService) {
        this.profileService = profileService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<MemberDto> getProfile( HttpServletRequest request) {

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
}
