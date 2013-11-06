package br.com.redefood.mail.core.exceptions;

/**
 * Checked exception que representa falhas no envio de e-mails.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class SendFailedException extends MailException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @see Exception
     */
    public SendFailedException() {
	super();
    } // fim do construtor padrão
    
    /**
     * @see Exception
     */
    public SendFailedException(String message) {
	super(message);
    } // fim do construtor SendFailedException(String)
    
    /**
     * @see Exception
     */
    public SendFailedException(Throwable cause) {
	super(cause);
    } // fim do construtor SendFailedException(Throwable)
    
    /**
     * @see Exception
     */
    public SendFailedException(String message, Throwable cause) {
	super(message, cause);
    } // fim do construtor SendFailedException(String, Throwable)
    
} // fim da classe SendFailedException
