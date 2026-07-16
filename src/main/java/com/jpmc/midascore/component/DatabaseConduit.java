package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    /**
     * Saves a user record to the database.
     * @param userRecord the user to save
     */
    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    /**
     * Retrieves a user by their ID.
     * @param id the user ID
     * @return the UserRecord if found, null otherwise
     */
    public UserRecord getUserById(long id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by their name.
     * @param name the user name
     * @return the UserRecord if found, null otherwise
     */
    public UserRecord getUserByName(String name) {
        return userRepository.findByName(name);
    }

    /**
     * Saves a transaction record to the database.
     * @param transactionRecord the transaction to save
     */
    public void save(TransactionRecord transactionRecord) {
        transactionRecordRepository.save(transactionRecord);
    }
}
