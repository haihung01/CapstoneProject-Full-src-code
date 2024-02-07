package com.example.triptix.DTO.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TwllioKey {
    private String accountSid;
    private String authToken;
    private String outgoingSmsNumber;

    @Override
    public String toString() {
        return "TwllioKey{" +
                "accountSid='" + accountSid + '\'' +
                ", authToken='" + authToken + '\'' +
                ", outgoingSmsNumber='" + outgoingSmsNumber + '\'' +
                '}';
    }
}