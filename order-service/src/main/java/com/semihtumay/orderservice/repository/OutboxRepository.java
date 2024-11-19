package com.semihtumay.orderservice.repository;

import com.semihtumay.orderservice.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
}