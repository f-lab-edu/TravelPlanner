package com.example.travelDiary.presentation.dto.review;

import com.example.travelDiary.domain.model.review.MediaFile;
import lombok.Data;

import java.util.List;

@Data
public class ReviewUpsertRequest {

        public List<MediaFile> fileList;

        public String userDescription;

        public Double rating;

}
