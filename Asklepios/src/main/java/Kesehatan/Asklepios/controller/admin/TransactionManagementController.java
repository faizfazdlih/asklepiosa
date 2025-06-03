package Kesehatan.Asklepios.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.Transaction;
import Kesehatan.Asklepios.repository.ConsultationRepository;
import Kesehatan.Asklepios.repository.TransactionRepository;
import Kesehatan.Asklepios.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/transactions")
public class TransactionManagementController {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private ConsultationRepository consultationRepository;
    
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String listTransactions(Model model) {
        List<Transaction> transactions = transactionRepository.findAll();
        model.addAttribute("transactions", transactions);
        return "admin/transactions/list";
    }

    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable String id, Model model) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));
        model.addAttribute("transaction", tx);
        return "admin/transactions/detail";
    }

    @PostMapping("/update/{id}")
    public String updateTransaction(
            @PathVariable String id,
            @RequestParam("paymentMethod") Transaction.PaymentMethod paymentMethod,
            @RequestParam("status") Transaction.Status status,
            @RequestParam(value = "paymentDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paymentDate
    ) {
        // Update payment method
        transactionService.updatePaymentMethod(id, paymentMethod);
        
        // Update status dan payment date
        transactionService.updatePaymentStatus(id, status, paymentDate);
        
        return "redirect:/admin/transactions/detail/" + id;
    }
    
    // Menampilkan form untuk membuat transaksi baru
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Consultation> consultations = consultationRepository.findAll();
        model.addAttribute("consultations", consultations);
        model.addAttribute("paymentMethods", Transaction.PaymentMethod.values());
        return "admin/transactions/create";
    }
    
    // Menyimpan transaksi baru
    @PostMapping("/create")
    public String createTransaction(
            @RequestParam("consultationId") String consultationId,
            @RequestParam("paymentMethod") String paymentMethod) {
        
        Transaction transaction = transactionService.createTransaction(consultationId, paymentMethod);
        return "redirect:/admin/transactions/detail/" + transaction.getId();
    }
}