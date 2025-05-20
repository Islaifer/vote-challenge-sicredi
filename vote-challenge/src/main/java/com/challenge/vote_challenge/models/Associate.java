package com.challenge.vote_challenge.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Associate {
    @Id
    private Long id;
    private String cpf;

    public Associate(String cpf){
        this.cpf = cpf;
    }
}
