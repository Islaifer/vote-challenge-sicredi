package com.challenge.vote_challenge.unit.util.validate;

import com.challenge.vote_challenge.exceptions.InvalidEntityException;
import com.challenge.vote_challenge.repositories.AssociateRepository;
import com.challenge.vote_challenge.util.validate.ValidateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidateUtilTest {

    @Mock
    private AssociateRepository repository;

    @InjectMocks
    private ValidateUtil validateUtil;

    @Test
    void validAgendaName_shouldThrowException_whenNameIsNull() {
        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            validateUtil.validAgendaName(null);
        });
        assertEquals("Agenda's name cannot be null", ex.getMessage());
    }

    @Test
    void validAgendaName_shouldThrowException_whenNameTooShort() {
        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            validateUtil.validAgendaName("ab");
        });
        assertEquals("Agenda's name cannot have less than 3 and more than 30 characters", ex.getMessage());
    }

    @Test
    void validAgendaName_shouldThrowException_whenNameTooLong() {
        String longName = "a".repeat(31);
        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> {
            validateUtil.validAgendaName(longName);
        });
        assertEquals("Agenda's name cannot have less than 3 and more than 30 characters", ex.getMessage());
    }

    @Test
    void validAgendaName_shouldPass_whenNameIsValid() {
        assertDoesNotThrow(() -> {
            validateUtil.validAgendaName("Nome válido");
        });
    }

    @Test
    void isCpfValid_shouldReturnTrue_whenCpfIsValid() {
        String cpf = "97525134090";
        when(repository.existsByCpf(cpf)).thenReturn(false);

        boolean result = validateUtil.isCpfValid(cpf);

        assertTrue(result);
    }

    @Test
    void isCpfValid_shouldReturnFalse_whenCpfIsInvalid() {
        String cpf = "12345678901";
        boolean result = validateUtil.isCpfValid(cpf);

        assertFalse(result);
    }


    @Test
    void isCpfValid_shouldThrowInvalidEntityException_whenValidCpfFormatFails() {
        String cpf = "123";

        assertThrows(InvalidEntityException.class, () -> {
            validateUtil.isCpfValid(cpf);
        });
    }
}
