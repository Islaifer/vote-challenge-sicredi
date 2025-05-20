package com.challenge.vote_challenge.services;

import com.challenge.vote_challenge.dto.AgendaDto;
import com.challenge.vote_challenge.dto.AgendaOpenedEvent;
import com.challenge.vote_challenge.exceptions.NotFoundException;
import com.challenge.vote_challenge.models.Agenda;
import com.challenge.vote_challenge.repositories.AgendaRepository;
import com.challenge.vote_challenge.util.validate.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Class representing the service class of the Agenda entity
 */
@Slf4j
@Service
public class AgendaService {
    private final AgendaRepository repository;
    private final KafkaTemplate<String, AgendaOpenedEvent> kafkaTemplate;
    private final ValidateUtil validator;

    @Autowired
    public AgendaService(AgendaRepository repository, KafkaTemplate<String, AgendaOpenedEvent> kafkaTemplate, ValidateUtil validator) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.validator = validator;
    }

    /**
     * Method to find Agenda by id
     * @param id the id to search Agenda
     * @return An AgendaDto who represent the entity Agenda
     */
    public AgendaDto getById(Long id){
        Agenda agenda = getEntityById(id);
        if(agenda == null){
            log.error("Agenda not found with id: '{}'", id);
            throw new NotFoundException("Not found any agenda by id " + id);
        }

        log.debug("Convert Agenda model to Agenda dto and return");
        return new AgendaDto(agenda);
    }

    /**
     * Method to find the Entity Agenda by id
     * @param id the id to search Agenda
     * @return the Entity Agenda
     */
    public Agenda getEntityById(Long id){
        log.info("Try to get Agenda into database with id '{}'", id);
        return repository.findById(id).orElse(null);
    }

    /**
     * Method to find all Agendas who has name equals the param name
     * @param name the name who used to search the Agendas
     * @return A List<AgendaDto> who represents the all Agendas that was found
     */
    public List<AgendaDto> getByName(String name){
        log.info("Try to get Agendas into database with name '{}'", name);
        Set<Agenda> agenda = repository.findByName(name).orElse(null);
        if(agenda == null){
            log.error("Agendas not found with name: '{}'", name);
            throw new NotFoundException("Not found any agenda by name " + name);
        }

        log.debug("Convert Agendas model to Agendas dto and return");
        return agenda.stream().map(AgendaDto::new).toList();
    }

    /**
     * Method to get all Agendas
     * @return A List<AgendaDto> who represents the all Agendas saved
     */
    public List<AgendaDto> getAll(){
        List<AgendaDto> result = new LinkedList<>();
        log.info("Try to get all Agendas into database");
        var agendas = repository.findAll();

        log.debug("Convert Agendas model to Agendas dto");
        agendas.forEach(p -> result.add(new AgendaDto(p)));

        return result;
    }

    /**
     * Method to add a new Agenda
     * @param agendaDto the dto who represent the new Agenda
     */
    public void addAgenda(AgendaDto agendaDto){
        validator.validAgendaName(agendaDto.getName());

        log.info("Try added new Agenda into database");
        Agenda agenda = agendaDto.toModel();
        repository.save(agenda);
    }

    /**
     * Method used to open exist Agenda
     * @param id the id of the Agenda
     * @param hours the hours to the expiration of voting
     * @param minutes the minutes to the expiration of voting
     * @param seconds the seconds to the expiration of voting
     */
    public void openAgenda(Long id, int hours, int minutes, int seconds){
        Agenda agenda =  getEntityById(id);
        if(agenda == null){
            log.error("Agenda not found with id: '{}'", id);
            throw new NotFoundException("Not found any agenda by id " + id);
        }

        log.debug("Define Agenda expiration");
        if(hours == 0 && minutes == 0 && seconds == 0)
            minutes = 1;

        LocalDateTime expiration = LocalDateTime.now()
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);

        log.info("Try update Agenda in database");
        agenda.setExpiration(expiration);
        agenda.getVotes().clear();
        agenda.setOpen(true);
        repository.save(agenda);

        log.info("Send event to Kafka");
        AgendaOpenedEvent event = new AgendaOpenedEvent(agenda.getId(), agenda.getExpiration());
        kafkaTemplate.send("agenda-opened", event);
    }

    /**
     * Method to update exist Agenda
     * @param id the id of exist Agenda
     * @param agendaDto the dto contains data who will be updated
     */
    public void updateAgenda(Long id, AgendaDto agendaDto){
        Agenda agenda =  getEntityById(id);
        if(agenda == null){
            log.error("Agenda not found with id: '{}'", id);
            throw new NotFoundException("Not found any agenda by id " + id);
        }

        log.debug("Validate new Agenda name");
        if(agendaDto.getName() != null && !agendaDto.getName().isEmpty()){
            validator.validAgendaName(agendaDto.getName());
            agenda.setName(agendaDto.getName());
        }

        log.debug("Validate new Agenda details");
        if(agendaDto.getDetails() != null && !agendaDto.getDetails().isEmpty())
            agenda.setDetails(agenda.getDetails());

        log.info("Try update Agenda in database");
        repository.save(agenda);
    }

    /**
     * Method to delete exist Agenda
     * @param id id to use to search Agenda
     */
    public void deleteAgenda(Long id){
        Agenda agenda =  getEntityById(id);
        if(agenda == null){
            log.error("Agenda not found with id: '{}'", id);
            throw new NotFoundException("Not found any agenda by id " + id);
        }

        repository.delete(agenda);
    }

}
