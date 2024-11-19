package com.semihtumay.orderservice.service;

import com.semihtumay.orderservice.model.Outbox;
import com.semihtumay.orderservice.repository.OutboxRepository;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public OutboxService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    public void createOutbox(Outbox outbox){
        outboxRepository.save(outbox);
    }
}
