package com.theara.erp.constant;

/**
 * Drives which industry-specific modules are enabled for a company.
 * Mirrors the {@code business_type} enum in erp_schema.sql.
 */
public enum BusinessType {
    MINI_MART,
    PHARMACY,
    CONVENIENCE,
    PUB,
    COFFEE_SHOP,
    RESTAURANT,
    RETAIL
}
