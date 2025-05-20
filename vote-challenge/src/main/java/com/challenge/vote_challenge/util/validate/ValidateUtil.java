package com.challenge.vote_challenge.util.validate;

import com.challenge.vote_challenge.exceptions.InvalidEntityException;
import com.challenge.vote_challenge.repositories.AssociateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Class that carries useful validations
 */
@Slf4j
@Component
public class ValidateUtil {
    private final String CPF_VALIDATION_URL = "https://api-cpf.vercel.app";
    private final String CPF_VALIDATION_ENDPOINT = "/cpf/valid/";

    private final WebClient cpfValidator;
    private final AssociateRepository repository;

    @Autowired
    public ValidateUtil(AssociateRepository repository){
        this.repository = repository;
        cpfValidator = WebClient.builder().baseUrl(CPF_VALIDATION_URL).build();
    }

    /**
     * Method that validates whether the calendar name is valid or not
     * @param name the name who will be verified
     */
    public void validAgendaName(String name){
        if(name == null){
            log.error("Agenda's name cannot be null");
            throw new InvalidEntityException("Agenda's name cannot be null");
        }

        if(name.length() < 3 || name.length() > 30){
            log.error("Agenda's name cannot have less than 3 and more than 30 characters");
            throw new InvalidEntityException("Agenda's name cannot have less than 3 and more than 30 characters");
        }
    }

    /**
     * Method that validates whether the CPF is valid
     * @param cpf the cpf who will be verified
     * @return A boolean, true if cpf is valid and false if cpf is invalid
     */
    public boolean isCpfValid(String cpf){
        try{
            validCpfFormat(cpf);
            cpfExists(cpf);

            log.info("Verify cpf with external call");
            log.debug("url: {}\n endpoint: {}", CPF_VALIDATION_URL, CPF_VALIDATION_ENDPOINT);

            String endpoint = CPF_VALIDATION_ENDPOINT + cpf;
            CpfValidateDto response = cpfValidator.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(CpfValidateDto.class)
                    .block();

            return response != null && response.Valid();
        } catch (InvalidEntityException iCEx){
            log.error(iCEx.getMessage());
            throw  iCEx;
        } catch (Exception ex){
            return false;
        }
    }

    private void validCpfFormat(String cpf){
        if(cpf == null)
            throw new InvalidEntityException("Cpf cannot be null");

        if(cpf.length() != 11)
            throw new InvalidEntityException("CPF in invalid format, CPF must have 11 digits and contain only numbers (must not contain periods or symbols)");

        if(!cpf.matches("^\\d+$"))
            throw new InvalidEntityException("CPF must contain only numbers (must not contain periods or symbols)");
    }

    private void cpfExists(String cpf){
        if(repository.existsByCpf(cpf))
            throw new InvalidEntityException("Cpf already exists");
    }
}
