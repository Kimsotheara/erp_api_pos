package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.ReservationRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.ReservationResponse;
import com.theara.erp.entity.Reservation;
import com.theara.erp.mapper.ReservationMapper;
import com.theara.erp.repository.CustomerRepository;
import com.theara.erp.repository.ReservationRepository;
import com.theara.erp.repository.RestaurantTableRepository;
import com.theara.erp.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final CustomerRepository customerRepository;
    private final ReservationMapper reservationMapper;

    @Override @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Reservation reservation = new Reservation();
        apply(reservation, request);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        return reservationMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        Reservation reservation = findById(id);
        apply(reservation, request);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Reservation, ReservationResponse, Void> getReservations(PageAbleRequest<Void> request) {
        Page<Reservation> page = reservationRepository.findAll(request.getPageAble());
        List<ReservationResponse> list = page.getContent().stream().map(reservationMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.delete(findById(id));
    }

    private void apply(Reservation res, ReservationRequest r) {
        res.setTable(restaurantTableRepository.findById(r.getTableId()).orElseThrow(() -> notFound("RestaurantTable")));
        res.setCustomer(r.getCustomerId() == null ? null
                : customerRepository.findById(r.getCustomerId()).orElseThrow(() -> notFound("Customer")));
        res.setCustomerName(r.getCustomerName());
        res.setPhone(r.getPhone());
        if (r.getPartySize() != null) res.setPartySize(r.getPartySize());
        res.setReservedFrom(r.getReservedFrom());
        res.setReservedTo(r.getReservedTo());
        res.setNote(r.getNote());
    }

    private Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> notFound("Reservation"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
