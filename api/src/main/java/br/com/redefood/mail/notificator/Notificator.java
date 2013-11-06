package br.com.redefood.mail.notificator;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import br.com.redefood.mail.notificator.core.MessageNotificator;

/**
 * Abstração de um notificador do sistema.
 * 
 * O objetivo desta classe é estabelecer uma interface mínima para o envio de
 * notificações assíncronas do sistema.
 * 
 * Além da implementação dos métodos abstratos desta classe, é fundamental que,
 * antes da invocação do método {@link #prepareMessage(String, Object...)}, seja
 * atribuído uma valor válido para o atributo template através do método
 * {@link #setTemplate(String)}.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public abstract class Notificator {
    
    /**
     * Nome do parâmetro com o caminho para as imagens utilizadas no template
     * das mensagens de notificação.
     */
    public static final String PARAM_DOMINIO_IMAGENS_EMAIL = "DOMINIO_IMAGENS_EMAIL";
    
    /**
     * Caminho relativo para o template da mensagem
     */
    private String template;
    
    // getters e setters
    public String getTemplate() {
	return template;
    }
    
    public void setTemplate(final String template) {
	this.template = template;
    } // fim dos getters e setters
    
    /**
     * Envia uma notificacão.
     * 
     * Cada implementação deste método ou do método
     * {@link #prepareMessage(String, Object...)} é responsável por definir os
     * parâmetros necessários para a preparação da mensagem.
     * 
     * Por definição, as implementações deste método deverão utilizar o JMS para
     * o envio das mensagem para a fila de notificações do sistema, evitando
     * assim que o usuário do sistema fique aguardando o retorno do
     * processamento da notificação para poder realizar outras ações.
     * 
     * @param emailData
     *            Parâmetros para preparação da mensagem.
     * @throws NotificacaoException
     *             Caso ocorra alguma falha no envio da notificação.
     */
    public abstract void send(HashMap<String,String> emailData) throws Exception;
    
    /**
     * Cria uma mensagem.
     * 
     * Este método implementa o design pattern Template Method para delegar
     * parte do algorítmo de criação de mensagens para as subclasses de cada
     * tipo de notificador.
     * 
     * Basicamente, a implementação deste método apenas carrega o template da
     * mensagem e invoca sua preparação.
     * 
     * @param emailData
     *            Parâmetros para preparação da mensagem.
     * @return Mensagem de notificação.
     * @throws Exception
     *             Caso ocorra alguma falha no carregamento do template.
     */
    protected MessageNotificator buildMessage(final HashMap<String,String> emailData) throws Exception {
	String template = null;
	try {
	    // carrega o template da mensagem
	    template = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
		    getTemplate()));
	} catch (IOException e) {
	    throw new Exception(e);
	}
	
	// invoca a implementação de preparação da mensagem
	return prepareMessage(template, emailData);
    } // fim do método crieMensagem(HashMap<String,String> emailData)
    
    /**
     * Prepara uma mensagem.
     * 
     * Cada implementação deste método ou do método {@link #enviar(Object...)} é
     * responsável por definir os parâmetros necessários para a preparação da
     * mensagem.
     * 
     * Este método é invocado pelo método {@link #buildMessage(Object...)} e
     * deve ser implementado por cada subclasse de notificador afim de produzir
     * mensagens específicas para cada tipo de notificação.
     * 
     * @param template
     *            Template da mensagem.
     * @param emailData
     *            Parâmetros para preparação da mensagem.
     * @return Mensagem de notificação.
     * @throws NotificacaoException
     *             Caso ocorra alguma falha na preparação da mensagem.
     */
    protected abstract MessageNotificator prepareMessage(String template, HashMap<String,String> emailData)
	    throws Exception;
    
} // fim da classe Notificador
