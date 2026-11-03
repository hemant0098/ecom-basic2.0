
package com.dharshi.cartservice.services;

import com.dharshi.cartservice.dtos.*;
import com.dharshi.cartservice.exceptions.ResourceNotFoundException;
import com.dharshi.cartservice.exceptions.ServiceLogicException;
import com.dharshi.cartservice.feigns.ProductService;
import com.dharshi.cartservice.feigns.UserService;
import com.dharshi.cartservice.modals.Cart;
import com.dharshi.cartservice.modals.CartItem;
import com.dharshi.cartservice.repositories.CartRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // =====================================================
    // ADD ITEM TO CART
    // =====================================================

    @Override
    public ResponseEntity<ApiResponseDto<?>> addItemToCart(
            String userId,
            CartItemRequestDto requestDto
    ) throws ResourceNotFoundException, ServiceLogicException {

        try {

            if (requestDto == null) {
                throw new IllegalArgumentException("Cart item request cannot be null");
            }

            // Validate user
            var userResponse = userService.existsUserById(userId);

            if (userResponse == null
                    || userResponse.getBody() == null
                    || !Boolean.TRUE.equals(
                            userResponse.getBody().getResponse()
                    )) {

                throw new ResourceNotFoundException(
                        "User not found with id " + userId
                );
            }

            // Validate product
            var productResponse = productService.getProductById(
                    requestDto.getProductId()
            );

            if (productResponse == null
                    || productResponse.getBody() == null
                    || productResponse.getBody().getResponse() == null) {

                throw new ResourceNotFoundException(
                        "Product not found with id "
                                + requestDto.getProductId()
                );
            }

            // Validate quantity
            if (requestDto.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero"
                );
            }

            // Get existing cart or create new cart
            Cart userCart = getCart(userId);

            Set<CartItem> cartItems = userCart.getCartItems();

            if (cartItems == null) {
                cartItems = new HashSet<>();
            }

            // Check whether product already exists in cart
            CartItem existingItem = getExistingCartItem(
                    cartItems,
                    requestDto.getProductId()
            );

            if (existingItem == null) {

                CartItem newItem = CartItem.builder()
                        .productId(requestDto.getProductId())
                        .quantity(requestDto.getQuantity())
                        .build();

                cartItems.add(newItem);

            } else {

                existingItem.setQuantity(
                        existingItem.getQuantity()
                                + requestDto.getQuantity()
                );
            }

            userCart.setCartItems(cartItems);

            cartRepository.save(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Item successfully added to cart!")
                            .build()
            );

        } catch (ResourceNotFoundException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Failed to add item to cart for userId: {}",
                    userId,
                    e
            );

            throw new ServiceLogicException(
                    "Unable to add item to cart!"
            );
        }
    }

    // =====================================================
    // GET CART ITEMS BY USER
    // =====================================================

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCartItemsByUser(
            String userId
    ) throws ResourceNotFoundException, ServiceLogicException {

        try {

            var userResponse = userService.existsUserById(userId);

            if (userResponse == null
                    || userResponse.getBody() == null
                    || !Boolean.TRUE.equals(
                            userResponse.getBody().getResponse()
                    )) {

                throw new ResourceNotFoundException(
                        "User not found with id " + userId
                );
            }

            Cart userCart = getCart(userId);

            CartResponseDto cartResponse =
                    cartToCartResponseDto(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(cartResponse)
                            .build()
            );

        } catch (ResourceNotFoundException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Failed to find cart for userId: {}",
                    userId,
                    e
            );

            throw new ServiceLogicException(
                    "Unable to find cart!"
            );
        }
    }

    // =====================================================
    // REMOVE ITEM FROM CART
    // =====================================================

    @Override
    public ResponseEntity<ApiResponseDto<?>> removeCartItemFromCart(
            String userId,
            String productId
    ) throws ServiceLogicException, ResourceNotFoundException {

        try {

            Cart userCart = cartRepository.findByUserId(userId);

            if (userCart == null) {
                throw new ResourceNotFoundException(
                        "No cart found for user " + userId
                );
            }

            Set<CartItem> cartItems = userCart.getCartItems();

            if (cartItems == null) {
                cartItems = new HashSet<>();
            }

            Set<CartItem> updatedItems =
                    removeCartItem(cartItems, productId);

            userCart.setCartItems(updatedItems);

            cartRepository.save(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Item successfully removed from cart!")
                            .build()
            );

        } catch (ResourceNotFoundException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Failed to remove cart item for userId: {}, productId: {}",
                    userId,
                    productId,
                    e
            );

            throw new ServiceLogicException(
                    "Unable to remove item from cart!"
            );
        }
    }

    // =====================================================
    // CLEAR CART
    // =====================================================

    @Override
    public ResponseEntity<ApiResponseDto<?>> clearCartById(
            String id
    ) throws ServiceLogicException, ResourceNotFoundException {

        try {

            Cart userCart = cartRepository.findById(id).orElse(null);

            if (userCart == null) {
                throw new ResourceNotFoundException(
                        "No cart found for id " + id
                );
            }

            userCart.setCartItems(new HashSet<>());

            cartRepository.save(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Cart has been successfully cleared!")
                            .build()
            );

        } catch (ResourceNotFoundException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Failed to clear cart with id: {}",
                    id,
                    e
            );

            throw new ServiceLogicException(
                    "Unable to clear cart!"
            );
        }
    }

    // =====================================================
    // GET CART BY ID
    // =====================================================

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCartById(
            String id
    ) throws ServiceLogicException {

        try {

            Cart cart = cartRepository.findById(id).orElse(null);

            if (cart == null) {
                throw new ResourceNotFoundException(
                        "Cart not found with id " + id
                );
            }

            CartResponseDto cartResponse =
                    cartToCartResponseDto(cart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Cart received successfully!")
                            .response(cartResponse)
                            .build()
            );

        } catch (Exception e) {

            log.error(
                    "Failed to find cart with id: {}",
                    id,
                    e
            );

            throw new ServiceLogicException(
                    "Unable to find cart!"
            );
        }
    }

    // =====================================================
    // CREATE NEW CART
    // =====================================================

    private void createAndSaveNewCart(String userId) {

        if (!cartRepository.existsByUserId(userId)) {

            Cart cart = Cart.builder()
                    .userId(userId)
                    .cartItems(new HashSet<>())
                    .build();

            cartRepository.insert(cart);

            log.info("New cart created for userId: {}", userId);
        }
    }

    // =====================================================
    // GET OR CREATE CART
    // =====================================================

    private Cart getCart(String userId) {

        createAndSaveNewCart(userId);

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new IllegalStateException(
                    "Cart could not be created or retrieved for userId: "
                            + userId
            );
        }

        if (cart.getCartItems() == null) {
            cart.setCartItems(new HashSet<>());
        }

        return cart;
    }

    // =====================================================
    // GET EXISTING CART ITEM
    // =====================================================

    private CartItem getExistingCartItem(
            Set<CartItem> cartItems,
            String productId
    ) {

        if (cartItems == null || cartItems.isEmpty()) {
            return null;
        }

        return cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    // =====================================================
    // CONVERT CART TO CART RESPONSE DTO
    // =====================================================

    private CartResponseDto cartToCartResponseDto(Cart userCart) {

        if (userCart == null) {
            throw new IllegalStateException("Cart cannot be null");
        }

        int noOfCartItems = 0;
        double subtotal = 0.0;

        Set<CartItemResponseDto> cartItems = new HashSet<>();

        if (userCart.getCartItems() != null) {

            for (CartItem cartItem : userCart.getCartItems()) {

                CartItemResponseDto cartItemResponse =
                        cartItemToCartItemResponseDto(cartItem);

                noOfCartItems += cartItemResponse.getQuantity();

                subtotal += cartItemResponse.getAmount();

                cartItems.add(cartItemResponse);
            }
        }

        return CartResponseDto.builder()
                .cartId(userCart.getId())
                .userId(userCart.getUserId())
                .cartItems(cartItems)
                .noOfCartItems(noOfCartItems)
                .subtotal(subtotal)
                .build();
    }

    // =====================================================
    // CONVERT CART ITEM TO RESPONSE DTO
    // =====================================================

    private CartItemResponseDto cartItemToCartItemResponseDto(
            CartItem cartItem
    ) {

        var productResponse =
                productService.getProductById(cartItem.getProductId());

        if (productResponse == null
                || productResponse.getBody() == null
                || productResponse.getBody().getResponse() == null) {

            throw new IllegalStateException(
                    "Product not found with id: " + cartItem.getProductId()
            );
        }

        ProductDto product =
                productResponse.getBody().getResponse();

        return CartItemResponseDto.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .quantity(cartItem.getQuantity())
                .categoryName(product.getCategoryName())
                .imageUrl(product.getImageUrl())
                .amount(product.getPrice() * cartItem.getQuantity())
                .build();
    }

    // =====================================================
    // REMOVE CART ITEM FROM SET
    // =====================================================

    private Set<CartItem> removeCartItem(
            Set<CartItem> cartItems,
            String productId
    ) {

        if (cartItems == null || cartItems.isEmpty()) {
            return new HashSet<>();
        }

        cartItems.removeIf(
                item -> item.getProductId().equals(productId)
        );

        return cartItems;
    }
}
