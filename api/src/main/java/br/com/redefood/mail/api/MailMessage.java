package br.com.redefood.mail.api;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import br.com.redefood.mail.core.exceptions.InvalidAddressException;
import br.com.redefood.mail.core.exceptions.SendFailedException;

/**
 * Interface de uma mensagem de e-mail.
 * 
 * A interface deve ser utilizada preferencialmente em relação as implementações
 * da mesma diretamente. A criação de uma mensagem deve ser realizada através da
 * seguinte instrução:
 * {@code MailMessage mailMessage = Mail.newHtmlMessage("jndiName");}
 * 
 * Os métodos de atribuição de parâmetros retornam a própria instância da
 * mensagem com o objetivo de permitir o encadeamento das invocações.
 * {@code mailMessage.to("a@a.com").from("b@b.com").send()}
 * 
 * A implementação desta interface deve validar para que, no envio, a mensagem
 * possua ao menos um destinatário, seja ele direto, em cópia ou em cópia
 * oculta.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public interface MailMessage {
    
    /**
     * Adiciona um destinatário na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage to(String addr) throws InvalidAddressException;
    
    /**
     * Adiciona uma coleção de destinatários na mensagem.
     * 
     * @param addrs
     *            Endereços de e-mail dos destinatários.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso algum endereço fornecido não seja válido
     */
    MailMessage to(Collection<String> addrs) throws InvalidAddressException;
    
    /**
     * Adiciona um destinatário na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage to(InternetAddress addr) throws InvalidAddressException;
    
    /**
     * Adiciona um destinatário em cópia na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage cc(String addr) throws InvalidAddressException;
    
    /**
     * Adiciona uma coleção de destinatários em cópia na mensagem.
     * 
     * @param addrs
     *            Endereços de e-mail dos destinatários.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage cc(Collection<String> addrs) throws InvalidAddressException;
    
    /**
     * Adiciona um destinatário em cópia na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage cc(InternetAddress addr) throws InvalidAddressException;
    
    /**
     * Adiciona um destinatário em cópia oculta na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage bcc(String addr) throws InvalidAddressException;
    
    /**
     * Adiciona uma coleção de destinatários em cópia oculta na mensagem.
     * 
     * @param addr
     *            Endereços de e-mail dos destinatários.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage bcc(Collection<String> addrs) throws InvalidAddressException;
    
    /**
     * Adiciona um destinatário em cópia oculta na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage bcc(InternetAddress addr) throws InvalidAddressException;
    
    /**
     * Define o endereço do remetente da mensagem.
     * 
     * Caso um remetente não seja passado para a mensagem, é utilizado o
     * parâmetro "mail.from" configurado no serviço de e-mail.
     * 
     * Invocações sucessivas deste método sobreescrevem as invocações
     * anteriores, ou seja, o último remetente informado será utilizado no envio
     * da mensagem.
     * 
     * @param addr
     *            Endereço de e-mail do remetente.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     */
    MailMessage from(String addr) throws InvalidAddressException;
    
    /**
     * Define o assunto da mensagem.
     * 
     * @param subject
     *            Assunto da mensagem.
     * @return Instância da própria mensagem
     */
    MailMessage subject(String subject);
    
    /**
     * Define o charset do assunto da mensagem.
     * 
     * @param charset
     *            Charset do assunto da mensagem.
     * @return Instância da própria mensagem.
     */
    MailMessage subjectCharset(String charset);
    
    /**
     * Define o corpo da mensagem.
     * 
     * @param body
     *            Corpo da mensagem.
     * @return Instância da própria mensagem
     */
    MailMessage body(String body);
    
    /**
     * Define o charset do corpo da mensagem.
     * 
     * @param charset
     *            Charset do corpo da mensagem.
     * @return Instância da própria mensagem.
     */
    MailMessage bodyCharset(String charset);
    
    /**
     * Realiza o envio da mensagem através do serviço de e-mail.
     * 
     * Esta etapa deve validar para que a mensagem possua ao menos um
     * destinatário, seja ele direto, em cópia ou em cópia oculta. Os demais
     * parâmetros devem ser opcionais, sendo que se o remetente não for
     * informado, deve considerar o parâmetro "mail.from" configurado no serviço
     * de e-mail.
     * 
     * @throws SendFailedException
     *             Caso ocorra alguma falha no envio do e-mail.
     */
    void send() throws SendFailedException;
} // fim da interface MailMessage
