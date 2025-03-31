package com.relove.controller;

import com.relove.model.dto.UserRegisterDTO;
import com.relove.model.dto.UserViewDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Set;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("registerData")
    public UserRegisterDTO registerDTO() {
        return new UserRegisterDTO();
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid UserRegisterDTO data,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/register";
        }

        boolean success = userService.register(data);

        if (!success) {
            return "redirect:/register";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/favourites")
    public String favourites() {
        return "favourites"; // templates/favorites.html
    }

    @GetMapping("/profile")
    public String getMyProfile(Model model, Principal principal) {
        String userEmail = principal.getName();
        UserViewDTO user = userService.getUserViewByEmail(userEmail);
        model.addAttribute("user", user);
        return "my-profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        String email = principal.getName();
        UserEntity user = userService.findByEmail(email);

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setName(user.getName());
        userRegisterDTO.setEmail(user.getEmail());

        model.addAttribute("userRegisterDTO", userRegisterDTO);
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
                                BindingResult bindingResult,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegisterDTO", userRegisterDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterDTO", bindingResult);
            return "edit-profile";
        }

        userService.updateProfile(principal.getName(), userRegisterDTO);
        return "redirect:/profile";
    }

    @GetMapping("/add-to-favorites/{id}")
    public String addToFavorites(@PathVariable Long id, Principal principal) {
        userService.addFavoriteProduct(principal.getName(), id);
        return "redirect:/products/details/" + id;
    }


    @GetMapping("/favorites")
    public String showFavorites(Model model, Principal principal) {
        Set<Product> favorites = userService.getFavoriteProducts(principal.getName());
        model.addAttribute("favoriteProducts", favorites);
        return "favorites";
    }

    @GetMapping("/remove-favorite/{id}")
    public String removeFavorite(@PathVariable Long id, Principal principal) {
        userService.removeFavoriteProduct(principal.getName(), id);
        return "redirect:/favorites";
    }


}
