package com.semihtumay.invoiceservice.service;

import com.semihtumay.invoiceservice.dto.request.CreateInvoiceRequest;
import com.semihtumay.invoiceservice.mapper.InvoiceMapper;
import com.semihtumay.invoiceservice.model.Invoice;
import com.semihtumay.invoiceservice.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = InvoiceMapper.requestToInvoice(request);
        invoiceRepository.save(invoice);
    }
}
