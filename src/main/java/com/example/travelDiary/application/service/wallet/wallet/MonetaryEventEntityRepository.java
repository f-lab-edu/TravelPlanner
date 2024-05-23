package com.example.travelDiary.application.service.wallet.wallet;

import com.example.travelDiary.application.service.wallet.entity.MonetaryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonetaryEventEntityRepository extends JpaRepository<MonetaryEventEntity, UUID> {
}
