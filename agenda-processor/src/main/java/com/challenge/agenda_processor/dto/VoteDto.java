package com.challenge.agenda_processor.dto;

import com.challenge.agenda_processor.interfaces.IDtoFrom;
import com.challenge.agenda_processor.models.Vote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The DTO of Vote entity
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VoteDto implements IDtoFrom<Vote> {
    private Long id;
    private String vote;

    public VoteDto(Vote vote){
        copyFrom(vote);
    }

    @Override
    public void copyFrom(Vote model) {
        this.id = model.getId();
        this.vote = model.getVote();
    }
}
