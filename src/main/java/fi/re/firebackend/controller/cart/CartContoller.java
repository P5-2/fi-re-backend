package fi.re.firebackend.controller.cart;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDepositDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@RestController
//@RequestMapping("/cart")
//public class CartContoller {
//    private final CartService cartService;
//
//    public CartContoller(CartService cartService) {
//        this.cartService = cartService;
//    }
//
//    @GetMapping(value = "/savings/add", produces = "text/plain;charset=UTF-8")
//    public ResponseEntity<String> addSavingToCart(@RequestParam int prdNo) {
//
//        // 상품이 이미 장바구니에 있는지 확인
//        if (cartService.isSavingsInCart(prdNo)) {
//            // 이미 장바구니에 있는 경우
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("해당 상품은 이미 장바구니에 추가되어 있습니다.");
//        } else {
//            // 장바구니에 추가되지 않은 경우
//            cartService.updateSavingCartStatus(prdNo, true);
//            return ResponseEntity.ok("상품이 장바구니에 성공적으로 추가되었습니다.");
//        }
//    }
//
//    @GetMapping(value = "/savings/remove", produces = "text/plain;charset=UTF-8")
//    public ResponseEntity<String> removeSavingFromCart(@RequestParam int prdNo) {
//        // 상품이 장바구니에 있는지 확인
//        if (!cartService.isSavingsInCart(prdNo)) {
//            // 상품이 장바구니에 없는 경우
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("해당 상품은 장바구니에 없습니다.");
//        } else{
//            cartService.updateSavingCartStatus(prdNo, false);
//            return ResponseEntity.ok("상품을 장바구니에 성공적으로 삭제하였습니다.");
//        }
//    }
//
//    @GetMapping("/savings")
//    public ResponseEntity<List<SavingsDepositDto>> getSavingsInCart() {
//        List<SavingsDepositDto> savingsInCart = cartService.getSavingsInCart();
//        return ResponseEntity.ok(savingsInCart);
//    }
//
//    // fund
//    @GetMapping(value ="/funds/add", produces = "text/plain;charset=UTF-8")
//    public ResponseEntity<String> addFundToCart(@RequestParam int prdNo) {
//        // 상품이 장바구니에 있는지 확인
//        if (cartService.isFundInCart(prdNo)) {
//            // 이미 장바구니에 있는 경우
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("해당 상품은 이미 장바구니에 추가되어 있습니다.");
//        } else{
//            cartService.updateFundCartStatus(prdNo, true);
//            return ResponseEntity.ok("상품이 장바구니에 성공적으로 추가되었습니다.");
//        }
//    }
//
//    @GetMapping(value ="/funds/remove", produces = "text/plain;charset=UTF-8")
//    public ResponseEntity<String> removeFundFromCart(@RequestParam int prdNo) {
//        // 상품이 장바구니에 있는지 확인
//        if (!cartService.isFundInCart(prdNo)) {
//            // 상품이 장바구니에 없는 경우
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("해당 상품은 장바구니에 없습니다.");
//        } else{
//            cartService.updateFundCartStatus(prdNo, false);
//            return ResponseEntity.ok("상품을 장바구니에 성공적으로 삭제하였습니다.");
//        }
//    }
//
//    @GetMapping("/funds")
//    public ResponseEntity<List<FundDto>> getFundsInCart() {
//        List<FundDto> fundsInCart = cartService.getFundsInCart();
//        return ResponseEntity.ok(fundsInCart);
//    }
//}
