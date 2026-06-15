package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.KitchenStatusRequest;
import com.theara.erp.dto.request.KitchenTicketRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.KitchenTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kitchen-tickets")
@Tag(name = "Kitchen Ticket", description = "Pub/Restaurant: kitchen order tickets (KOT) workflow")
@Slf4j
public class KitchenTicketController {
    private final KitchenTicketService kitchenTicketService;

    @Operation(summary = "List kitchen tickets")
    @GetMapping
    public ResponseEntity<?> getTickets(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(kitchenTicketService.getTickets(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get kitchen ticket by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        return DefaultResponse.withCode(kitchenTicketService.getTicketById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create kitchen ticket", description = "Sends an order to the kitchen (status NEW).")
    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody KitchenTicketRequest request) {
        return DefaultResponse.withCode(kitchenTicketService.createTicket(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update kitchen ticket status", description = "NEW -> PREPARING -> READY -> SERVED (or CANCELLED).")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody KitchenStatusRequest request) {
        return DefaultResponse.withCode(kitchenTicketService.updateStatus(id, request.getStatus()), ErrorCode.SUCCESS);
    }
}
