package com.theara.erp.service;

import com.theara.erp.dto.request.ManufacturerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.ManufacturerResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Manufacturer;

public interface ManufacturerService {
    ManufacturerResponse createManufacturer(ManufacturerRequest request);
    ManufacturerResponse getManufacturerById(Long id);
    ManufacturerResponse updateManufacturer(Long id, ManufacturerRequest request);
    PageAbleResponse<Manufacturer, ManufacturerResponse, Void> getManufacturers(PageAbleRequest<Void> request);
    void deleteManufacturer(Long id);
}
