package com.theara.erp.common;

import com.theara.erp.dto.response.Imageable;
import com.theara.erp.dto.response.PageAbleResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Turns a {@link Page} of entities into the project's {@link PageAbleResponse},
 * replacing the repeated {@code page.getContent().stream().map(...).toList()}
 * boilerplate found in every list endpoint.
 */
public final class PageMapper {

    private PageMapper() {
    }

    public static <E, D> PageAbleResponse<E, D, Void> toResponse(Page<E> page, Function<E, D> mapper) {
        List<D> list = page.getContent().stream().map(mapper).toList();
        return new PageAbleResponse<>(page, list);
    }

    /**
     * Same as {@link #toResponse} but strips the (heavy, base64) image from each row.
     * Use on list endpoints so the image is only returned by {@code getById}.
     */
    public static <E, D extends Imageable> PageAbleResponse<E, D, Void> toResponseWithoutImage(
            Page<E> page, Function<E, D> mapper) {
        List<D> list = page.getContent().stream().map(mapper).toList();
        list.forEach(d -> {
            if (d != null) {
                d.setImage(null);
            }
        });
        return new PageAbleResponse<>(page, list);
    }
}
