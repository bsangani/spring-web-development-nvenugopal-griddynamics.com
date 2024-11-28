package com.gd.ecom.controller;

import com.gd.ecom.model.CartItemDto;
import com.gd.ecom.model.CartRequest;
import com.gd.ecom.records.CartApiResponse;
import com.gd.ecom.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add an item to the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added to cart successfully",
                    content = @Content(schema = @Schema(implementation = CartApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest cartRequest, HttpSession session) {
        cartService.addToCart(cartRequest, session.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update cart item information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item updated successfully",
                    content = @Content(schema = @Schema(implementation = CartApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Cart item not found"),
    })
    @PutMapping("/updateCart")
    public ResponseEntity<CartApiResponse> updateCartInformation(@Valid @RequestBody CartRequest cartRequest, HttpSession session) {
        Long updatedId = cartService.updateCartById(cartRequest, session.getId());
        CartApiResponse response = new CartApiResponse(HttpStatus.OK.value(), updatedId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Retrieve all cart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CartItemDto.class))),
            @ApiResponse(responseCode = "404", description = "Cart is empty"),
            @ApiResponse(responseCode = "404", description = "Cart item not found"),
    })
    @GetMapping("/getCartItems")
    public ResponseEntity<List<CartItemDto>> getCartItems(HttpSession session) {
        List<CartItemDto> cartItems = cartService.getAllCartItems(session.getId());
        return ResponseEntity.ok(cartItems);
    }

    @Operation(summary = "Delete a cart item by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item deleted successfully",
                    content = @Content(schema = @Schema(implementation = CartApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Cart is empty"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/cart/{id}")
    public ResponseEntity<CartApiResponse> deleteCartItemById(@PathVariable("id") Long id, HttpSession session) {
        Long deletedId = cartService.deleteCartItemById(id, session.getId());
        CartApiResponse response = new CartApiResponse(HttpStatus.OK.value(), deletedId);
        return ResponseEntity.ok(response);
    }
}
