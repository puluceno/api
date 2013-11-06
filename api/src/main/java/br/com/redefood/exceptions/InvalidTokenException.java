package br.com.redefood.exceptions;

public class InvalidTokenException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @see Exception
	 */
	public InvalidTokenException() {
		super();
	} // fim do construtor padrão

	/**
	 * @see Exception
	 */
	public InvalidTokenException(String message) {
		super(message);
	} // fim do construtor MailException(String)

	/**
	 * @see Exception
	 */
	public InvalidTokenException(Throwable cause) {
		super(cause);
	} // fim do construtor MailException(Throwable)

	/**
	 * @see Exception
	 */
	public InvalidTokenException(String message, Throwable cause) {
		super(message, cause);
	} // fim do construtor MailException(String, Throwable)

}
