package Kesehatan.Asklepios.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.Transaction;
import Kesehatan.Asklepios.repository.ConsultationRepository;
import Kesehatan.Asklepios.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    public Transaction createTransaction(String consultationId, String paymentMethodStr) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        BigDecimal price = consultation.getSchedule()
                .getPsychologist()
                .getPrice();

        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID().toString());
        tx.setConsultation(consultation);
        tx.setAmount(price);
        tx.setPaymentMethod(Transaction.PaymentMethod.valueOf(paymentMethodStr));
        tx.setStatus(Transaction.Status.PENDING);

        return transactionRepository.save(tx);
    }

    public Transaction updatePaymentMethod(String transactionId, Transaction.PaymentMethod paymentMethod) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        tx.setPaymentMethod(paymentMethod);
        return transactionRepository.save(tx);
    }

    // Update existing method to ensure it saves properly
    public Transaction updatePaymentStatus(String transactionId, Transaction.Status status, LocalDateTime paymentDate) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        tx.setStatus(status);
        tx.setPaymentDate(paymentDate);
        return transactionRepository.save(tx);
    }

    public List<Transaction> getTransactionsByClient(String clientId) {
        return transactionRepository.findByConsultationClientId(clientId);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Transaction getById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
    
    // Method untuk mendapatkan transaksi berdasarkan consultation ID
    public Transaction getTransactionByConsultationId(String consultationId) {
        return transactionRepository.findByConsultationId(consultationId);
    }
}