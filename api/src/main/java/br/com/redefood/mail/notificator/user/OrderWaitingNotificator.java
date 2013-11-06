package br.com.redefood.mail.notificator.user;

import java.text.MessageFormat;
import java.util.HashMap;

import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.core.MessageNotificator;
import br.com.redefood.mail.notificator.core.NotificatorSender;
import br.com.redefood.model.User;

/**
 * Representação de um notificador para cadastro de novos usuários.
 * 
 * O objetivo desta classe é enviar uma notificação para um novo usuário,
 * informando que seu cadastro foi realizado no sistema, fornecendo seu login e
 * senha de acesso.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class OrderWaitingNotificator extends Notificator {
    
    /**
     * Construtor da classe.
     * 
     * Define o template desta mensagem de notificação.
     */
    public OrderWaitingNotificator() {
	setTemplate("mailtemplates/orderwaiting.mailtemplate");
    } // fim do construtor padrão
    
    /**
     * Envia uma notificação.
     * 
     * A implementação consiste em criar uma mensagem de notificação e solicitar
     * seu envio para a fila de notificações do sistema, retornando o controle
     * da ação para o usuário.
     * 
     * @param emailData
     *            Parâmetros para criação da mensagem.
     * @see Notificator#enviar(Object...)
     */
    @Override
    public void send(final HashMap<String, String> emailData) throws Exception {
	NotificatorSender.send(buildMessage(emailData));
    } // fim do método enviar(Object...)
    
    /**
     * Prepara uma mensagem.
     * 
     * Para a preparação da mensagem é necessário que o primeiro parâmetro seja
     * do tipo {@link User} e o segundo parâmetro do tipo {@link String}.
     * 
     * A implementação deste método consiste em passar o template da mensagem
     * para um {@link MessageFormat}, que substitui os parâmetros do template
     * pelas informações necessárias, e adicionar um assunto para a notificação.
     * 
     * @param template
     *            Template da mensagem.
     * @param emailData
     *            Parâmetros para criação da mensagem.
     * @see Notificator#prepareMessage(String, Object...)
     */
    @Override
    protected MessageNotificator prepareMessage(final String template, final HashMap<String, String> emailData)
	    throws Exception {
	
	String conteudo = MessageFormat.format(template, emailData.get("subsidiaryUrl"), emailData.get("logo"),
		emailData.get("subsidiaryName"), emailData.get("facebook"), emailData.get("userName"),
		emailData.get("orderNumber"), emailData.get("orderMade"), emailData.get("restaurantName"),
		emailData.get("orderData"), emailData.get("urlHelp"), emailData.get("contactEmail"),
		emailData.get("footerSlogan"));
	
	// cria a mensagem
	MessageNotificator notificacao = new MessageNotificator(emailData.get("userEmail"));
	notificacao.setAssunto("[" + emailData.get("subsidiaryName") + "] - Pedido nº " + emailData.get("orderNumber")
		+ " aguardando retirada!");
	notificacao.setConteudo(conteudo);
	
	return notificacao;
    } // fim do método prepareMensagem(String, Object...)
    
} // fim da classe NovoUsuarioNotificador
