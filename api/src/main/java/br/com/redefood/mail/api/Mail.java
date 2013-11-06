package br.com.redefood.mail.api;

import br.com.redefood.mail.core.HtmlMessage;
import br.com.redefood.mail.core.exceptions.MailException;

/**
 * Classe auxiliar do processo de envio de e-mails.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class Mail {
    
    /**
     * Cria uma nova instância de uma mensagem HTML.
     * 
     * @param jndiName
     *            Nome JNDI do serviço de e-mail.
     * @return Nova instância de uma mensagem HTML.
     * @throws MailException
     *             Caso ocorra alguma falha na criação da mensagem.
     */
    public static final HtmlMessage newHtmlMessage(String jndiName) throws MailException {
	return new HtmlMessage(jndiName);
    } // fim do método newHtmlMessage(String)
} // fim da classe Mail
