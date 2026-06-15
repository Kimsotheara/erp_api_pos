package com.theara.erp.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SupplierContactResponse {
    private Long id;
    private String name;
    private String position;
    private String phone;
    private String email;
}
