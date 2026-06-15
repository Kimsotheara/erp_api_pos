package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.request.PrescriptionRequest;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.dto.response.PrescriptionResponse;
import com.theara.erp.entity.Prescription;
import com.theara.erp.mapper.PrescriptionMapper;
import com.theara.erp.repository.CustomerRepository;
import com.theara.erp.repository.InvoiceRepository;
import com.theara.erp.repository.PrescriptionRepository;
import com.theara.erp.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Override @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        Prescription prescription = new Prescription();
        apply(prescription, request);
        return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
    }

    @Override @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
        return prescriptionMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {
        Prescription prescription = findById(id);
        apply(prescription, request);
        return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Prescription, PrescriptionResponse, Void> getPrescriptions(PageAbleRequest<Void> request) {
        Page<Prescription> page = prescriptionRepository.findAll(request.getPageAble());
        List<PrescriptionResponse> list = page.getContent().stream().map(prescriptionMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deletePrescription(Long id) {
        prescriptionRepository.delete(findById(id));
    }

    private void apply(Prescription p, PrescriptionRequest r) {
        p.setInvoice(r.getInvoiceId() == null ? null
                : invoiceRepository.findById(r.getInvoiceId()).orElseThrow(() -> notFound("Invoice")));
        p.setCustomer(r.getCustomerId() == null ? null
                : customerRepository.findById(r.getCustomerId()).orElseThrow(() -> notFound("Customer")));
        p.setDoctorName(r.getDoctorName());
        p.setDoctorLicense(r.getDoctorLicense());
        p.setNotes(r.getNotes());
        p.setIssuedDate(r.getIssuedDate());
    }

    private Prescription findById(Long id) {
        return prescriptionRepository.findById(id).orElseThrow(() -> notFound("Prescription"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
