package br.com.redefood.mail.core.exceptions;

/**
 * Checked exception que representa todas as falhas relacionadas a tratamento de
 * e-mails, permitindo assim a captura de uma unica exception.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class MailException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @see Exception
     */
    public MailException() {
	super();
    } // fim do construtor padrão
    
    /**
     * @see Exception
     */
    public MailException(String message) {
	super(message);
    } // fim do construtor MailException(String)
    
    /**
     * @see Exception
     */
    public MailException(Throwable cause) {
	super(cause);
    } // fim do construtor MailException(Throwable)
    
    /**
     * @see Exception
     */
    public MailException(String message, Throwable cause) {
	super(message, cause);
    } // fim do construtor MailException(String, Throwable)
    
} // fim da classe MailException
