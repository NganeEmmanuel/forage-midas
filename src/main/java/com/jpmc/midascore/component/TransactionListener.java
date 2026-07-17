package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.foundation.Incentive;
import org.springframework.web.client.RestTemplate;

/**
 * Listens for incoming transaction messages from Kafka, validates them,
 * and records valid transactions to the database.
 */
@Component
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Consumes a transaction message, validates the sender and recipient,
     * checks for sufficient balance, updates balances, and records the transaction.
     * 
     * @param transaction the deserialized transaction record from Kafka
     */
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        UserRecord sender = databaseConduit.getUserById(transaction.getSenderId());
        UserRecord recipient = databaseConduit.getUserById(transaction.getRecipientId());

        if (sender != null && recipient != null) {
            if (sender.getBalance() >= transaction.getAmount()) {
                
                Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
                float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

                databaseConduit.save(sender);
                databaseConduit.save(recipient);

                TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
                databaseConduit.save(transactionRecord);
            }
        }
    }
}
