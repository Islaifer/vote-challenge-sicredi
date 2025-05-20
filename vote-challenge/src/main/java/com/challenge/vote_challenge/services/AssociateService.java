package com.challenge.vote_challenge.services;

import com.challenge.vote_challenge.constants.EligibleVoteStatusConstants;
import com.challenge.vote_challenge.dto.AssociateDto;
import com.challenge.vote_challenge.dto.EligibleVoteStatus;
import com.challenge.vote_challenge.exceptions.InvalidEntityException;
import com.challenge.vote_challenge.exceptions.NotFoundException;
import com.challenge.vote_challenge.models.Associate;
import com.challenge.vote_challenge.repositories.AssociateRepository;
import com.challenge.vote_challenge.util.validate.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class representing the service class of the Associate entity
 */
@Slf4j
@Service
public class AssociateService {
    private final AssociateRepository repository;
    private final ValidateUtil validator;

    @Autowired
    public AssociateService(AssociateRepository repository, ValidateUtil validator){
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Method to search Associate by id
     * @param id id to search the Associate
     * @return A AssociateDto who represent the entity Associate
     */
    public AssociateDto getById(Long id){
        Associate associate =  getEntityById(id);
        if(associate == null){
            log.error("Associate not found with id: '{}'", id);
            throw new NotFoundException("Doesn't have any associate with id '" + id + "'");
        }

        log.debug("Convert Associate Model to Associate dto");
        return new AssociateDto(associate);
    }

    /**
     * Method to get the entity of Associate by id
     * @param id id to search the Associate
     * @return the entity Associate
     */
    public Associate getEntityById(Long id){
        log.info("Try to get Associate into database with id '{}'", id);
        return repository.findById(id).orElse(null);
    }

    /**
     * Method to get the entity of Associate by cpf
     * @param cpf cpf to search the Associate
     * @return the entity Associate
     */
    public Associate getEntityByCpf(String cpf){
        log.info("Try to get Associate into database with cpf '{}'", cpf);
        return repository.findByCpf(cpf).orElse(null);
    }

    /**
     * Method to search Associate by cpf
     * @param cpf cpf to search the Associate
     * @return A AssociateDto who represent the entity Associate
     */
    public AssociateDto getByCpf(String cpf){
        Associate associate = getEntityByCpf(cpf);
        if(associate == null){
            log.error("Associate not found with cpf: '{}'", cpf);
            throw new NotFoundException("Doesn't have any associate with cpf '" + cpf + "'");
        }

        return new AssociateDto(associate);
    }

    /**
     * Method to return all Associates
     * @return List<AssociateDto> who represent all Associates
     */
    public List<AssociateDto> getAll(){
        log.info("Try to get all Associates into database");
        List<AssociateDto> result = new LinkedList<>();
        var associates = repository.findAll();

        log.debug("Convert Associates model to Associates dto");
        associates.forEach(p -> result.add(new AssociateDto(p)));

        return result;
    }

    /**
     * Method used to add new Associate
     * @param associateDto the new Associate
     * @return the EligibleVoteStatus who contain the information if the Associate can be vote or not
     */
    public EligibleVoteStatus addAssociate(AssociateDto associateDto){
        if(!validator.isCpfValid(associateDto.getCpf())){
            log.warn("Cpf '{}' is invalid", associateDto.getCpf());
            return new EligibleVoteStatus(EligibleVoteStatusConstants.UNABLE_TO_VOTE);
        }

        Random random = new Random();
        if(associateDto.getId() == null || associateDto.getId() == 0){
            log.info("Define new Id");
            long newId = random.nextLong();
            while (repository.existsById(newId)){
                newId = random.nextLong(1L, Long.MAX_VALUE);
            }

            associateDto.setId(newId);
        }

        if(repository.existsById(associateDto.getId())){
            log.error("Associate Id already exists");
            throw new InvalidEntityException("Id already exist.");
        }

        log.info("Try to added Associate into database");
        Associate associate = associateDto.toModel();
        associate.setId(associateDto.getId());
        repository.save(associate);

        return new EligibleVoteStatus(EligibleVoteStatusConstants.ABLE_TO_VOTE);
    }

    /**
     * Method to update exist Associate
     * @param id the id to search the Associate
     * @param associateDto the dto who contains the new data of the Associate
     */
    public void updateAssociate(Long id, AssociateDto associateDto){
        Associate associate =  getEntityById(id);
        if(associate == null){
            log.error("Associate not found with id: '{}'", id);
            throw new NotFoundException("Associate not found with id " + id);
        }

        if(!validator.isCpfValid(associateDto.getCpf())){
            log.error("Cpf is {}", EligibleVoteStatusConstants.UNABLE_TO_VOTE);
            throw new InvalidEntityException("Cpf is " + EligibleVoteStatusConstants.UNABLE_TO_VOTE);
        }

        log.info("Try update associate into database");
        associate.setCpf(associateDto.getCpf());
        repository.save(associate);
    }

    /**
     * Method to delete exist Associate by id
     * @param id the id to search exist Associate
     */
    public void deleteAssociate(Long id){
        Associate associate =  getEntityById(id);
        if(associate == null){
            log.error("Associate not found with id: '{}'", id);
            throw new NotFoundException("Associate not found with id " + id);
        }

        log.info("Try delete Associate into database");
        repository.delete(associate);
    }
}
