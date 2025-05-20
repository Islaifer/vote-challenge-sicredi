package com.challenge.vote_challenge.dto;

import com.challenge.vote_challenge.interfaces.IDtoFrom;
import com.challenge.vote_challenge.models.Vote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The DTO of Vote entity
 */
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class VoteDto implements IDtoFrom<Vote> {
    private Long id;
    private String vote;
    private AssociateDto associate;

    public VoteDto(Vote vote){
        copyFrom(vote);
    }

    @Override
    public void copyFrom(Vote model) {
        this.id = model.getId();
        this.vote = model.getVote();
        this.associate = new AssociateDto(model.getAssociate());
    }

    @Override
    public Vote toModel() {
        return new Vote(this.vote, null, null);
    }
}

