package br.com.redefood.exceptions;

public class ExpiredTokenException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @see Exception
	 */
	public ExpiredTokenException() {
		super();
	} // fim do construtor padrão

	/**
	 * @see Exception
	 */
	public ExpiredTokenException(String message) {
		super(message);
	} // fim do construtor MailException(String)

	/**
	 * @see Exception
	 */
	public ExpiredTokenException(Throwable cause) {
		super(cause);
	} // fim do construtor MailException(Throwable)

	/**
	 * @see Exception
	 */
	public ExpiredTokenException(String message, Throwable cause) {
		super(message, cause);
	} // fim do construtor MailException(String, Throwable)

}
