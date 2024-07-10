package com.roamgram.travelDiary.domain.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    private UUID authUserId;

    private String userProfileName;

    private String userDescription;
}
