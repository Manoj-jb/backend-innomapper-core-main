package com.innowell.core.features.site.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SitePagination<T> {
    private List<T> content;
    private int pageNo;
    private int pageSize;
    private Long totalElements;
    private int totalPages;

    public int getPageNo() {
        return pageNo + 1;
    }
}
