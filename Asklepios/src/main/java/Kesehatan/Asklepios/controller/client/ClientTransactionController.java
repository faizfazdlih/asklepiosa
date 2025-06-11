package Kesehatan.Asklepios.controller.client;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Kesehatan.Asklepios.model.Transaction;
import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.service.TransactionService;
import Kesehatan.Asklepios.service.UserService;

@Controller
@RequestMapping("/client/transactions")
public class ClientTransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    // Menampilkan semua transaksi client
    @GetMapping
    public String listTransactions(Model model, Principal principal) {
        String email = principal.getName();
        User client = userService.getByEmail(email);

        List<Transaction> transactions = transactionService.getTransactionsByClient(client.getId());
        model.addAttribute("transactions", transactions);
        return "client/transactions/list";
    }

    // Menampilkan detail transaksi
    @GetMapping("/{id}")
    public String viewTransaction(@PathVariable String id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        User client = userService.getByEmail(email);

        Transaction transaction;
        try {
            transaction = transactionService.getById(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Transaksi tidak ditemukan.");
            return "redirect:/client/transactions";
        }

        // Pastikan transaksi ini milik client yang sedang login
        if (!transaction.getConsultation().getClient().getId().equals(client.getId())) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke transaksi ini.");
            return "redirect:/client/transactions";
        }

        model.addAttribute("transaction", transaction);
        model.addAttribute("paymentMethods", Transaction.PaymentMethod.values());
        return "client/transactions/detail";
    }

    // Proses pembayaran
    @PostMapping("/{id}/pay")
    public String processPayment(
            @PathVariable String id,
            @RequestParam("paymentMethod") String paymentMethod,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User client = userService.getByEmail(email);

        Transaction transaction;
        try {
            transaction = transactionService.getById(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Transaksi tidak ditemukan.");
            return "redirect:/client/transactions";
        }

        // Pastikan transaksi ini milik client yang sedang login
        if (!transaction.getConsultation().getClient().getId().equals(client.getId())) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke transaksi ini.");
            return "redirect:/client/transactions";
        }
    
        try {
            transaction.setPaymentMethod(Transaction.PaymentMethod.valueOf(paymentMethod));
            transactionService.updatePaymentStatus(id, Transaction.Status.PAID, LocalDateTime.now());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memproses pembayaran: " + e.getMessage());
            return "redirect:/client/transactions/" + id;
        }
    
        redirectAttributes.addFlashAttribute("success", "Pembayaran berhasil diproses.");
        return "redirect:/client/transactions/" + id + "/receipt";
    }

    // Menampilkan bukti pembayaran
 @GetMapping("/{id}/receipt")
public String viewReceipt(@PathVariable String id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
    System.out.println("=== DEBUG: Receipt accessed for ID: " + id);
    
    String email = principal.getName();
    User client = userService.getByEmail(email);

    Transaction transaction;
    try {
        transaction = transactionService.getById(id);
        System.out.println("=== DEBUG: Transaction found, status: " + transaction.getStatus());
    } catch (RuntimeException e) {
        System.out.println("=== DEBUG: Transaction error: " + e.getMessage());
        redirectAttributes.addFlashAttribute("error", "Transaksi tidak ditemukan.");
        return "redirect:/client/transactions";
    }

    // Pastikan transaksi ini milik client yang sedang login
    if (!transaction.getConsultation().getClient().getId().equals(client.getId())) {
        System.out.println("=== DEBUG: Access denied");
        redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke transaksi ini.");
        return "redirect:/client/transactions";
    }

    // Pastikan transaksi sudah dibayar
    if (transaction.getStatus() != Transaction.Status.PAID) {
        System.out.println("=== DEBUG: Transaction not paid yet: " + transaction.getStatus());
        redirectAttributes.addFlashAttribute("error", "Bukti pembayaran hanya tersedia untuk transaksi yang sudah dibayar.");
        return "redirect:/client/transactions/" + id;
    }

    model.addAttribute("transaction", transaction);
    System.out.println("=== DEBUG: Returning template: client/transactions/receipt");
    return "client/transactions/receipt";
}
}