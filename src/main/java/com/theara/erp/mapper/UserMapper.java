package com.theara.erp.mapper;

import com.theara.erp.dto.response.RoleSummaryResponse;
import com.theara.erp.dto.response.UserResponse;
import com.theara.erp.entity.Branch;
import com.theara.erp.entity.Role;
import com.theara.erp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Entity -> Response only; the password hash is never exposed. */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "defaultBranchId", source = "defaultBranch.id")
    @Mapping(target = "branchIds", source = "branches")
    UserResponse toResponse(User user);

    RoleSummaryResponse toSummary(Role role);

    /** Used by MapStruct to turn each Branch in the set into its id. */
    default Long branchId(Branch branch) {
        return branch == null ? null : branch.getId();
    }
}
