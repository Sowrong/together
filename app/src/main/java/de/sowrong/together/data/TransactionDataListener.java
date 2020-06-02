package de.sowrong.together.data;

import java.util.HashMap;

public interface TransactionDataListener {
        public void onTransactionDataChanged(HashMap<String, Transaction> transactions);
    }