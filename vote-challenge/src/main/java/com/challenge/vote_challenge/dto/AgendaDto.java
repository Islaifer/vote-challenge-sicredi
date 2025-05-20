package com.challenge.vote_challenge.dto;

import com.challenge.vote_challenge.constants.VoteConstants;
import com.challenge.vote_challenge.interfaces.IDtoFrom;
import com.challenge.vote_challenge.models.Agenda;
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
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
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

    @Override
    public Agenda toModel() {
        //Set<Vote> votes = new HashSet<>(this.votes);
        return new Agenda(this.name, this.details, null, null, false);
    }

    @JsonProperty("votesYes")
    public long getVotesYes(){
        return votes.stream().filter(p -> p.getVote().equals(VoteConstants.YES)).count();
    }

    @JsonProperty("votesNo")
    public long getVotesNo(){
        return votes.stream().filter(p -> p.getVote().equals(VoteConstants.NO)).count();
    }
}
