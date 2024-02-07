package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.Transaction.TransactionDTOview;
import com.example.triptix.DTO.Transaction.TransactionDTocreate;
import com.example.triptix.Model.Transaction;
import com.example.triptix.Model.UserSystem;
import com.example.triptix.Model.Wallet;
import com.example.triptix.Repository.TransactionRepo;
import com.example.triptix.Repository.UserSystemRepo;
import com.example.triptix.Repository.WalletRepo;
import com.example.triptix.Service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private UserSystemRepo userSystemRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseObject<?> getAllOfCustomer(int idCustomer, int pageSize, int pageIndex) {
        try {
            //check id customer
            UserSystem obj = userSystemRepo.findById(idCustomer).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found user with id "+ idCustomer).build();
            }
            //paging
            Pageable pageable = null;
            if(pageSize != 0 && pageIndex != 0){
                pageable = PageRequest.of(pageIndex - 1, pageSize);   //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            }else{ //get all
                pageIndex = 1;
            }
            List<Transaction> objList = transactionRepo.findAllByCustomerId(idCustomer, pageable);
            List<TransactionDTOview> dtoList = new ArrayList<>();
            TransactionDTOview dto = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Transaction item : objList) {
                dto = modelMapper.map(item, TransactionDTOview.class);
                dto.setDateTimeStamp(item.getDate().getTime()/1000);
                if(dto.getDescription().contains("Thanh toan")){
                    dto.setDescription("Nạp tiền từ " + item.getBankCode());
                }
                dtoList.add(dto);
            }
            return ResponseObject.builder().status(true).message("get all success")
                    .pageSize(dtoList.size()).pageIndex(pageIndex)
                    .data(dtoList).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> getDetail(int id) {
        try {
            Transaction obj = transactionRepo.findById(id).orElse(null);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TransactionDTOview dto = modelMapper.map(obj, TransactionDTOview.class);
            dto.setDateTimeStamp(obj.getDate().getTime()/1000);
            return ResponseObject.builder().status(true).message("get success").data(dto).build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }

    @Override
    public ResponseObject<?> create(String description, int amoount, int walletId, String bankCode) {
        try {
            //chck wallet
            Wallet obj = walletRepo.findById(walletId).orElse(null);
            if (obj == null) {
                return ResponseObject.builder().status(false).message("Not found wallet with id "+ walletId).build();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 7);
            Transaction paymentTransaction = new Transaction();
            paymentTransaction.setDescription(description);
            paymentTransaction.setWallet(obj);
            paymentTransaction.setAmount(amoount);
            paymentTransaction.setBankCode(bankCode);
            paymentTransaction.setDate(cal.getTime());
            transactionRepo.save(paymentTransaction);
            return ResponseObject.builder().status(true).message("create success").build();
        }catch (Exception e){
            return ResponseObject.builder().status(false).message("error").data(e.getMessage()).build();
        }
    }
}
