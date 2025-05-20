package com.challenge.vote_challenge.controller.v1;

import com.challenge.vote_challenge.dto.EligibleVoteStatus;
import com.challenge.vote_challenge.dto.VoteDto;
import com.challenge.vote_challenge.services.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Control class to provide call endpoints involving the Vote object
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1/votes/")
@Tag(name = "Vote", description = "Vote calls")
public class VoteController {
    private final VoteService service;

    @Autowired
    public VoteController(VoteService service) {
        this.service = service;
    }

    /**
     * Method that searches for an VoteDto by id
     * <p>
     * This method is an endpoint for Vote calls,
     * more specifically the endpoint GET /api/v1/votes/{id}
     * </p>
     * @param id the id to search for the specific Vote
     * @return A ResponseEntity<VoteDto> of the Vote that was found via id
     */
    @GetMapping("{id}")
    @Operation(summary = "Get Vote by Id")
    public ResponseEntity<VoteDto> getById(@PathVariable("id") Long id){
        log.info("Start get vote with id '{}'", id);
        VoteDto vote = service.getVoteById(id);

        log.info("Vote was found");
        return ResponseEntity.ok(vote);
    }

    /**
     * Method for voting in a poll (whether the vote is yes or no)
     * <p>
     * This method is an endpoint for Vote calls,
     * more specifically the endpoint POST /api/v1/votes/{agendaId}
     * </p>
     * @param agendaId the id that represents the Agenda where the vote will be deposited
     * @param vote The vote that will be cast
     * @return A ResponseEntity<EligibleVoteStatus> that represents whether the member is eligible to vote
     */
    @PostMapping("{agendaId}")
    @Operation(summary = "Vote in a Agenda with agenda Id")
    public ResponseEntity<EligibleVoteStatus> vote(@PathVariable("agendaId") Long agendaId, @RequestBody VoteDto vote){
        log.info("Start vote in Agenda with id '{}'", agendaId);
        log.debug("Associate id: '{}'", vote.getAssociate().getId());
        EligibleVoteStatus status = service.vote(agendaId, vote);

        return ResponseEntity.ok(status);
    }
}
