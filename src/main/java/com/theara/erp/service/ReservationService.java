package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ReservationRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.ReservationResponse;
import com.theara.erp.entity.Reservation;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse getReservationById(Long id);
    ReservationResponse updateReservation(Long id, ReservationRequest request);
    PageAbleResponse<Reservation, ReservationResponse, Void> getReservations(PageAbleRequest<Void> request);
    void deleteReservation(Long id);
}
