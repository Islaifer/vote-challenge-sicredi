package com.challenge.agenda_processor.repositories;

import com.challenge.agenda_processor.models.Agenda;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AgendaRepository extends CrudRepository<Agenda, Long> {
    Optional<Set<Agenda>> findByIsOpen(boolean isOpen);
}
