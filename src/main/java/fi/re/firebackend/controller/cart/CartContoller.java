package fi.re.firebackend.controller.cart;

import fi.re.firebackend.dto.finance.fund.FundDto;
import fi.re.firebackend.dto.finance.savings.SavingsDto;
import fi.re.firebackend.service.cart.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartContoller {
    private final CartService cartService;

    public CartContoller(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/savings/add")
    public ResponseEntity<String> addSavingToCart(@RequestParam int prdNo) {
        System.out.println("CartContoller addSavingToCart");
        cartService.updateSavingCartStatus(prdNo, true);
        return ResponseEntity.ok("Saving item added to cart successfully.");
    }

    @GetMapping("/savings/remove")
    public ResponseEntity<String> removeSavingFromCart(@RequestParam int prdNo) {
        cartService.updateSavingCartStatus(prdNo, false);
        return ResponseEntity.ok("Saving item removed from cart successfully.");
    }

    @GetMapping("/savings")
    public ResponseEntity<List<SavingsDto>> getSavingsInCart() {
        List<SavingsDto> savingsInCart = cartService.getSavingsInCart();
        return ResponseEntity.ok(savingsInCart);
    }

    // fund
    @GetMapping("/funds/add")
    public ResponseEntity<String> addFundToCart(@RequestParam int prdNo) {
        cartService.updateFundCartStatus(prdNo, true);
        return ResponseEntity.ok("Fund item added to cart successfully.");
    }

    @GetMapping("/funds/remove")
    public ResponseEntity<String> removeFundFromCart(@RequestParam int prdNo) {
        cartService.updateFundCartStatus(prdNo, false);
        return ResponseEntity.ok("Fund item removed from cart successfully.");
    }

    @GetMapping("/funds")
    public ResponseEntity<List<FundDto>> getFundsInCart() {
        List<FundDto> fundsInCart = cartService.getFundsInCart();
        return ResponseEntity.ok(fundsInCart);
    }
}
