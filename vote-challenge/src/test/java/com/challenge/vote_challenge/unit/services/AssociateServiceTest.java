package com.challenge.vote_challenge.unit.services;

import com.challenge.vote_challenge.constants.EligibleVoteStatusConstants;
import com.challenge.vote_challenge.dto.AssociateDto;
import com.challenge.vote_challenge.dto.EligibleVoteStatus;
import com.challenge.vote_challenge.exceptions.InvalidEntityException;
import com.challenge.vote_challenge.exceptions.NotFoundException;
import com.challenge.vote_challenge.models.Associate;
import com.challenge.vote_challenge.repositories.AssociateRepository;
import com.challenge.vote_challenge.services.AssociateService;
import com.challenge.vote_challenge.util.validate.ValidateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssociateServiceTest {

    @Mock
    private AssociateRepository repository;

    @Mock
    private ValidateUtil validator;

    @InjectMocks
    private AssociateService service;

    @Test
    void getById_shouldReturnAssociateDto_whenIdExist(){
        Long id = 1L;
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        Associate associate = new Associate(1L, "97525134090");

        when(repository.findById(id)).thenReturn(Optional.of(associate));

        AssociateDto result = service.getById(id);

        assertEquals(associateDto.getId(), result.getId());
    }

    @Test
    void getById_shouldThrowException_whenIdNotExist(){
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.getById(id);
        });
        assertEquals("Doesn't have any associate with id '" + id + "'", ex.getMessage());
    }

    @Test
    void getByCpf_shouldReturnAssociateDto_whenCpfExist(){
        String cpf = "97525134090";
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        Associate associate = new Associate(1L, "97525134090");

        when(repository.findByCpf(cpf)).thenReturn(Optional.of(associate));

        AssociateDto result = service.getByCpf(cpf);

        assertEquals(associateDto.getId(), result.getId());
    }

    @Test
    void getByCpf_shouldReturnException_whenCpfNotExist(){
        String cpf = "97525134090";

        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.getByCpf(cpf);
        });
        assertEquals("Doesn't have any associate with cpf '" + cpf + "'", ex.getMessage());
    }

    @Test
    void addAssociate_shouldReturnAbleToVote_whenIdNotExistAndCpfIsValid(){
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        EligibleVoteStatus status = new EligibleVoteStatus(EligibleVoteStatusConstants.ABLE_TO_VOTE);

        when(validator.isCpfValid(associateDto.getCpf())).thenReturn(true);
        when(repository.existsById(associateDto.getId())).thenReturn(false);
        when(repository.save(any())).thenReturn(new Associate());

        EligibleVoteStatus result = service.addAssociate(associateDto);
        assertEquals(status, result);
    }

    @Test
    void addAssociate_shouldReturnUnableToVote_whenCpfIsInvalid(){
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        EligibleVoteStatus status = new EligibleVoteStatus(EligibleVoteStatusConstants.UNABLE_TO_VOTE);

        when(validator.isCpfValid(associateDto.getCpf())).thenReturn(false);

        EligibleVoteStatus result = service.addAssociate(associateDto);
        assertEquals(status, result);
    }

    @Test
    void addAssociate_shouldThrowException_whenIdExist(){
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");

        when(validator.isCpfValid(associateDto.getCpf())).thenReturn(true);
        when(repository.existsById(associateDto.getId())).thenReturn(true);

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            service.addAssociate(associateDto);
        });
        assertEquals("Id already exist.", ex.getMessage());
    }

    @Test
    void updateAssociate_shouldThrowException_whenIdNotExist(){
        Long id = 1L;
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.updateAssociate(id, associateDto);
        });
        assertEquals("Associate not found with id " + id, ex.getMessage());
    }

    @Test
    void updateAssociate_shouldThrowException_whenCpfIsInvalid(){
        Long id = 1L;
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        Associate associate = new Associate();

        when(repository.findById(id)).thenReturn(Optional.of(associate));
        when(validator.isCpfValid(associateDto.getCpf())).thenReturn(false);

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            service.updateAssociate(id, associateDto);
        });
        assertEquals("Cpf is " + EligibleVoteStatusConstants.UNABLE_TO_VOTE, ex.getMessage());
    }

    @Test
    void updateAssociate_shouldUpdateData_whenIdAndDtoIsValid(){
        Long id = 1L;
        AssociateDto associateDto = new AssociateDto(1L, "97525134090");
        Associate associate = new Associate();

        when(repository.findById(id)).thenReturn(Optional.of(associate));
        when(validator.isCpfValid(associateDto.getCpf())).thenReturn(true);

        service.updateAssociate(id, associateDto);
        verify(repository, times(1)).save(associate);
    }

    @Test
    void deleteAssociate_shouldThrowException_whenIdNotExist(){
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
           service.deleteAssociate(id);
        });

        assertEquals("Associate not found with id " + id, ex.getMessage());
    }

    @Test
    void deleteAssociate_shouldDeleteData_whenIdExist(){
        Long id = 1L;
        Associate associate = new Associate();

        when(repository.findById(id)).thenReturn(Optional.of(associate));
        service.deleteAssociate(id);
        verify(repository, times(1)).delete(associate);
    }
}
