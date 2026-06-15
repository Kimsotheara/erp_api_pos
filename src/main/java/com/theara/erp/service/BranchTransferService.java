package com.theara.erp.service;

import com.theara.erp.dto.request.BranchTransferRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.BranchTransferResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.BranchTransfer;

public interface BranchTransferService {
    BranchTransferResponse createTransfer(BranchTransferRequest request);
    BranchTransferResponse getTransferById(Long id);
    BranchTransferResponse shipTransfer(Long id);
    BranchTransferResponse receiveTransfer(Long id);
    BranchTransferResponse cancelTransfer(Long id);
    PageAbleResponse<BranchTransfer, BranchTransferResponse, Void> getTransfers(PageAbleRequest<Void> request);
    void deleteTransfer(Long id);
}
