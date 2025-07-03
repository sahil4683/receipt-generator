package io.vv.receipt_generator.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "*")
public class ReceiptViewController {
    @GetMapping("/receipts-ui")
    public String showReceiptUi() {
        return "receipts";
    }

    @GetMapping("/receipts-master")
    public String showReceiptMaster() {
        return "receipts-master";
    }
}
