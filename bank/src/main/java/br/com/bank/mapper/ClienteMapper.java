package br.com.bank.mapper;

import org.springframework.stereotype.Component;

import br.com.bank.model.Cliente;
import br.com.bank.model.ClienteRequestDTO;
import br.com.bank.model.ClienteResponseDTO;

import org.mapstruct.Mapper;

@Component
@Mapper(componentModel = "spring")
public interface ClienteMapper {
	
	//pega a request e transf na entidade
	Cliente toEntity(ClienteRequestDTO clienteRequestDTO);
	//pega os dados da entidade e transf na resposta
	ClienteResponseDTO toDto(Cliente cliente);

}
