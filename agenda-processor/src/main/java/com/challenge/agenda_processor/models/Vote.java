package com.challenge.agenda_processor.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class Vote {
    @Id
    @GeneratedValue
    @Getter
    private Long id;
    @Setter @Getter
    private String vote;
    @Setter @Getter @ManyToOne
    @JoinColumn
    private Associate associate;
    @Setter @ManyToOne @JoinColumn
    private Agenda agenda;

    public Vote(String vote, Associate associate, Agenda agenda){
        this.vote = vote;
        this.associate = associate;
        this.agenda = agenda;
    }
}
