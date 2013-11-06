package br.com.redefood.mail.notificator.core;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.redefood.util.RedeFoodConstants;

/**
 * POJO utilitário responsável pelo envio de mensagens para a fila de
 * notificações.
 * 
 * Optou-se nesta versão pela utilização de um POJO com método estático (1) pela
 * simplicidade da implementação, não exigindo assim um SLSB e (2) por não ter
 * sido possível realizar a injeção da Queue e da ConnectionFactory.
 * 
 * Esta classe se propõe apenas a encaminhar a notificação para a fila. Como a
 * notificação será tratada é responsabilidade da classe que processa as
 * mensagens da fila.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class NotificatorSender {
    
    /**
     * Envia uma mensagem para a fila de notificações.
     * 
     * Apenas coloca a mensagem na fila e retorna para o cliente. Em caso de
     * qualquer falha no envio da mensagem para a fila, uma CCMMailException é
     * lançada.
     * 
     * Utilizou-se nesta versão o lookup tradicional para obtenção das
     * referências da Queue e da ConnectionFactory em função de não ter sido
     * possível utilizar a injeção destes recursos.
     * 
     * @param notificacao
     *            Mensagem a ser enviada.
     * @throws Exception
     *             Caso ocorra alguma falha no envio da notificação para a fila.
     */
    public static void send(final MessageNotificator notificacao) throws Exception {
	// variáveis declaradas fora do bloco try...catch para uso no finally
	Connection c = null;
	Session s = null;
	try {
	    // faz o lookup da Queue e da ConnectionFactory
	    InitialContext ic = new InitialContext();
	    
	    Queue q = (Queue) ic.lookup(RedeFoodConstants.JNDI_JMS_QUEUE_NOTIFICACAO);
	    ConnectionFactory cf = (ConnectionFactory) ic.lookup(RedeFoodConstants.JNDI_JMS_CONNECTION_FACTORY);
	    
	    c = cf.createConnection();
	    s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    
	    // cria a mensagem
	    ObjectMessage message = s.createObjectMessage();
	    message.setObject(notificacao);
	    
	    // enviar a mensagem para a fila
	    MessageProducer producer = s.createProducer(q);
	    producer.send(message);
	} catch (NamingException e) {
	    throw new Exception(e);
	} catch (JMSException e) {
	    throw new Exception(e);
	} finally {
	    try {
		// fecha a conexão e a sessão utilizada
		if (c != null) {
		    c.close();
		}
		if (s != null) {
		    s.close();
		}
	    } catch (JMSException e) {
		throw new Exception(e);
	    }
	}
    } // fim do método send(NotificacaoMessage)
    
} // fim da classe NotificacaoSender
