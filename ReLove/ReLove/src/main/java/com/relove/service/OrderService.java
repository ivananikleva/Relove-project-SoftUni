package com.relove.service;

import com.relove.client.CartClient;
import com.relove.model.dto.CartItemDTO;
import com.relove.model.entity.Order;
import com.relove.model.entity.OrderItem;
import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.repo.OrderRepo;
import com.relove.repo.ProductRepo;
import com.relove.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final CartClient cartClient;
    private final UserRepo userRepo;

    public OrderService(OrderRepo orderRepo,
                        ProductRepo productRepo,
                        CartClient cartClient,
                        UserRepo userRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.cartClient = cartClient;
        this.userRepo = userRepo;
    }

    public Order createOrder(String userEmail) {
        UserEntity buyer = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItemDTO> cartItems = cartClient.getCart(userEmail);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Количката е празна.");
        }

        Order order = new Order();
        order.setBuyer(buyer);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemDTO item : cartItems) {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Продукт не е намерен"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        orderRepo.save(order); // <-- save ще попълни и ID-то

        cartClient.clearCart(userEmail);

        return order; // <-- ВРЪЩАМЕ Order
    }

    public List<Order> getOrdersByUser(String userEmail) {
        return orderRepo.findAllByBuyerEmailWithItemsAndProducts(userEmail);
    }

}
