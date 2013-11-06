package br.com.redefood.mail.notificator.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Classe que representa uma mensagem de notificação.
 * 
 * Nesta versão, é exigido que a notificação possua pelo menos um destinatário.
 * O assunto e o conteúdo da notificação não foram considerados obrigatórios.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class MessageNotificator implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Lista de destinatários
     */
    private Collection<String> destinatarios = new ArrayList<String>();
    
    /**
     * Assunto da mensagem
     */
    private String assunto;
    
    /**
     * Conteúdo da mensagem
     */
    private String conteudo;
    
    /**
     * Construtor da classe.
     * 
     * Exige o fornecimento de um destinatário para a criação da instância.
     * Desta forma, nenhuma notificação é criada sem destinatário.
     * 
     * @param destinatario
     *            Destinatário da notificação.
     * @throws Exception
     *             Caso não seja fornecido um destinatário para a notificação.
     */
    public MessageNotificator(final String destinatario) throws Exception {
	if (destinatario == null || destinatario.isEmpty())
	    throw new Exception("Destinatário inválido");
	
	destinatarios.add(destinatario);
    } // fim do construtor NotificacaoMessage(String)
    
    // getters e setters
    public Collection<String> getDestinatarios() {
	return destinatarios;
    }
    
    /**
     * Setter de destinatários modificado para garantir que a notificação possua
     * pelo menos um destinatário.
     * 
     * @param destinatarios
     *            Lista de destinatários
     * @throws Exception
     *             Caso a lista de destinatários seja nula ou esteja vazia.
     */
    public void setDestinatarios(final Collection<String> destinatarios) throws Exception {
	if (destinatarios == null || destinatarios.isEmpty())
	    throw new Exception("Lista de destinatários inválida");
	
	this.destinatarios = destinatarios;
    }
    
    public String getAssunto() {
	return assunto;
    }
    
    public void setAssunto(final String assunto) {
	this.assunto = assunto;
    }
    
    public String getConteudo() {
	return conteudo;
    }
    
    public void setConteudo(final String conteudo) {
	this.conteudo = conteudo;
    } // fim dos getters e setters
    
    /**
     * Método sobrecarregado para retornar a representação textual específica
     * desta classe.
     * 
     * Gerado automaticamente pelo Eclipse (Source > Generate toString()...)
     * 
     * @return Representação textual.
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("NotificacaoMessage [destinatarios=");
	builder.append(destinatarios);
	builder.append(", assunto=");
	builder.append(assunto);
	builder.append(", conteudo=");
	builder.append(conteudo);
	builder.append("]");
	return builder.toString();
    } // fim do método toString()
    
} // fim da classe NotificacaoMessage
