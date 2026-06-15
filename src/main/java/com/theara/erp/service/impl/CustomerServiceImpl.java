package com.theara.erp.service.impl;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.CustomerRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.CustomerResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Customer;
import com.theara.erp.mapper.CustomerMapper;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.CustomerRepository;
import com.theara.erp.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final CustomerMapper customerMapper;

    @Override @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        apply(customer, request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        return customerMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = findById(id);
        apply(customer, request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override @Transactional(readOnly = true)
    public PageAbleResponse<Customer, CustomerResponse, Void> getCustomers(PageAbleRequest<Void> request) {
        Page<Customer> page = customerRepository.findAll(request.getPageAble());
        List<CustomerResponse> list = page.getContent().stream().map(customerMapper::toResponse).toList();
        return new PageAbleResponse<>(page, list);
    }

    @Override @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = findById(id);
        customer.setIsDeleted(1);
        customerRepository.save(customer);
    }

    private void apply(Customer c, CustomerRequest r) {
        Company company = companyRepository.findById(r.getCompanyId())
                .orElseThrow(() -> notFound("Company"));
        c.setCompany(company);
        c.setCode(r.getCode());
        c.setName(r.getName());
        c.setPhone(r.getPhone());
        c.setEmail(r.getEmail());
        c.setAddress(r.getAddress());
        c.setMembershipNo(r.getMembershipNo());
        c.setMembershipTier(r.getMembershipTier());
        if (r.getLoyaltyBalance() != null) c.setLoyaltyBalance(r.getLoyaltyBalance());
        if (r.getIsActive() != null) c.setIsActive(r.getIsActive());
    }

    private Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> notFound("Customer"));
    }

    private ResponseStatusException notFound(String e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, e + " " + ErrorCode.NOT_FOUND.getDescription());
    }
}
