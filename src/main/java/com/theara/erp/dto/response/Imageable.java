package com.theara.erp.dto.response;

/**
 * Implemented by response DTOs that carry a (potentially large) base64 {@code image}.
 * List endpoints use {@link com.theara.erp.common.PageMapper#toResponseWithoutImage}
 * to drop the image so only {@code getById} ships the full payload.
 */
public interface Imageable {
    void setImage(String image);
}
