package com.example.travelDiary.domain.persistence.travel;

import com.example.travelDiary.domain.persistence.location.Location;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class SingleTravelPage {
    @Id
    private Long id;

    public Set<UserTags> travelCategory;

    @OneToOne
    public Location location;

    public String userDescription;

    @OneToMany
    public List<Image> imageList;


}
