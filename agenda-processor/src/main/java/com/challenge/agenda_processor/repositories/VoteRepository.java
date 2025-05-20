package com.challenge.agenda_processor.repositories;

import com.challenge.agenda_processor.models.Vote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {
    Optional<Set<Vote>> findAllByAgendaId(Long agendaId);
}
