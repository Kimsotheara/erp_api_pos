package com.theara.erp.common;

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
}
