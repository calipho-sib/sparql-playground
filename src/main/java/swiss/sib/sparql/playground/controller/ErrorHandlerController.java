package swiss.sib.sparql.playground.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import swiss.sib.sparql.playground.exception.SparqlTutorialException;

/**
 * A global error handler
 *  @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@ControllerAdvice
@RestController
public class ErrorHandlerController {

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(SparqlTutorialException.class)
	public Map<String, String> handleError(HttpServletRequest req, SparqlTutorialException exception) throws Exception {
		Map<String, String> response = new HashMap<String, String>();
		response.put("responseText", exception.getLocalizedMessage());
		response.put("status", "404");
		return response;
	}

}