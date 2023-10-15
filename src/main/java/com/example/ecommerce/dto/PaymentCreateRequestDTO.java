package com.example.ecommerce.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class PaymentCreateRequestDTO 
{
    private String token;
    private String issuer_id;
    private String payment_method_id;
    private BigDecimal transaction_amount;
    private Integer installments;
    private PayerDTO payer;

    @Getter @Setter @ToString
    public static class PayerDTO 
    {
        private String email;
        private IdentificationDTO identification;
        
        @Getter @Setter @ToString
        public static class IdentificationDTO 
        {
            private String type;
            private String number;
        }
    }
}
