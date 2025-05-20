package com.challenge.vote_challenge.unit.services;

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
import com.challenge.vote_challenge.services.AgendaService;
import com.challenge.vote_challenge.services.AssociateService;
import com.challenge.vote_challenge.services.VoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {
    @Mock
    private VoteRepository repository;

    @Mock
    private AgendaService agendaService;

    @Mock
    private AssociateService associateService;

    @InjectMocks
    private VoteService service;

    @Test
    void getVoteById_shouldReturnCorrect_WhenIdExists(){
        Long id = 1L;

        Vote voteExpected = new Vote(1L, "YES", new Associate(), new Agenda());

        when(repository.findById(id)).thenReturn(Optional.of(voteExpected));

        VoteDto voteDto = service.getVoteById(id);

        assertEquals(voteExpected.getId(), voteDto.getId());
        assertEquals(voteExpected.getVote(), voteDto.getVote());
    }

    @Test
    void getVoteById_shouldThrowException_WhenIdNotExists(){
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.getVoteById(id);
        });
        assertEquals("Not found any vote by id " + id, ex.getMessage());
    }

    @Test
    void vote_shouldReturnAbleToVote_WhenIdExistAndWasCorrect(){
        Long agendaId = 1L;
        VoteDto voteDto = new VoteDto(0L, "YES", new AssociateDto(1L, "97525134090"));
        Vote vote = new Vote(1L, "YES", null, null);

        LocalDateTime expiration = LocalDateTime.now().plusHours(1);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon details", expiration, new HashSet<>(), true);
        Associate associate = new Associate(1L, "97525134090");

        when(agendaService.getEntityById(agendaId)).thenReturn(agenda);
        when(associateService.getEntityById(1L)).thenReturn(associate);
        when(repository.save(any())).thenReturn(vote);

        EligibleVoteStatus status = service.vote(agendaId, voteDto);
        assertEquals(EligibleVoteStatusConstants.ABLE_TO_VOTE, status.status());
    }

    @Test
    void vote_shouldReturnUnableToVote_WhenCannotAddAssociate(){
        Long agendaId = 1L;
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        VoteDto voteDto = new VoteDto(0L, "YES", associateDto);

        LocalDateTime expiration = LocalDateTime.now().plusHours(1);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon details", expiration, new HashSet<>(), true);


        when(agendaService.getEntityById(agendaId)).thenReturn(agenda);
        when(associateService.getEntityById(associateDto.getId())).thenReturn(null);
        when(associateService.addAssociate(associateDto)).thenReturn(new EligibleVoteStatus(EligibleVoteStatusConstants.UNABLE_TO_VOTE));

        EligibleVoteStatus status = service.vote(agendaId, voteDto);
        assertEquals(EligibleVoteStatusConstants.UNABLE_TO_VOTE, status.status());
    }

    @Test
    void vote_shouldReturnAbleToVote_WhenIdNotExistAndCanAddAssociate(){
        Long agendaId = 1L;
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        VoteDto voteDto = new VoteDto(0L, "YES", associateDto);
        Vote vote = new Vote(1L, "YES", null, null);

        LocalDateTime expiration = LocalDateTime.now().plusHours(1);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon details", expiration, new HashSet<>(), true);
        Associate associate = new Associate(1L, "97525134090");


        when(agendaService.getEntityById(agendaId)).thenReturn(agenda);
        when(associateService.getEntityById(1L)).thenReturn(null);
        when(associateService.addAssociate(associateDto)).thenReturn(new EligibleVoteStatus(EligibleVoteStatusConstants.ABLE_TO_VOTE));
        when(associateService.getEntityByCpf(associateDto.getCpf())).thenReturn(associate);
        when(repository.save(any())).thenReturn(vote);

        EligibleVoteStatus status = service.vote(agendaId, voteDto);
        assertEquals(EligibleVoteStatusConstants.ABLE_TO_VOTE, status.status());
    }

    @Test
    void vote_shouldThrowException_whenAgendaNotExist(){
        Long agendaId = 1L;
        VoteDto voteDto = new VoteDto(0L, "YES", new AssociateDto(1L, "97525134090"));

        when(agendaService.getEntityById(agendaId)).thenReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.vote(agendaId, voteDto);
        });
        assertEquals("Agenda not found by id " + agendaId, ex.getMessage());
    }

    @Test
    void vote_shouldThrowException_whenAgendaIsClose(){
        Long agendaId = 1L;
        VoteDto voteDto = new VoteDto(0L, "YES", new AssociateDto(1L, "97525134090"));

        LocalDateTime expiration = LocalDateTime.now().plusHours(1);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon details", expiration, new HashSet<>(), false);

        when(agendaService.getEntityById(agendaId)).thenReturn(agenda);

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            service.vote(agendaId, voteDto);
        });
        assertEquals("Agenda is Close", ex.getMessage());
    }

    @Test
    void vote_shouldThrowException_whenAgendaIsExpired(){
        Long agendaId = 1L;
        VoteDto voteDto = new VoteDto(0L, "YES", new AssociateDto(1L, "97525134090"));

        LocalDateTime expiration = LocalDateTime.now().minusHours(1);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon details", expiration, new HashSet<>(), true);

        when(agendaService.getEntityById(agendaId)).thenReturn(agenda);

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            service.vote(agendaId, voteDto);
        });
        assertEquals("Agenda already expired. Expiration: " + agenda.getExpiration(), ex.getMessage());
    }

    @Test
    void vote_shouldThrowException_whenAssociateAlreadyVoted(){
        Long agendaId = 1L;
        VoteDto voteDto = new VoteDto(0L, "YES", new AssociateDto(1L, "97525134090"));

        LocalDateTime expiration = LocalDateTime.now().plusHours(1);

        Set<Vote> votes = new HashSet<>();
        Associate associate = new Associate(1L, "97525134090");

        votes.add(new Vote(1L, "YES", associate, null));

        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon details", expiration, votes, true);


        when(agendaService.getEntityById(agendaId)).thenReturn(agenda);
        when(associateService.getEntityById(1L)).thenReturn(associate);
        //doNothing().when(repository).save(any());

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            service.vote(agendaId, voteDto);
        });
        assertEquals("Associate " + associate.getId() + " already voted", ex.getMessage());
    }
}
