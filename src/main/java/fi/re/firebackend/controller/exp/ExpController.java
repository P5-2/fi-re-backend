package fi.re.firebackend.controller.exp;

import fi.re.firebackend.dao.exp.ExpDao;
import fi.re.firebackend.dto.exp.VisitedDto;
import fi.re.firebackend.jwt.JwtTokenProvider;
import fi.re.firebackend.service.exp.ExpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/exp")
public class ExpController {

    private final ExpService expService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExpDao expDao;

    public ExpController(ExpService expService, JwtTokenProvider jwtTokenProvider, ExpDao expDao) {
        this.expService = expService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.expDao = expDao;
    }

    @PostMapping
    public ResponseEntity<Void> getVisitedDate(@RequestBody VisitedDto visitedDto, HttpServletRequest request) {
        // JWT 토큰에서 username 추출
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // JWT에서 username을 추출

        String username = jwtTokenProvider.getUserInfo(token);
        System.out.println("Received request with username: " + username + ", page: " + visitedDto.getPage());

        // Service 메서드를 호출
        expService.checkExp(username, visitedDto.getPage());

        // 결과를 ResponseEntity로 반환
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/test")
    public ResponseEntity<Void> test(@RequestParam String username, HttpServletRequest request) {
        // JWT 토큰에서 username 추출
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // JWT에서 username을 추출
        String extractedUsername = jwtTokenProvider.getUserInfo(token);
        System.out.println("Received request to reset exp for username: " + extractedUsername);

        // Service 메서드를 호출
        expDao.decreaseExp(extractedUsername);
        expDao.allDelete(extractedUsername);

        // 결과를 ResponseEntity로 반환
        return ResponseEntity.noContent().build();
    }

}
