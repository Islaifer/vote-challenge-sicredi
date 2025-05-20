package com.challenge.vote_challenge.controller.v1;

import com.challenge.vote_challenge.dto.AssociateDto;
import com.challenge.vote_challenge.dto.EligibleVoteStatus;
import com.challenge.vote_challenge.services.AssociateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Control class to provide call endpoints involving the Associate object
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1/associates")
@Tag(name = "Associate", description = "Associate calls")
public class AssociateController {
    private final AssociateService service;

    @Autowired
    public AssociateController(AssociateService service) {
        this.service = service;
    }

    /**
     * Method that searches for an AssociateDto by id
     * <p>
     * This method is an endpoint for Associate calls,
     * more specifically the endpoint GET /api/v1/associates/{id}
     * </p>
     * @param id the id to search for the specific Associates
     * @return A ResponseEntity<AssociateDto> of the Associate that was found via id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Associate by Id")
    public ResponseEntity<AssociateDto> getById(@PathVariable("id") Long id){
        log.info("Start get Associate with id '{}'", id);
        AssociateDto associate = service.getById(id);

        log.info("Associate was found");
        return ResponseEntity.ok(associate);
    }

    /**
     * Method that searches for an AssociateDto by cpf
     * <p>
     * This method is an endpoint for Associate calls,
     * more specifically the endpoint GET /api/v1/associates/{cpf}
     * </p>
     * @param cpf the cpf to search for the specific Associates
     * @return A ResponseEntity<AssociateDto> of the Associate that was found via cpf
     */
    @GetMapping("/byCpf/{cpf}")
    @Operation(summary = "Get Associate by Cpf")
    public ResponseEntity<AssociateDto> getByCpf(@PathVariable("cpf") String cpf){
        log.info("Start get associate by cpf '{}'", cpf);
        AssociateDto associate = service.getByCpf(cpf);

        log.info("Associate was found");
        return ResponseEntity.ok(associate);
    }

    /**
     * Method that searches for all AssociateDto or filtered all by name
     * <p>
     * This method is an endpoint for Associate calls,
     * more specifically the endpoint GET /api/v1/associates
     * </p>
     * @return A ResponseEntity<List<AssociateDto>> of the Associates that was found
     */
    @GetMapping
    @Operation(summary = "Get all Associates")
    public ResponseEntity<List<AssociateDto>> getAll(){
        log.info("Start get all Associates");
        List<AssociateDto> associates = service.getAll();

        log.info("Associates was found");
        return ResponseEntity.ok(associates);
    }

    /**
     * Method that adds a new Associate
     * <p>
     * This method is an endpoint for Associate calls,
     * more specifically the endpoint POST /api/v1/associates
     * </p>
     * @param associateDto represents the new associate that will be inserted
     * @return A ResponseEntity<EligibleVoteStatus> that represents whether the member is eligible to vote
     */
    @PostMapping
    @Operation(summary = "Create new Associate")
    public ResponseEntity<EligibleVoteStatus> post(@RequestBody AssociateDto associateDto){
        log.info("Start post new Associate with id '{}' and cpf '{}'", associateDto.getId(), associateDto.getCpf());
        EligibleVoteStatus status = service.addAssociate(associateDto);

        return ResponseEntity.ok(status);
    }

    /**
     * Method that updates an Associate
     * <p>
     * This method is an endpoint for Associate calls,
     * more specifically the endpoint PUT /api/v1/associates/{id}
     * </p>
     * @param id represents the associate that will be updated
     * @return A ResponseEntity<String> that tells you if it was updated successfully
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a exist associate by id")
    public ResponseEntity<String> put(@PathVariable("id") Long id, @RequestBody AssociateDto associateDto){
        log.info("Update Associate with id '{}'", id);
        service.updateAssociate(id, associateDto);

        log.info("Associate was updated");
        return ResponseEntity.ok("Associate has been updated");
    }

    /**
     * Method that deletes an Associate
     * <p>
     * This method is an endpoint for Associate calls,
     * more specifically the endpoint DELETE /api/v1/associates/{id}
     * </p>
     * @param id represents the associate that will be deleted
     * @return A ResponseEntity<String> that tells you if it was deleted successfully
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a exist Associate by id")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        log.info("Delete Associate with id '{}'", id);
        service.deleteAssociate(id);

        log.info("Associate was deleted");
        return ResponseEntity.ok("Associate has been deleted");
    }
}
