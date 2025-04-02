package com.relove.controller;

import com.relove.model.dto.ProductAddDTO;
import com.relove.model.dto.ReviewDTO;
import com.relove.model.entity.Product;
import com.relove.service.ProductService;
import com.relove.service.ReviewService;
import com.relove.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, UserService userService, ReviewService reviewService) {
        this.productService = productService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @ModelAttribute("productAddDTO")
    public ProductAddDTO productAddDTO() {
        return new ProductAddDTO();
    }

    @GetMapping("/add")
    public String showAddForm() {
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("productAddDTO") ProductAddDTO data,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productAddDTO", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productAddDTO", bindingResult);
            return "redirect:/add-product";
        }

        productService.addProduct(data, userDetails.getUsername());
        return "redirect:/products/mine";
    }


    @GetMapping("/mine")
    public String showUserProducts(Model model) {
        model.addAttribute("myProducts", productService.getProductsByCurrentUser());
        return "my-products";
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products/mine";
    }

    @GetMapping("/all")
    public String showAllProducts(Model model) {
        model.addAttribute("allProducts", productService.getAllProducts());
        return "all-products";
    }

    @GetMapping("/details/{id}")
    public String showProductDetails(@PathVariable Long id, Model model, Principal principal) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);

        model.addAttribute("reviews", reviewService.getReviewsForProduct(id));

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(id);
        model.addAttribute("reviewDTO", reviewDTO);



        if (principal != null) {
            boolean isFavorite = userService.isProductFavorite(principal.getName(), id);
            model.addAttribute("isFavorite", isFavorite);
        }

        return "product-details";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        ProductAddDTO data = productService.getProductForEdit(id, userDetails.getUsername());
        model.addAttribute("productAddDTO", data);
        model.addAttribute("productId", id);
        return "edit-product";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productAddDTO") ProductAddDTO data,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productAddDTO", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productAddDTO", bindingResult);
            return "redirect:/products/edit/" + id;
        }

        productService.updateProduct(id, data, userDetails.getUsername());
        return "redirect:/my-products";
    }


}
