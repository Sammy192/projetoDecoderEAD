package com.ead.authuser.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePageDTO<T> extends PageImpl<T> {

    private final PageMetadata page;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ResponsePageDTO(@JsonProperty("content") List<T> content,
                           @JsonProperty("page") PageMetadata page) {
        super(content, PageRequest.of(page.getNumber(), page.getSize()), page.getTotalElements());
        this.page = page;
    }

    public PageMetadata getPage() {
        return page;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageMetadata {
        private int size;
        private long totalElements;
        private int totalPages;
        private int number;

        @JsonCreator
       public PageMetadata(@JsonProperty("size") int size,
                           @JsonProperty("totalElements") long totalElements,
                           @JsonProperty("totalPages") int totalPages,
                           @JsonProperty("number") int number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }

        public int getSize() {
            return size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getNumber() {
            return number;
        }
    }
}
