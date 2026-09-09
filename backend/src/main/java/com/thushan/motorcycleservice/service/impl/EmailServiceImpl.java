package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.response.InvoiceDetailResponseDTO;
import com.thushan.motorcycleservice.dto.response.PaymentResponseDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderItemResponseDTO;
import com.thushan.motorcycleservice.dto.response.ServiceOrderSparePartResponseDTO;
import com.thushan.motorcycleservice.exception.BadRequestException;
import com.thushan.motorcycleservice.service.EmailService;
import com.thushan.motorcycleservice.service.InvoiceService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final InvoiceService invoiceService;

    @Value("${app.mail.from:}")
    private String fromAddress;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Override
    public void sendInvoiceEbill(Long invoiceId) {
        if (!mailEnabled) {
            log.warn("Email sending is disabled (app.mail.enabled=false)");
            return;
        }
        if (fromAddress == null || fromAddress.isBlank() || fromAddress.contains("YOUR_GMAIL")) {
            throw new BadRequestException("Email is not configured. Set spring.mail.username, spring.mail.password and app.mail.from in application.properties");
        }

        InvoiceDetailResponseDTO inv = invoiceService.getInvoiceDetail(invoiceId);
        if (inv.getCustomerEmail() == null || inv.getCustomerEmail().isBlank()) {
            throw new BadRequestException("Customer has no email address");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(inv.getCustomerEmail());
            helper.setSubject("E-Bill / Invoice " + inv.getInvoiceNumber() + " - Motorcycle Service");
            helper.setText(buildHtml(inv), true);
            mailSender.send(message);
            log.info("E-bill sent to {} for invoice {}", inv.getCustomerEmail(), inv.getInvoiceNumber());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to send e-bill: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to send e-bill email: " + e.getMessage());
        }
    }

    private String buildHtml(InvoiceDetailResponseDTO inv) {
        StringBuilder services = new StringBuilder();
        if (inv.getServices() != null) {
            for (ServiceOrderItemResponseDTO s : inv.getServices()) {
                services.append("<tr><td>").append(esc(s.getServiceName())).append("</td><td>")
                        .append(s.getQuantity()).append("</td><td>")
                        .append(money(s.getUnitPrice())).append("</td><td>")
                        .append(money(s.getSubtotal())).append("</td></tr>");
            }
        }
        if (services.length() == 0) {
            services.append("<tr><td colspan='4'>None</td></tr>");
        }

        StringBuilder parts = new StringBuilder();
        if (inv.getSpareParts() != null) {
            for (ServiceOrderSparePartResponseDTO p : inv.getSpareParts()) {
                parts.append("<tr><td>").append(esc(p.getSparePartName())).append("</td><td>")
                        .append(p.getQuantity()).append("</td><td>")
                        .append(money(p.getUnitPrice())).append("</td><td>")
                        .append(money(p.getSubtotal())).append("</td></tr>");
            }
        }
        if (parts.length() == 0) {
            parts.append("<tr><td colspan='4'>None</td></tr>");
        }

        StringBuilder payments = new StringBuilder();
        if (inv.getPayments() != null) {
            for (PaymentResponseDTO p : inv.getPayments()) {
                payments.append("<tr><td>").append(p.getPaymentDate()).append("</td><td>")
                        .append(esc(p.getPaymentMethod())).append("</td><td>")
                        .append(money(p.getAmount())).append("</td><td>")
                        .append(esc(p.getReference() != null ? p.getReference() : "-")).append("</td></tr>");
            }
        }
        if (payments.length() == 0) {
            payments.append("<tr><td colspan='4'>None</td></tr>");
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><body style=\"font-family:Arial,sans-serif;color:#222;max-width:700px;margin:0 auto;\">");
        html.append("<h2 style=\"margin-bottom:4px;\">Motorcycle Service - E-Bill</h2>");
        html.append("<p><strong>Invoice No:</strong> ").append(esc(inv.getInvoiceNumber())).append("<br>");
        html.append("<strong>Date:</strong> ").append(inv.getInvoiceDate()).append("<br>");
        html.append("<strong>Status:</strong> ").append(esc(inv.getStatus())).append("</p>");
        html.append("<h3>Customer</h3>");
        html.append("<p>").append(esc(inv.getCustomerName())).append("<br>");
        html.append(esc(inv.getCustomerPhone())).append("<br>");
        html.append(esc(inv.getCustomerEmail())).append("<br>");
        html.append(esc(inv.getCustomerAddress())).append("</p>");
        html.append("<h3>Motorcycle</h3>");
        html.append("<p>").append(esc(inv.getMotorcycleRegistration())).append(" | ");
        html.append(esc(inv.getMotorcycleBrand())).append(" | ");
        html.append(esc(inv.getMotorcycleModelYear())).append("</p>");
        html.append("<h3>Services</h3>");
        html.append("<table width=\"100%\" cellpadding=\"6\" cellspacing=\"0\" border=\"1\" style=\"border-collapse:collapse;\">");
        html.append("<tr style=\"background:#f3f3f3;\"><th align=\"left\">Service</th><th>Qty</th><th>Unit (LKR)</th><th>Subtotal</th></tr>");
        html.append(services);
        html.append("</table>");
        html.append("<h3>Spare Parts</h3>");
        html.append("<table width=\"100%\" cellpadding=\"6\" cellspacing=\"0\" border=\"1\" style=\"border-collapse:collapse;\">");
        html.append("<tr style=\"background:#f3f3f3;\"><th align=\"left\">Part</th><th>Qty</th><th>Unit (LKR)</th><th>Subtotal</th></tr>");
        html.append(parts);
        html.append("</table>");
        html.append("<p style=\"text-align:right;margin-top:16px;\">");
        html.append("Subtotal: LKR ").append(money(inv.getSubtotal())).append("<br>");
        html.append("Discount: LKR ").append(money(inv.getDiscount() != null ? inv.getDiscount() : BigDecimal.ZERO)).append("<br>");
        html.append("<strong>Total: LKR ").append(money(inv.getTotalAmount())).append("</strong><br>");
        html.append("Paid: LKR ").append(money(inv.getTotalPaid() != null ? inv.getTotalPaid() : BigDecimal.ZERO)).append("<br>");
        html.append("<strong>Balance: LKR ").append(money(inv.getBalance() != null ? inv.getBalance() : BigDecimal.ZERO)).append("</strong>");
        html.append("</p>");
        html.append("<h3>Payments</h3>");
        html.append("<table width=\"100%\" cellpadding=\"6\" cellspacing=\"0\" border=\"1\" style=\"border-collapse:collapse;\">");
        html.append("<tr style=\"background:#f3f3f3;\"><th align=\"left\">Date</th><th>Method</th><th>Amount</th><th>Reference</th></tr>");
        html.append(payments);
        html.append("</table>");
        html.append("<p style=\"margin-top:24px;color:#666;font-size:12px;\">This is an automated e-bill from Motorcycle Service Workshop.</p>");
        html.append("</body></html>");
        return html.toString();
    }

    private String money(BigDecimal v) {
        if (v == null) {
            return "0.00";
        }
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
