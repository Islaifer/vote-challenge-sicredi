package com.challenge.vote_challenge.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter
public class Agenda {
    @Id @GeneratedValue
    private Long id;
    @Setter
    private String name;
    @Setter
    private String details;
    @Setter
    private LocalDateTime expiration;
    @Setter @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes;
    @Setter
    private boolean isOpen;

    public Agenda(String name, String details, LocalDateTime expiration, Set<Vote> votes, boolean isOpen){
        this.name = name;
        this.details = details;
        this.expiration = expiration;
        this.votes = votes;
        this.isOpen = isOpen;
    }
}
