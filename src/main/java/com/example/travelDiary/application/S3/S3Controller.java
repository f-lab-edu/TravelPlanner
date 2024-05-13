package com.example.travelDiary.application.S3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/S3Test")
public class S3Controller {
    private final S3TestService s3Service;

    @Autowired
    public S3Controller(S3TestService s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/test")
    public String s3Test(@RequestParam(value="path") String path) {
        return s3Service.createPresignedURLForUpload(path);
    }
}
