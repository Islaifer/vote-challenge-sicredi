package com.challenge.vote_challenge.unit.services;

import com.challenge.vote_challenge.dto.AgendaDto;
import com.challenge.vote_challenge.dto.AgendaOpenedEvent;
import com.challenge.vote_challenge.exceptions.NotFoundException;
import com.challenge.vote_challenge.models.Agenda;
import com.challenge.vote_challenge.repositories.AgendaRepository;
import com.challenge.vote_challenge.services.AgendaService;
import com.challenge.vote_challenge.util.validate.ValidateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendaServiceTest {
    @Mock
    private AgendaRepository repository;

    @Mock
    private KafkaTemplate<String, AgendaOpenedEvent> kafkaTemplate;

    @Mock
    private ValidateUtil validator;

    @InjectMocks
    private AgendaService service;

    @Test
    void getById_shouldReturnCorrect_whenIdExist(){
        Long id = 1L;
        LocalDateTime date = LocalDateTime.now();
        AgendaDto agendaDto = new AgendaDto(1L, "Pokemon", "Pokemon Details", date, new LinkedList<>(), false);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon Details", date, new HashSet<>(), false);

        when(repository.findById(id)).thenReturn(Optional.of(agenda));

        AgendaDto result = service.getById(id);

        assertEquals(agendaDto.getId(), result.getId());
    }

    @Test
    void getById_shouldThrowException_whenIdNotExist(){
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
           service.getById(id);
        });

        assertEquals("Not found any agenda by id " + id, ex.getMessage());
    }

    @Test
    void getByName_shouldReturnList_whenNameExist(){
        String name = "Pokemon";

        Set<Agenda> agendas = new HashSet<>();
        agendas.add(new Agenda(1L, "Pokemon", "Pokemon Details", LocalDateTime.now(), new HashSet<>(), false));
        agendas.add(new Agenda(2L, "Pokemon", "Pokemon Details 2", LocalDateTime.now(), new HashSet<>(), false));
        agendas.add(new Agenda(3L, "Pokemon", "Pokemon Details 3", LocalDateTime.now(), new HashSet<>(), false));
        agendas.add(new Agenda(4L, "Pokemon", "Pokemon Details 4", LocalDateTime.now(), new HashSet<>(), false));
        agendas.add(new Agenda(5L, "Pokemon", "Pokemon Details 5", LocalDateTime.now(), new HashSet<>(), false));

        when(repository.findByName(name)).thenReturn(Optional.of(agendas));

        List<AgendaDto> agendasDto = service.getByName(name);

        for(var agendaDto : agendasDto){
            assertEquals(name, agendaDto.getName());
        }
    }

    @Test
    void getByName_shouldThrowException_whenNameNotExist(){
        String name = "Pokemon";

        when(repository.findByName(name)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
           service.getByName(name);
        });

        assertEquals("Not found any agenda by name " + name, ex.getMessage());
    }

    @Test
    void openAgenda_shouldExecuteSaveAndSend_whenIdExist(){
        Long id = 1L;
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon Details", LocalDateTime.now(), new HashSet<>(), false);

        when(repository.findById(id)).thenReturn(Optional.of(agenda));

        service.openAgenda(id, hour, minutes, seconds);
        verify(repository, times(1)).save(agenda);
        verify(kafkaTemplate, times(1)).send(any(), any());
    }

    @Test
    void openAgenda_shouldThrowException_whenIdNotExist(){
        Long id = 1L;
        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.openAgenda(id, hour, minutes, seconds);
        });

        assertEquals("Not found any agenda by id " + id, ex.getMessage());
    }

    @Test
    void updateAgenda_shouldExecuteSave_whenIdExist(){
        Long id = 1L;
        LocalDateTime date = LocalDateTime.now();
        AgendaDto agendaDto = new AgendaDto(1L, "Pokemon", "Pokemon Details", date, new LinkedList<>(), false);
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon Details", date, new HashSet<>(), false);

        when(repository.findById(id)).thenReturn(Optional.of(agenda));

        service.updateAgenda(id, agendaDto);
        verify(repository, times(1)).save(agenda);
    }

    @Test
    void updateAgenda_shouldThrowException_whenIdNotExist(){
        Long id = 1L;
        LocalDateTime date = LocalDateTime.now();
        AgendaDto agendaDto = new AgendaDto(1L, "Pokemon", "Pokemon Details", date, new LinkedList<>(), false);

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.updateAgenda(id, agendaDto);
        });

        assertEquals("Not found any agenda by id " + id, ex.getMessage());
    }

    @Test
    void deleteAgenda_shouldExecuteDelete_whenIdExist(){
        Long id = 1L;
        LocalDateTime date = LocalDateTime.now();
        Agenda agenda = new Agenda(1L, "Pokemon", "Pokemon Details", date, new HashSet<>(), false);

        when(repository.findById(id)).thenReturn(Optional.of(agenda));

        service.deleteAgenda(id);
        verify(repository, times(1)).delete(agenda);
    }

    @Test
    void deleteAgenda_shouldThrowException_whenIdNotExist(){
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.deleteAgenda(id);
        });

        assertEquals("Not found any agenda by id " + id, ex.getMessage());
    }
}
