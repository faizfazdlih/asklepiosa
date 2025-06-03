package Kesehatan.Asklepios.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.Schedule;
import Kesehatan.Asklepios.model.Transaction;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.repository.ConsultationRepository;
import Kesehatan.Asklepios.repository.ScheduleRepository;
import Kesehatan.Asklepios.repository.UserRepository;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;

    // Booking konsultasi
    public Consultation bookConsultation(String scheduleId, String clientId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (Boolean.TRUE.equals(schedule.getIsBooked())) {
            throw new RuntimeException("Schedule already booked");
        }

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Consultation consultation = new Consultation();
        consultation.setId(UUID.randomUUID().toString());
        consultation.setSchedule(schedule);
        consultation.setClient(client);
        consultation.setStatus(Consultation.Status.PENDING);
        consultation.setCreatedAt(LocalDateTime.now());

        schedule.setIsBooked(true);
        scheduleRepository.save(schedule);

        Consultation savedConsultation = consultationRepository.save(consultation);
        
        // Buat transaksi otomatis dengan status PENDING
        transactionService.createTransaction(savedConsultation.getId(), "BANK_TRANSFER");
        
        return savedConsultation;
    }

    public Consultation getById(String id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
    }

    public List<Consultation> getConsultationsByClient(String clientId) {
        return consultationRepository.findByClientId(clientId);
    }

    public Consultation updateStatus(String consultationId, Consultation.Status status) {
        Consultation consultation = getById(consultationId);
        Consultation.Status oldStatus = consultation.getStatus();
        consultation.setStatus(status);
        
        // Jika status diubah menjadi CANCELLED, tambahkan saldo ke client
        if (status == Consultation.Status.CANCELLED && oldStatus != Consultation.Status.CANCELLED) {
            // Cari transaksi terkait
            Transaction transaction = transactionService.getTransactionByConsultationId(consultationId);
            
            System.out.println("Consultation " + consultationId + " cancelled");
            System.out.println("Transaction found: " + (transaction != null));
            
            // Jika transaksi sudah dibayar, kembalikan dana ke saldo client
            if (transaction != null) {
                System.out.println("Transaction status: " + transaction.getStatus());
                if (transaction.getStatus() == Transaction.Status.PAID) {
                    User client = consultation.getClient();
                    BigDecimal oldBalance = client.getBalance();
                    User updatedClient = userService.addBalance(client.getId(), transaction.getAmount());
                    // Update client reference in consultation
                    consultation.setClient(updatedClient);
                    
                    System.out.println("Added balance: " + transaction.getAmount() + 
                                     " to client " + client.getFullName() + 
                                     ". Old balance: " + oldBalance + 
                                     ", New balance: " + updatedClient.getBalance());
                }
            }
        }
        
        return consultationRepository.save(consultation);
    }

    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }
}