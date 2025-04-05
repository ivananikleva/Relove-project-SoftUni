package com.relove.controller;

import com.relove.model.entity.Order;
import com.relove.model.entity.UserEntity;
import com.relove.service.OrderService;
import com.relove.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;

    public AdminController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<UserEntity> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @GetMapping("/orders")
    public String viewAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin-orders";
    }


    @PostMapping("/users/change-role/{id}")
    public String changeUserRole(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        UserEntity currentUser = userService.findByEmail(principal.getName());

        if (currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Не можете да промените собствената си роля.");
        } else {
            userService.toggleUserRole(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ролята беше успешно променена.");
        }

        return "redirect:/admin/users";
    }



}
