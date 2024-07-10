package com.roamgram.travelDiary.domain.model.travel;

import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.roamgram.travelDiary.domain.model.tags.Tags;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Activity implements IdentifiableResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID scheduleId;

    public String name;

    public String description;

    public LocalDateTime eventStartTime;

    public LocalDateTime eventEndTime;

    public String eventDescription;

    @OneToMany
    public List<MonetaryEventEntity> monetaryEvents;

    @OneToMany
    public List<Tags> tags;
}
