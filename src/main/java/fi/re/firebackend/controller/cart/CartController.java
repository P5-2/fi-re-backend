package fi.re.firebackend.controller.cart;

import fi.re.firebackend.dto.cart.CartDto;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    // 특정 회원의 장바구니에 담긴 펀드 상품 목록 조회
    @GetMapping("/funds/{username}")
    public List<FundDto> getFundsInCart(@PathVariable String username) {
        return cartService.getFundsInCart(username);
    }

    // 장바구니에 펀드 상품 추가
    @PostMapping("/funds")
    public ResponseEntity<String> addFundToCart(@RequestBody CartDto cart) {
        cartService.addFundToCart(cart);
        return ResponseEntity.ok("Fund added to cart successfully");
    }

    // 장바구니에서 특정 펀드 상품 개별 삭제
    @DeleteMapping("/funds/{username}/{prdNo}")
    public ResponseEntity<String> removeFundFromCart(@PathVariable String username, @PathVariable int prdNo) {
        cartService.removeFundFromCart(username, prdNo);
        return ResponseEntity.ok("Fund removed from cart successfully");
    }

    // 사용자가 장바구니에 펀드를 추가했는지 확인
    @GetMapping("/funds/{username}/{prdNo}")
    public ResponseEntity<Map<String, Boolean>> checkFundInCart(@PathVariable String username, @PathVariable int prdNo) {
        try {
            boolean isInCart = cartService.isFundInUserCart(username, prdNo);
            return ResponseEntity.ok(Collections.singletonMap("isInCart", isInCart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", true));
        }
    }
}