package com.relove.controller;


        import com.relove.model.dto.CartItemDTO;
        import com.relove.service.CartService;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import java.security.Principal;
        import java.util.List;

@Controller
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id, Principal principal) {
        CartItemDTO item = new CartItemDTO(id, principal.getName());
        item.setQuantity(1);
        cartService.addToCart(item);
        return "redirect:/products/details/" + id;
    }

    @PostMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable Long id, Principal principal) {
        cartService.removeFromCart(principal.getName(), id);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        List<CartItemDTO> cartItems = cartService.getCartItems(principal.getName());
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    @PutMapping("/api/cart")
    public ResponseEntity<Void> updateCartQuantity(@RequestBody CartItemDTO dto, Principal principal) {
        System.out.println(">>> UPDATE QUANTITY CALLED by " + principal.getName());
        dto.setUserEmail(principal.getName());
        cartService.updateQuantity(dto);
        return ResponseEntity.noContent().build();
    }





}
