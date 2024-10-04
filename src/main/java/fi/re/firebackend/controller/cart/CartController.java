package fi.re.firebackend.controller.cart;

import fi.re.firebackend.dto.cart.CartDto;
import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositWithOptionsDto;
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

    // 사용자가 즐겨찾기에 펀드를 추가했는지 확인
    @GetMapping("/funds/{username}/{prdNo}")
    public ResponseEntity<Map<String, Boolean>> checkFundInCart(@PathVariable String username, @PathVariable int prdNo) {
        try {
            boolean isInCart = cartService.isFundInUserCart(username, prdNo);
            return ResponseEntity.ok(Collections.singletonMap("isInCart", isInCart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", true));
        }
    }



    // 장바구니에 저축 상품 추가
    @PostMapping("/savings")
    public ResponseEntity<String> addSavingsToCart(@RequestBody CartDto cart) {
        cartService.addSavingsToCart(cart);
        return ResponseEntity.ok("Savings product added to cart successfully");
    }

    // 장바구니에서 특정 저축 상품 개별 삭제
    @DeleteMapping("/savings/{username}/{finPrdtCd}")
    public ResponseEntity<String> removeSavingsFromCart(@PathVariable String username, @PathVariable String finPrdtCd) {
        cartService.removeSavingsFromCart(username, finPrdtCd);
        return ResponseEntity.ok("Savings product removed from cart successfully");
    }

    // 사용자가 장바구니에 저축 상품을 추가했는지 확인
    @GetMapping("/savings/{username}/{finPrdtCd}")
    public ResponseEntity<Map<String, Boolean>> checkSavingsInCart(@PathVariable String username, @PathVariable String finPrdtCd) {
        try {
            boolean isInCart = cartService.isSavingsInUserCart(username, finPrdtCd);
            return ResponseEntity.ok(Collections.singletonMap("isInCart", isInCart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", true));
        }
    }

    // 장바구니에 예금 상품 추가
    @PostMapping("/deposits")
    public ResponseEntity<String> addDepositToCart(@RequestBody CartDto cart) {
        cartService.addDepositToCart(cart);
        return ResponseEntity.ok("Deposit product added to cart successfully");
    }

    // 장바구니에서 특정 예금 상품 개별 삭제
    @DeleteMapping("/deposits/{username}/{finPrdtCd}")
    public ResponseEntity<String> removeDepositFromCart(@PathVariable String username, @PathVariable String finPrdtCd) {
        cartService.removeDepositFromCart(username, finPrdtCd);
        return ResponseEntity.ok("Deposit product removed from cart successfully");
    }

    // 사용자가 장바구니에 예금 상품을 추가했는지 확인
    @GetMapping("/deposits/{username}/{finPrdtCd}")
    public ResponseEntity<Map<String, Boolean>> checkDepositInCart(@PathVariable String username, @PathVariable String finPrdtCd) {
        try {
            boolean isInCart = cartService.isDepositInUserCart(username, finPrdtCd);
            return ResponseEntity.ok(Collections.singletonMap("isInCart", isInCart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", true));
        }
    }


    // 즐겨 찾기에 포함된 특정 사용자의 적금 상품 조회 API
    @GetMapping("/savings/{username}")
    public List<SavingsDepositWithOptionsDto> getSavingsDepositByUsername(@PathVariable String username) {
        return cartService.getSavingsDepositByUsername(username);
    }

    // 즐겨 찾기에 포함된 특정 사용자의 예금 상품 조회 API
    @GetMapping("/deposit/{username}")
    public List<SavingsDepositWithOptionsDto> getDepositByUsername(@PathVariable String username) {
        return cartService.getDepositByUsername(username);
    }

    // 즐겨 찾기에 포함된 특정 사용자의 적금 및 예금 상품 모두 조회하는 API
    @GetMapping("/savings-deposit/{username}")
    public Map<String, List<SavingsDepositWithOptionsDto>> getSavingsAndDepositByUsername(@PathVariable String username) {
        // 적금 및 예금 상품 정보를 가져옴
        List<SavingsDepositWithOptionsDto> savingsDeposits = cartService.getSavingsDepositByUsername(username);
        List<SavingsDepositWithOptionsDto> deposits = cartService.getDepositByUsername(username);

        // 결과를 맵으로 묶어 반환
        Map<String, List<SavingsDepositWithOptionsDto>> result = new HashMap<>();
        result.put("savings", savingsDeposits);
        result.put("deposits", deposits);
        return result;
    }
}