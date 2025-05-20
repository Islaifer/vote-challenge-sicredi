package com.challenge.vote_challenge.services;

import com.challenge.vote_challenge.constants.EligibleVoteStatusConstants;
import com.challenge.vote_challenge.dto.AssociateDto;
import com.challenge.vote_challenge.dto.EligibleVoteStatus;
import com.challenge.vote_challenge.dto.VoteDto;
import com.challenge.vote_challenge.exceptions.InvalidEntityException;
import com.challenge.vote_challenge.exceptions.NotFoundException;
import com.challenge.vote_challenge.models.Agenda;
import com.challenge.vote_challenge.models.Associate;
import com.challenge.vote_challenge.models.Vote;
import com.challenge.vote_challenge.repositories.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Class representing the service class of the Vote entity
 */
@Slf4j
@Service
public class VoteService {
    private final VoteRepository repository;
    private final AgendaService agendaService;
    private final AssociateService associateService;

    @Autowired
    public VoteService(VoteRepository repository, AgendaService agendaService, AssociateService associateService) {
        this.repository = repository;
        this.agendaService = agendaService;
        this.associateService = associateService;
    }

    /**
     * Method to find Vote by id
     * @param id the id to search Vote
     * @return An VoteDto who represent the entity Vote
     */
    public VoteDto getVoteById(Long id){
        log.info("Try get Vote into database with id '{}'", id);
        Vote vote = repository.findById(id).orElse(null);
        if(vote == null){
            log.error("Vote not found with id: '{}'", id);
            throw new NotFoundException("Not found any vote by id " + id);
        }

        log.debug("Convert Vote model to Vote dto");
        return new VoteDto(vote);
    }

    /**
     * Method used to vote on an agenda
     * @param agendaId the id to get the target Agenda
     * @param voteDto the vote to vote in the Agenda
     * @return the EligibleVoteStatus who represent if the Associate can vote or not
     */
    public EligibleVoteStatus vote(Long agendaId, VoteDto voteDto){
        EligibleVoteStatus status;
        AssociateDto associateDto = voteDto.getAssociate();
        LocalDateTime now = LocalDateTime.now();

        Agenda agenda = agendaService.getEntityById(agendaId);
        if(agenda == null){
            log.error("Agenda not found with agenda Id: '{}'", agendaId);
            throw new NotFoundException("Agenda not found by id " + agendaId);
        }

        if(!agenda.isOpen()){
            log.error("Agenda is Close");
            throw new InvalidEntityException("Agenda is Close");
        }

        if(agenda.getExpiration().isBefore(now)) {
            log.error("Agenda already expired. Expiration: '{}'", agenda.getExpiration());
            throw new InvalidEntityException("Agenda already expired. Expiration: " + agenda.getExpiration());
        }

        Associate associate = associateService.getEntityById(associateDto.getId());

        if(associate == null){
            log.warn("Associate doesn't exist, try to added with cpf");
            status = associateService.addAssociate(associateDto);

            if(status.status().equals(EligibleVoteStatusConstants.UNABLE_TO_VOTE)){
                log.warn("Associate is {}", EligibleVoteStatusConstants.UNABLE_TO_VOTE);
                return status;
            }

            associate = associateService.getEntityByCpf(associateDto.getCpf());
        }

        Long associateId = associate.getId();
        boolean existVote = agenda.getVotes().stream().anyMatch(p -> p.getAssociate().getId().equals(associateId));
        if(existVote){
            log.error("Associate {} already voted", associateId);
            throw new InvalidEntityException("Associate " + associateId + " already voted");
        }

        Vote vote = voteDto.toModel();
        vote.setAssociate(associate);
        vote.setAgenda(agenda);

        log.info("Try added vote in database");
        repository.save(vote);

        status = new EligibleVoteStatus(EligibleVoteStatusConstants.ABLE_TO_VOTE);
        return status;
    }
}
