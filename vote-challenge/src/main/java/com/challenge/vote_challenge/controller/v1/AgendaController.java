package com.challenge.vote_challenge.controller.v1;

import com.challenge.vote_challenge.dto.AgendaDto;
import com.challenge.vote_challenge.services.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Control class to provide call endpoints involving the Agenda object
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1/agendas")
@Tag(name = "Agenda", description = "Agenda calls")
public class AgendaController {
    private final AgendaService service;

    @Autowired
    public AgendaController(AgendaService service) {
        this.service = service;
    }

    /**
     * Method that searches for an AgendaDto by id
     * <p>
     * This method is an endpoint for Agenda calls,
     * more specifically the endpoint GET /api/v1/agendas/{id}
     * </p>
     * @param id the id to search for the specific Agenda
     * @return A ResponseEntity<AgendaDo> of the Agenda that was found via id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Agenda by Id")
    public ResponseEntity<AgendaDto> getById(@PathVariable("id") Long id){
        log.info("Start to get Agenda by id with id: '{}'", id);
        AgendaDto agenda = service.getById(id);

        log.info("Agenda was found");
        return ResponseEntity.ok(agenda);
    }

    /**
     * Method that searches for all AgendaDto or filtered all by name
     * <p>
     * This method is an endpoint for Agenda calls,
     * more specifically the endpoint GET /api/v1/agendas
     * </p>
     * @param name the name to search for the Agendas (optional parameter)
     * @return A ResponseEntity<List<AgendaDo>> of the Agenda that was found
     */
    @GetMapping
    @Operation(summary = "Get All Agendas and accept a filter 'name'")
    public ResponseEntity<List<AgendaDto>> getAll(@RequestParam(value = "name", defaultValue = "") String name){
        log.info("Start get agendas");
        List<AgendaDto> agendas;

        if(name.isEmpty()){
            log.debug("Doesn't use filter");
            agendas = service.getAll();
        } else{
            log.debug("Use name filter '{}'", name);
            agendas = service.getByName(name);
        }

        log.info("Agendas was found");
        return ResponseEntity.ok(agendas);
    }

    /**
     * Method that adds a new agenda
     * <p>
     * This method is an endpoint for Agenda calls,
     * more specifically the endpoint POST /api/v1/agendas
     * </p>
     * @param agenda represents the new agenda that will be inserted
     * @return A ResponseEntity<String> that tells you if it was created successfully
     */
    @PostMapping
    @Operation(summary = "Create new Agenda")
    public ResponseEntity<String> post(@RequestBody AgendaDto agenda){
        log.info("Start post new Agenda with name '{}' and details '{}'", agenda.getName(), agenda.getDetails());
        service.addAgenda(agenda);

        log.info("New Agenda was posted");
        return ResponseEntity.ok("Agenda has been created");
    }

    /**
     * Method that opens an Agenda
     * <p>
     * This method is an endpoint for Agenda calls,
     * more specifically the endpoint PUT /api/v1/agendas/open/{id}
     * </p>
     * @param id represents the agenda that will be opened
     * @param hours the hours that agenda will be opened (optional param)
     * @param minutes the minutes that agenda will be opened (optional param)
     * @param seconds the seconds that agenda will be opened (optional param)
     * @return A ResponseEntity<String> that tells you if it was opened successfully
     */
    @PutMapping("/open/{id}")
    @Operation(summary = "Open a exist Agenda, accept three query params to define expiration, 'hours', 'minutes' and 'seconds', all integer")
    public ResponseEntity<String> openAgenda(@PathVariable("id") Long id,
                                             @RequestParam(value = "hours", defaultValue = "0") int hours,
                                             @RequestParam(value = "minutes", defaultValue = "0") int minutes,
                                             @RequestParam(value = "seconds", defaultValue = "0") int seconds){
        log.info("Start open Agenda with id '{}'", id);
        log.debug("Expiration params: hour:'{}', minutes:'{}', seconds:'{}'", hours, minutes, seconds);

        service.openAgenda(id, hours, minutes, seconds);

        log.info("Agenda was opened");
        return ResponseEntity.status(HttpStatus.CREATED).body("Agenda was opened");
    }

    /**
     * Method that updates an Agenda
     * <p>
     * This method is an endpoint for Agenda calls,
     * more specifically the endpoint PUT /api/v1/agendas/{id}
     * </p>
     * @param id represents the agenda that will be updated
     * @return A ResponseEntity<String> that tells you if it was updated successfully
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a exist Agenda by id")
    public ResponseEntity<String> put(@PathVariable("id") Long id, @RequestBody AgendaDto agenda){
        log.info("Start update agenda with id '{}'", id);
        service.updateAgenda(id, agenda);

        log.info("Agenda was updated");
        return ResponseEntity.ok("Agenda has been updated");
    }

    /**
     * Method that deletes an Agenda
     * <p>
     * This method is an endpoint for Agenda calls,
     * more specifically the endpoint DELETE /api/v1/agendas/{id}
     * </p>
     * @param id represents the agenda that will be deleted
     * @return A ResponseEntity<String> that tells you if it was deleted successfully
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a exist Agenda by Id")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        log.info("Start delete agenda with id '{}'", id);
        service.deleteAgenda(id);

        log.info("Agenda was deleted");
        return ResponseEntity.ok("Agenda has been deleted");
    }
}
