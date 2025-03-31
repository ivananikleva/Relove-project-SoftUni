package com.relove.controller;

import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.service.CartService;
import com.relove.service.OrderService;
import com.relove.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public String createOrder(Principal principal) {
        String email = principal.getName();
        orderService.createOrder(email);
        return "redirect:/orders/confirmation";
    }

    @GetMapping("/confirmation")
    public String showConfirmation() {
        return "order-confirmation";
    }
}
