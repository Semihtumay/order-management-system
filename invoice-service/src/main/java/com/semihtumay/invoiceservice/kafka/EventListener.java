package com.semihtumay.invoiceservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihtumay.invoiceservice.dto.events.OrderEvent;
import com.semihtumay.invoiceservice.dto.request.CreateInvoiceRequest;
import com.semihtumay.invoiceservice.exception.EventProcessingException;
import com.semihtumay.invoiceservice.service.InvoiceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import static com.semihtumay.invoiceservice.mapper.InvoiceMapper.mapToCreateInvoiceRequest;

@Component
public class EventListener {

    private final InvoiceService invoiceService;

    public EventListener(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = "outbox-events", groupId = "invoice-service")
    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 5000))
    public void handleOrderCreatedEvent(ConsumerRecord<String, String> record) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode eventNode = mapper.readTree(record.value());
            OrderEvent orderEvent = mapper.readValue(eventNode.get("payload").asText(), OrderEvent.class);
            CreateInvoiceRequest request = mapToCreateInvoiceRequest(orderEvent);
            invoiceService.createInvoice(request);
        } catch (JsonProcessingException e) {
            throw new EventProcessingException("Failed to process Debezium message from Kafka topic: "
                    + record.topic(), e);
        }
    }
}
