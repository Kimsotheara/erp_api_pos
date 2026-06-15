package com.theara.erp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Lightweight sanity test that does NOT boot the Spring context
 * (full context needs a running PostgreSQL). Replace with @SpringBootTest
 * once a test datasource (e.g. Testcontainers / H2) is configured.
 */
class ErpPosApiApplicationTests {

    @Test
    void sanity() {
        assertTrue(true);
    }
}
