package com.theara.erp.service;

import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PrescriptionRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PrescriptionResponse;
import com.theara.erp.entity.Prescription;

public interface PrescriptionService {
    PrescriptionResponse createPrescription(PrescriptionRequest request);
    PrescriptionResponse getPrescriptionById(Long id);
    PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request);
    PageAbleResponse<Prescription, PrescriptionResponse, Void> getPrescriptions(PageAbleRequest<Void> request);
    void deletePrescription(Long id);
}
