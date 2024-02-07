package com.example.triptix.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseObject<T> {
    private int code;
    private boolean status;
    private String message;
    private int pageSize;
    private int pageIndex;
    private int totalPage;
    private T data;
}
