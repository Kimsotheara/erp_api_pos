package com.theara.erp.repository;

import com.theara.erp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByCompanyIdAndNameIgnoreCase(Long companyId, String name);
    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(Long companyId, String name, Long id);
    List<Role> findByIdIn(List<Long> ids);
}
