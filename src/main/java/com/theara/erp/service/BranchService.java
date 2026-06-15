package com.theara.erp.service;

import com.theara.erp.dto.request.BranchRequest;
import com.theara.erp.dto.request.PageAbleRequest;
import com.theara.erp.dto.response.BranchResponse;
import com.theara.erp.dto.response.PageAbleResponse;
import com.theara.erp.entity.Branch;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    BranchResponse getBranchById(Long id);
    BranchResponse updateBranch(Long id, BranchRequest request);
    PageAbleResponse<Branch, BranchResponse, Void> getBranches(PageAbleRequest<Void> request);
    void deleteBranch(Long id);
}
