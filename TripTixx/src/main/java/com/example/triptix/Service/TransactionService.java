package com.example.triptix.Service;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Transaction.TransactionDTocreate;

public interface TransactionService {
    ResponseObject<?> getAllOfCustomer(int idCustomer, int pageSize, int pageIndex);
    ResponseObject<?> getDetail(int id);
    ResponseObject<?> create(String description, int amoount, int walletId, String bankCode);

}
