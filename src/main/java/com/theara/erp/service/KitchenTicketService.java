package com.theara.erp.service;

import com.theara.erp.constant.KitchenStatus;
import com.theara.erp.dto.request.KitchenTicketRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.KitchenTicketResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.KitchenTicket;

public interface KitchenTicketService {
    KitchenTicketResponse createTicket(KitchenTicketRequest request);
    KitchenTicketResponse getTicketById(Long id);
    KitchenTicketResponse updateStatus(Long id, KitchenStatus status);
    PageAbleResponse<KitchenTicket, KitchenTicketResponse, Void> getTickets(PageAbleRequest<Void> request);
}
