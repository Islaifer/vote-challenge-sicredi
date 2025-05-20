package com.challenge.agenda_processor.dto;

import com.challenge.agenda_processor.interfaces.IDtoFrom;
import com.challenge.agenda_processor.models.Agenda;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The DTO of Agenda entity
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgendaDto implements IDtoFrom<Agenda> {
    private Long id;
    private String name;
    private String details;
    private LocalDateTime expiration;
    private List<VoteDto> votes;
    private boolean isOpen;

    public AgendaDto(Agenda agenda){
        copyFrom(agenda);
    }

    @Override
    public void copyFrom(Agenda model) {
        this.id = model.getId();
        this.name = model.getName();
        this.details = model.getDetails();
        this.expiration = model.getExpiration();
        this.votes = model.getVotes().stream().map(VoteDto::new).toList();
        this.isOpen = model.isOpen();
    }
}
