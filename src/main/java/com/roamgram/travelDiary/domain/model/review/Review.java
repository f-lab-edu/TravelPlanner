package com.roamgram.travelDiary.domain.model.review;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
public class Review implements IdentifiableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID scheduleId;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<MediaFile> fileList;

    @Column(columnDefinition = "TEXT")
    public String userDescription;

    public Double rating;

    @ElementCollection
    public Map<String,Long> contentLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    public Resource resource;
}
