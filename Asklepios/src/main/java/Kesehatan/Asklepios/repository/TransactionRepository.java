package Kesehatan.Asklepios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Kesehatan.Asklepios.model.Transaction;


import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByStatus(Transaction.Status status);
    List<Transaction> findByConsultationClientId(String clientId);
    Transaction findByConsultationId(String consultationId);
}