package com.theara.erp.service;

import com.theara.erp.dto.request.CustomerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CustomerResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Customer;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    PageAbleResponse<Customer, CustomerResponse, Void> getCustomers(PageAbleRequest<Void> request);
    void deleteCustomer(Long id);
}
