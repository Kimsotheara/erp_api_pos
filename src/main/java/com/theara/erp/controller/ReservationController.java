package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ReservationRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "Pub/Restaurant: table reservations")
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "List reservations")
    @GetMapping
    public ResponseEntity<?> getReservations(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(reservationService.getReservations(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get reservation by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return DefaultResponse.withCode(reservationService.getReservationById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create reservation")
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request) {
        return DefaultResponse.withCode(reservationService.createReservation(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update reservation")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationRequest request) {
        return DefaultResponse.withCode(reservationService.updateReservation(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete reservation")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
