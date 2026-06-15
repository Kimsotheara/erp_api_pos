package com.theara.erp.dto.response;

import com.theara.erp.constant.KitchenStatus;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenTicketItemResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private BigDecimal quantity;
    private String note;
    private KitchenStatus status;
}
