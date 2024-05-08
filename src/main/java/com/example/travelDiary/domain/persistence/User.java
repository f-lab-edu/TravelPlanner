package com.example.travelDiary.domain.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {


    @Id
    private Long id;

    private String userName;

    private Long saltedPassword;

}
