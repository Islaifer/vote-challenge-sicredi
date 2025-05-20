package com.challenge.vote_challenge.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter
public class Vote {
    @Id @GeneratedValue
    private Long id;
    @Setter
    private String vote;
    @Setter @ManyToOne @JoinColumn
    private Associate associate;
    @Setter @ManyToOne @JoinColumn
    private Agenda agenda;

    public Vote(String vote, Associate associate, Agenda agenda){
        this.vote = vote;
        this.associate = associate;
        this.agenda = agenda;
    }
}
