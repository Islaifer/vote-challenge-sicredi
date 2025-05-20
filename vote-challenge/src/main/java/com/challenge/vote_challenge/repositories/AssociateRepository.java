package com.challenge.vote_challenge.repositories;

import com.challenge.vote_challenge.models.Associate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssociateRepository extends CrudRepository<Associate, Long> {
    Optional<Associate> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
