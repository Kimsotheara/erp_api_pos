package com.theara.erp.config;

import com.theara.erp.constant.BusinessType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "erp")
public class ErpProperties {

//    Active business type for this deployment.
    private BusinessType businessType = BusinessType.MINI_MART;

    public boolean isPharmacyEnabled() {
        return businessType == BusinessType.PHARMACY;
    }

    public boolean isPubEnabled() {
        return businessType == BusinessType.PUB;
    }

    public boolean isMiniMartEnabled() {
        return businessType == BusinessType.MINI_MART;
    }

    public boolean isConvenienceEnabled() {
        return businessType == BusinessType.CONVENIENCE;
    }

    public boolean isCoffeeShopEnabled() {
        return businessType == BusinessType.COFFEE_SHOP;
    }

    public boolean isRestaurantEnabled() {
        return businessType == BusinessType.RESTAURANT;
    }

    public boolean isRetailEnabled() {
        return businessType == BusinessType.RETAIL;
    }

    /** Generic check, handy when several types share a feature. */
    public boolean is(BusinessType type) {
        return businessType == type;
    }
}
