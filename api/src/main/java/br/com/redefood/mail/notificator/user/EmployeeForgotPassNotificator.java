package br.com.redefood.mail.notificator.user;

import java.text.MessageFormat;
import java.util.HashMap;

import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.core.MessageNotificator;
import br.com.redefood.mail.notificator.core.NotificatorSender;
import br.com.redefood.model.User;

/**
 * Representação de um notificador para recuperação de senhas.
 * 
 * O objetivo desta classe é enviar uma notificação para um usuário com sua nova
 * senha de acesso.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class EmployeeForgotPassNotificator extends Notificator {
    
    /**
     * Construtor da classe.
     * 
     * Define o template desta mensagem de notificação.
     */
    public EmployeeForgotPassNotificator() {
	setTemplate("mailtemplates/employeeresetpass.mailtemplate");
    } // fim do construtor padrão
    
    /**
     * Envia uma notificação.
     * 
     * A implementação consiste em criar uma mensagem de notificação e solicitar
     * seu envio para a fila de notificações do sistema, retornando o controle
     * da ação para o usuário.
     * 
     * @param objects
     *            Parâmetros para criação da mensagem.
     * @see Notificator#enviar(HashMap<String, String>)
     */
    @Override
    public void send(final HashMap<String, String> emailData) throws Exception {
	NotificatorSender.send(buildMessage(emailData));
    } // fim do método enviar(HashMap<String, String>)
    
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
     * @param objects
     *            Parâmetros para criação da mensagem.
     * @see Notificator#prepareMessage(String, HashMap<String, String>)
     */
    @Override
    protected MessageNotificator prepareMessage(final String template, final HashMap<String, String> emailData)
	    throws Exception {
	// passa o template pelo formatador
	String conteudo = MessageFormat.format(template, emailData.get("subsidiaryUrl"), emailData.get("logo"),
		emailData.get("subsidiaryName"), emailData.get("facebook"), emailData.get("userName"),
		emailData.get("urlPass"), emailData.get("urlHelp"), emailData.get("contactEmail"),
		emailData.get("footerSlogan"));
	
	// cria a mensagem
	MessageNotificator notificacao = new MessageNotificator(emailData.get("addressee"));
	notificacao.setAssunto("[" + emailData.get("subsidiaryName") + "] - Recuperar senha");
	notificacao.setConteudo(conteudo);
	
	return notificacao;
    } // fim do método prepareMensagem(String, HashMap<String, String>)
} // fim da classe EsqueciSenhaNotificador
