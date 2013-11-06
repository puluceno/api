package br.com.redefood.mail.notificator.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import br.com.redefood.mail.api.Mail;
import br.com.redefood.mail.api.MailMessage;
import br.com.redefood.mail.core.exceptions.MailException;
import br.com.redefood.util.RedeFoodConstants;

/**
 * Message Driven Bean responsável pelo processamento de notificações.
 * 
 * A fila de notificações tem por objetivo tratar todos os tipos de notificações
 * automáticas originadas pelo sistema. Nesta versão, as notificações são
 * enviadas apenas por e-mail.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/queue/EmailsQueue") })
public class NotificatorReceiver implements MessageListener {
    
    /**
     * Logger do sistema
     */
    @Inject
    private Logger log;
    
    /**
     * Processa uma mensagem da fila de notificações.
     * 
     * Recebe uma mensagem da fila de notificações e a envia por e-mail. As
     * informações pertinentes ao destinatário, assunto e conteúdo da mensagem
     * já estão definidas no conteúdo da mensagem.
     * 
     * O envio do e-mail é realizada utilizando-se a API SubsMail.
     * 
     * @param message
     *            Mensagem da fila de notificações.
     * @see Mail
     * @see MailMessage
     */
    @Override
    public void onMessage(final Message message) {
	ObjectMessage objectMessage = (ObjectMessage) message;
	
	try {
	    MessageNotificator notificacao = (MessageNotificator) objectMessage.getObject();
	    
	    // instancia uma mensagem de e-mail passando o nome JNDI do serviço
	    // de e-mail configurado no AS
	    MailMessage mailMessage = Mail.newHtmlMessage(RedeFoodConstants.JNDI_MAIL_SERVICE);
	    
	    // envio o e-mail
	    mailMessage.to(notificacao.getDestinatarios()).subject(notificacao.getAssunto())
	    .subjectCharset(RedeFoodConstants.MAIL_CHARSET).body(notificacao.getConteudo())
	    .bodyCharset(RedeFoodConstants.MAIL_CHARSET).send();
	    
	} catch (MailException e) {
	    e.printStackTrace();
	    log.log(Level.SEVERE, e.getMessage(), e);
	    
	} catch (JMSException e) {
	    log.log(Level.SEVERE, e.getMessage(), e);
	    e.printStackTrace();
	}
    } // fim do método onMessage(Message)
    
} // fim da classe NotificacaoReceiver