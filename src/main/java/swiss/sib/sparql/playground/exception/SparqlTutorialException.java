package swiss.sib.sparql.playground.exception;

/**
 * A generic exception handled by the global controller #ErrorHandlerController
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class SparqlTutorialException extends RuntimeException {

	public SparqlTutorialException(Exception e) {
		super(e);
	}

	public SparqlTutorialException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 4604803406207435360L;

}