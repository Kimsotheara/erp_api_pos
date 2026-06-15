package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.constant.KitchenStatus;
import com.theara.erp.dto.request.KitchenTicketItemRequest;
import com.theara.erp.dto.request.KitchenTicketRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.KitchenTicketResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.KitchenTicket;
import com.theara.erp.entity.KitchenTicketItem;
import com.theara.erp.mapper.KitchenTicketMapper;
import com.theara.erp.repository.InvoiceRepository;
import com.theara.erp.repository.KitchenTicketRepository;
import com.theara.erp.repository.MenuItemRepository;
import com.theara.erp.repository.RestaurantTableRepository;
import com.theara.erp.service.KitchenTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class KitchenTicketServiceImpl implements KitchenTicketService {

    private final KitchenTicketRepository kitchenTicketRepository;
    private final InvoiceRepository invoiceRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final MenuItemRepository menuItemRepository;
    private final KitchenTicketMapper kitchenTicketMapper;

    @Override @Transactional
    public KitchenTicketResponse createTicket(KitchenTicketRequest request) {
        KitchenTicket ticket = KitchenTicket.builder()
                .invoice(request.getInvoiceId() == null ? null
                        : invoiceRepository.findById(request.getInvoiceId()).orElseThrow(() -> notFound("Invoice")))
                .table(request.getTableId() == null ? null
                        : restaurantTableRepository.findById(request.getTableId()).orElseThrow(() -> notFound("RestaurantTable")))
                .ticketNumber(generateTicketNumber())
                .status(KitchenStatus.NEW)
                .build();

        for (KitchenTicketItemRequest r : request.getItems()) {
            ticket.addItem(KitchenTicketItem.builder()
                    .menuItem(r.getMenuItemId() == null ? null
                            : menuItemRepository.findById(r.getMenuItemId()).orElseThrow(() -> notFound("MenuItem")))
                    .quantity(r.getQuantity() != null ? r.getQuantity() : BigDecimal.ONE)
                    .note(r.getNote())
                    .status(KitchenStatus.NEW)
                    .build());
        }
        return kitchenTicketMapper.toResponse(kitchenTicketRepository.save(ticket));
    }

    @Override @Transactional(readOnly = true)
    public KitchenTicketResponse getTicketById(Long id) {
        return kitchenTicketMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public KitchenTicketResponse updateStatus(Long id, KitchenStatus status) {
        KitchenTicket ticket = findById(id);
        ticket.setStatus(status);
        // Cascade the status to the lines and stamp served time when the ticket is served.
        ticket.getItems().forEach(item -> item.setStatus(status));
        if (status == KitchenStatus.SERVED) {
            ticket.setServedAt(LocalDateTime.now());
        }
        return kitchenTicketMapper.toResponse(kitchenTicketRepository.save(ticket));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<KitchenTicket, KitchenTicketResponse, Void> getTickets(PageAbleRequest<Void> request) {
        Page<KitchenTicket> page = kitchenTicketRepository.findAll(request.getPageAble());
        List<KitchenTicketResponse> list = page.getContent().stream().map(kitchenTicketMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    private String generateTicketNumber() {
        long seq = kitchenTicketRepository.count() + 1;
        String candidate = String.format("KOT-%06d", seq);
        while (kitchenTicketRepository.existsByTicketNumber(candidate)) {
            seq++;
            candidate = String.format("KOT-%06d", seq);
        }
        return candidate;
    }

    private KitchenTicket findById(Long id) {
        return kitchenTicketRepository.findById(id).orElseThrow(() -> notFound("KitchenTicket"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
