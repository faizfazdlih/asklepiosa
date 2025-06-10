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
        
        System.out.println("Updating consultation status from " + oldStatus + " to " + status);
    
        consultation.setStatus(status);
        Consultation updatedConsultation = consultationRepository.save(consultation);
    
        // Jika status diubah menjadi CANCELLED, ubah status transaksi menjadi REFUND
        if (status == Consultation.Status.CANCELLED && oldStatus != Consultation.Status.CANCELLED) {
            System.out.println("Consultation cancelled, checking transaction...");
            // Cari transaksi terkait
            Transaction transaction = transactionService.getTransactionByConsultationId(consultationId);
    
            if (transaction != null) {
                System.out.println("Found transaction with status: " + transaction.getStatus());
                
                if (transaction.getStatus() == Transaction.Status.PAID) {
                    // Ubah status transaksi menjadi REFUND
                    transactionService.updatePaymentStatus(transaction.getId(), Transaction.Status.REFUND, LocalDateTime.now());
                    System.out.println("Transaction status updated to REFUND");
                } else {
                    System.out.println("Transaction status is not PAID, no update needed");
                }
            } else {
                System.out.println("No transaction found for consultation ID: " + consultationId);
            }
        }
    
        return updatedConsultation;
    }
    
    

    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }
}
