package com.theara.erp.repository;

import com.theara.erp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByCompanyIdAndUsernameIgnoreCase(Long companyId, String username);
    boolean existsByCompanyIdAndUsernameIgnoreCaseAndIdNot(Long companyId, String username, Long id);
    Optional<User> findByCompanyIdAndUsernameIgnoreCase(Long companyId, String username);
}
