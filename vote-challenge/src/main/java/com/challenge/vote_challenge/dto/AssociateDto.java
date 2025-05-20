package com.challenge.vote_challenge.dto;

import com.challenge.vote_challenge.interfaces.IDtoFrom;
import com.challenge.vote_challenge.models.Associate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The DTO of Associate entity
 */
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class AssociateDto implements IDtoFrom<Associate> {
    private Long id;
    private String cpf;

    public AssociateDto(Associate associate){
        copyFrom(associate);
    }

    @Override
    public void copyFrom(Associate model) {
        id = model.getId();
        cpf = model.getCpf();
    }

    @Override
    public Associate toModel() {
        return new Associate(this.cpf);
    }
}
