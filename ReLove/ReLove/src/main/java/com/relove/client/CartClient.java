package com.relove.client;

import com.relove.model.dto.CartItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CartClient {
    private final RestTemplate restTemplate;
    private final String cartServiceBaseUrl;

    public CartClient(RestTemplate restTemplate, @Value("${cart.service.url}") String cartServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.cartServiceBaseUrl = cartServiceBaseUrl;
    }

    public void addToCart(CartItemDTO item) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CartItemDTO> request = new HttpEntity<>(item, headers);
        restTemplate.postForEntity(cartServiceBaseUrl + "/api/cart", request, Void.class);
    }

    public List<CartItemDTO> getCart(String email) {
        ResponseEntity<CartItemDTO[]> response = restTemplate.getForEntity(
                cartServiceBaseUrl + "/api/cart/" + email, CartItemDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public void removeFromCart(String email, Long productId) {
        restTemplate.delete(cartServiceBaseUrl + "/api/cart/" + email + "/" + productId);
    }

    public void updateQuantity(CartItemDTO dto) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CartItemDTO> request = new HttpEntity<>(dto, headers);

        restTemplate.exchange(
                cartServiceBaseUrl + "/api/cart", // важно: това е микросървисът
                HttpMethod.PUT,
                request,
                Void.class
        );
    }


    public void clearCart(String email) {
        restTemplate.delete(cartServiceBaseUrl + "/api/cart/" + email);
    }


}
