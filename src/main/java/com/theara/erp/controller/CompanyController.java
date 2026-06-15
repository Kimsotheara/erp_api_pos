package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CompanyRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
@Tag(name = "Company", description = "Organization — company management")
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "List companies", description = "Paginated list of companies.")
    @ApiResponse(responseCode = "200", description = "List retrieved")
    @GetMapping
    public ResponseEntity<?> getCompanies(PageAbleRequest<Void> request) {
        return DefaultResponse.withCode(companyService.getCompanies(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Get company by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        return DefaultResponse.withCode(companyService.getCompanyById(id), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Create company")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Name already exists")
    })
    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyRequest request) {
        return DefaultResponse.withCode(companyService.createCompany(request), ErrorCode.CREATED);
    }

    @Operation(summary = "Update company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "409", description = "Name already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        return DefaultResponse.withCode(companyService.updateCompany(id, request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Delete company", description = "Soft-deletes a company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return DefaultResponse.withCode(null, ErrorCode.SUCCESS);
    }
}
