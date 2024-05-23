package com.example.travelDiary.repository.persistence.wallet;

import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonetaryEventEntityRepository extends JpaRepository<MonetaryEventEntity, UUID> {
}
