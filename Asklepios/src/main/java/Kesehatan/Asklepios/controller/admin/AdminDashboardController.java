package Kesehatan.Asklepios.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.Transaction;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.repository.ConsultationRepository;
import Kesehatan.Asklepios.repository.TransactionRepository;
import Kesehatan.Asklepios.repository.UserRepository;
import Kesehatan.Asklepios.service.ConsultationService;
import Kesehatan.Asklepios.service.TransactionService;
import Kesehatan.Asklepios.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public String dashboard(Model model) {
        // Statistik pengguna
        List<User> allUsers = userService.getAllUsers();
        long clientCount = allUsers.stream().filter(u -> u.getRole() == User.Role.CLIENT).count();
        long psychologistCount = allUsers.stream().filter(u -> u.getRole() == User.Role.PSYCHOLOGIST).count();
        long adminCount = allUsers.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("clientCount", clientCount);
        model.addAttribute("psychologistCount", psychologistCount);
        model.addAttribute("adminCount", adminCount);
        
        // Statistik konsultasi
        List<Consultation> allConsultations = consultationService.getAllConsultations();
        long pendingConsultations = allConsultations.stream()
                .filter(c -> c.getStatus() == Consultation.Status.PENDING).count();
        long confirmedConsultations = allConsultations.stream()
                .filter(c -> c.getStatus() == Consultation.Status.CONFIRMED).count();
        long completedConsultations = allConsultations.stream()
                .filter(c -> c.getStatus() == Consultation.Status.COMPLETED).count();
        long cancelledConsultations = allConsultations.stream()
                .filter(c -> c.getStatus() == Consultation.Status.CANCELLED).count();
        
        model.addAttribute("totalConsultations", allConsultations.size());
        model.addAttribute("pendingConsultations", pendingConsultations);
        model.addAttribute("confirmedConsultations", confirmedConsultations);
        model.addAttribute("completedConsultations", completedConsultations);
        model.addAttribute("cancelledConsultations", cancelledConsultations);
        
        // Statistik transaksi
        List<Transaction> allTransactions = transactionService.getAllTransactions();
        long pendingTransactions = allTransactions.stream()
                .filter(t -> t.getStatus() == Transaction.Status.PENDING).count();
        long paidTransactions = allTransactions.stream()
                .filter(t -> t.getStatus() == Transaction.Status.PAID).count();
        long failedTransactions = allTransactions.stream()
                .filter(t -> t.getStatus() == Transaction.Status.FAILED).count();
        
        model.addAttribute("totalTransactions", allTransactions.size());
        model.addAttribute("pendingTransactions", pendingTransactions);
        model.addAttribute("paidTransactions", paidTransactions);
        model.addAttribute("failedTransactions", failedTransactions);
        
        // Konsultasi terbaru (5 teratas)
        List<Consultation> recentConsultations = allConsultations.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentConsultations", recentConsultations);
        
        // Transaksi terbaru (5 teratas)
        List<Transaction> recentTransactions = allTransactions.stream()
                .sorted((t1, t2) -> {
                    if (t1.getPaymentDate() == null && t2.getPaymentDate() == null) return 0;
                    if (t1.getPaymentDate() == null) return 1;
                    if (t2.getPaymentDate() == null) return -1;
                    return t2.getPaymentDate().compareTo(t1.getPaymentDate());
                })
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentTransactions", recentTransactions);
        
        return "admin/dashboard/index";
    }
}