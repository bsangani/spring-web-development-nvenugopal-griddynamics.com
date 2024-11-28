package com.gd.ecom.controller;

import com.gd.ecom.records.ErrorResponse;
import com.gd.ecom.records.OrderApiResponse;
import com.gd.ecom.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Check out the cart and create an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order checked out successfully",
                    content = @Content(schema = @Schema(implementation = OrderApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empty cart",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/checkOutCart")
    public ResponseEntity<OrderApiResponse> checkOutOrder(HttpSession session) {
        Long id = orderService.checkOutOrder(session.getId());
        OrderApiResponse orderResponse = new OrderApiResponse(HttpStatus.OK.value(), id);
        return ResponseEntity.ok().body(orderResponse);
    }
}
