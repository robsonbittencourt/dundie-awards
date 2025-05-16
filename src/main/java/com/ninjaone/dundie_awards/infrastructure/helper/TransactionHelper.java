package com.ninjaone.dundie_awards.infrastructure.helper;

import org.springframework.stereotype.Component;

import static org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus;

@Component
public class TransactionHelper {
    public void setRollbackOnly() {
        currentTransactionStatus().setRollbackOnly();
    }
}
