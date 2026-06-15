package com.theara.erp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PageAbleResponse<T, D, S> {

    public PageAbleResponse(Page<T> page, List<D> list) {
        this(page, list, null);
    }

    public PageAbleResponse(Page<T> page, List<D> list, S addition) {
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.pageNumber = page.getNumber();
        this.last = page.isLast();
        this.row = list.size();
        this.list = list;
        this.addition = addition;

        for (Sort.Order order : page.getSort()) {
            this.sortProperty = order.getProperty();
            this.sortDirection = order.getDirection().isDescending()
                    ? Sort.Direction.DESC.name() : Sort.Direction.ASC.name();
        }
    }

    public static <T, D> PageAbleResponse<T, D, Void> withoutAddition(Page<T> page, List<D> list) {
        return new PageAbleResponse<>(page, list);
    }

    public static <T, D, S> PageAbleResponse<T, D, S> withAddition(Page<T> page, List<D> list, S statics) {
        return new PageAbleResponse<>(page, list, statics);
    }

    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
    private int pageNumber;
    private int row;
    private String sortProperty;
    private String sortDirection;
    private List<D> list = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private S addition;

    public int getPageNumber() {
        return pageNumber + 1;
    }
}
