package Kesehatan.Asklepios.controller.client;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Kesehatan.Asklepios.model.Consultation;
import Kesehatan.Asklepios.model.Transaction;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.ConsultationService;
import Kesehatan.Asklepios.service.TransactionService;
import Kesehatan.Asklepios.service.UserService;

@Controller
@RequestMapping("/client/payment")
public class PaymentController {

    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/consultation/{id}")
    public String showPaymentForm(@PathVariable String id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        // Validasi user
        String email = principal.getName();
        User client = userService.getByEmail(email);
        
        // Ambil data konsultasi
        Consultation consultation;
        try {
            consultation = consultationService.getById(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Konsultasi tidak ditemukan.");
            return "redirect:/client/consultations";
        }
        
        // Pastikan konsultasi ini milik client yang sedang login
        if (!consultation.getClient().getId().equals(client.getId())) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke konsultasi ini.");
            return "redirect:/client/consultations";
        }
        
        // Cek apakah sudah ada transaksi untuk konsultasi ini
        Transaction transaction = consultation.getTransaction();
        
        // Jika belum ada transaksi, buat transaksi baru
        if (transaction == null) {
            // Gunakan BANK_TRANSFER sebagai default, nanti bisa diubah di halaman pembayaran
            transaction = transactionService.createTransaction(id, "BANK_TRANSFER");
            model.addAttribute("newTransaction", true);
        } else if (transaction.getStatus() == Transaction.Status.PAID) {
            // Jika transaksi sudah dibayar, redirect ke halaman detail transaksi
            redirectAttributes.addFlashAttribute("success", "Konsultasi ini sudah dibayar.");
            return "redirect:/client/transactions/" + transaction.getId();
        }
        
        model.addAttribute("consultation", consultation);
        model.addAttribute("transaction", transaction);
        model.addAttribute("paymentMethods", Transaction.PaymentMethod.values());
        
        return "client/payments/payment";
    }
}