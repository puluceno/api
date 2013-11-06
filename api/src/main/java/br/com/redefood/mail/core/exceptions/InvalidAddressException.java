package br.com.redefood.mail.core.exceptions;

/**
 * Checked exception que representa falhas na validação de endereços de e-mail.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class InvalidAddressException extends MailException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @see Exception
     */
    public InvalidAddressException() {
	super();
    } // fim do construtor padrão
    
    /**
     * @see Exception
     */
    public InvalidAddressException(String message) {
	super(message);
    } // fim do construtor InvalidAddressException(String)
    
    /**
     * @see Exception
     */
    public InvalidAddressException(Throwable cause) {
	super(cause);
    } // fim do construtor InvalidAddressException(Throwable)
    
    /**
     * @see Exception
     */
    public InvalidAddressException(String message, Throwable cause) {
	super(message, cause);
    } // fim do construtor InvalidAddressException(String, Throwable)
    
} // fim da classe InvalidAddressException
