package com.challenge.vote_challenge.repositories;

import com.challenge.vote_challenge.models.Agenda;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AgendaRepository extends CrudRepository<Agenda, Long> {
    Optional<Set<Agenda>> findByName(String name);
}
