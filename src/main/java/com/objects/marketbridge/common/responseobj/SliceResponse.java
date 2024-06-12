package com.objects.marketbridge.common.responseobj;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class SliceResponse<T> {
    private final List<T> content;
    private final SortResponse sort;
    private final int currentPage;
    private final int size;
    private final boolean first;
    private final boolean last;


    public SliceResponse(Slice<T> sliceContent) {
        this.content = sliceContent.getContent();
        this.sort = new SortResponse(sliceContent.getSort());
        this.currentPage = sliceContent.getPageable().getPageNumber();
        this.size = sliceContent.getSize();
        this.first = sliceContent.isFirst();
        this.last = sliceContent.isLast();
    }

    @Getter
    @NoArgsConstructor
    public static class SortResponse{
        private boolean sorted;
        private String direction;
        private String orderProperty;

        public SortResponse(Sort sort) {
            this.sorted = sort.isSorted();
            this.direction = sort.stream().map(o -> o.getDirection().toString()).collect(Collectors.joining(","));
            this.orderProperty = sort.stream().map(Sort.Order::getProperty).collect(Collectors.joining(", "));
        }
    }
}
