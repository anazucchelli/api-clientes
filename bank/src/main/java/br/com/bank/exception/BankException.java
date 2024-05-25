package br.com.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.bank.model.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class BankException {

	@ExceptionHandler(value = EntityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleEntityNotFoundException(final EntityNotFoundException exception) {
		log.error(exception.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ErrorMessage.builder().message(exception.getMessage()).build());

	}

}
