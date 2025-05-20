package com.challenge.agenda_processor.consumers;

import com.challenge.agenda_processor.constants.VoteConstants;
import com.challenge.agenda_processor.dto.AgendaOpenedEvent;
import com.challenge.agenda_processor.dto.AgendaResult;
import com.challenge.agenda_processor.models.Agenda;
import com.challenge.agenda_processor.models.Vote;
import com.challenge.agenda_processor.repositories.AgendaRepository;
import com.challenge.agenda_processor.repositories.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class with consumers of the agenda opening events
 */
@Slf4j
@Service
public class AgendaOpenedConsumer {

    private final AgendaRepository repository;
    private final KafkaTemplate<String, AgendaResult> kafkaTemplate;
    private final VoteRepository voteRepository;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public AgendaOpenedConsumer(AgendaRepository repository, KafkaTemplate<String, AgendaResult> kafkaTemplate, VoteRepository voteRepository, ScheduledExecutorService scheduler) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.voteRepository = voteRepository;
        this.scheduler = scheduler;
    }

    /**
     * Consumer reading the opening event
     * @param event the AgendaOpenedEvent who represent the Agenda that was opened
     */
    @KafkaListener(topics = "agenda-opened", groupId = "agenda-processor", containerFactory = "agendaOpenedKafkaListenerContainerFactory")
    public void scheduleAgenda(AgendaOpenedEvent event){
        long delayMillis = Duration.between(LocalTime.now(), event.expiration()).toMillis();
        log.info("New event listened!\n Agenda id: '{}'", event.agendaId());

        scheduler.schedule(() -> {
            closeAgenda(event.agendaId());
        }, delayMillis, TimeUnit.MILLISECONDS);
        log.info("New event registered into scheduler queue");
    }

    /**
     * Consumer that runs only at the start of the application to close the Agendas that were Open and will expire when the service was offline
     */
    @EventListener(ApplicationReadyEvent.class)
    public void reschedulePendingAgendas(){
        log.info("Try to get all Agendas that wasn't closed");
        Set<Agenda> openedAgendas = repository.findByIsOpen(true).orElse(new HashSet<>());

        log.debug("Start register all open Agenda in schedule or close if already expired");
        for(Agenda agenda : openedAgendas){
            LocalDateTime expiration = agenda.getExpiration();

            long delayMillis = Duration.between(LocalDateTime.now(), expiration).toMillis();

            if (delayMillis <= 0) {
                closeAgenda(agenda.getId());
            } else {
                scheduler.schedule(() -> {
                    closeAgenda(agenda.getId());
                }, delayMillis, TimeUnit.MILLISECONDS);

                log.debug("Agenda with id '{}' registered into scheduler queue", agenda.getId());
            }
        }
    }

    private void closeAgenda(Long agendaId){
        log.info("Start close Agenda with agendaId: '{}'", agendaId);
        Agenda agenda = repository.findById(agendaId).orElse(null);

        if(agenda != null){
            agenda.setOpen(false);
            repository.save(agenda);

            Set<Vote> votes = voteRepository.findAllByAgendaId(agenda.getId()).orElse(new HashSet<>());

            int votesYes = 0;
            int votesNo = 0;

            log.info("Calculate voting result for Agenda with agendaId: '{}'", agendaId);
            for(var vote : votes){
                if(vote.getVote().equals(VoteConstants.YES))
                    votesYes++;
                else if(vote.getVote().equals(VoteConstants.NO))
                    votesNo++;
            }

            String voteWin;

            if(votesYes > votesNo)
                voteWin = VoteConstants.YES;
            else if(votesNo > votesYes)
                voteWin = VoteConstants.NO;
            else
                voteWin = VoteConstants.DRAW;

            log.info("Agenda with agendaId: '{}' was close and her result was sent to kafka", agendaId);
            AgendaResult result = new AgendaResult(agenda.getId(), agenda.getName(), agenda.getDetails(), votesYes, votesNo, voteWin);
            kafkaTemplate.send("agenda-finished", result);
        }
    }
}
