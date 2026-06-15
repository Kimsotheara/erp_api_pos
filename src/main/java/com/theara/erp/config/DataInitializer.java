package com.theara.erp.config;

import com.theara.erp.constant.BusinessType;
import com.theara.erp.entity.Company;
import com.theara.erp.entity.Role;
import com.theara.erp.entity.User;
import com.theara.erp.repository.CompanyRepository;
import com.theara.erp.repository.RoleRepository;
import com.theara.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final String DEFAULT_COMPANY = "Default Company";
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${erp.security.seed-superadmin:true}")
    private boolean seedSuperadmin;

    @Value("${erp.security.superadmin-username:superadmin}")
    private String superadminUsername;

    @Value("${erp.security.superadmin-password:123456}")
    private String superadminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedSuperadmin) {
            return;
        }

        Company company = companyRepository.findFirstByNameIgnoreCase(DEFAULT_COMPANY)
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setName(DEFAULT_COMPANY);
                    c.setBusinessType(BusinessType.MINI_MART);
                    c.setIsActive(true);
                    log.info("Seeding default company '{}'", DEFAULT_COMPANY);
                    return companyRepository.save(c);
                });

        Role superAdmin = roleRepository.findFirstByCompanyIdAndNameIgnoreCase(company.getId(), SUPER_ADMIN_ROLE)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setCompany(company);
                    r.setName(SUPER_ADMIN_ROLE);
                    r.setDescription("Full system access");
                    r.setIsSystem(true);
                    log.info("Seeding role '{}'", SUPER_ADMIN_ROLE);
                    return roleRepository.save(r);
                });

        if (userRepository.findByCompanyIdAndUsernameIgnoreCase(company.getId(), superadminUsername).isEmpty()) {
            User user = new User();
            user.setCompany(company);
            user.setUsername(superadminUsername);
            user.setFullName("Super Administrator");
            user.setPasswordHash(passwordEncoder.encode(superadminPassword));
            user.setIsActive(true);
            user.getRoles().add(superAdmin);
            userRepository.save(user);
            log.info("Seeded superadmin user '{}' (company id {})", superadminUsername, company.getId());
        }
    }
}
