package br.com.redefood.mail.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.redefood.mail.api.MailMessage;
import br.com.redefood.mail.core.exceptions.InvalidAddressException;
import br.com.redefood.mail.core.exceptions.MailException;
import br.com.redefood.mail.core.exceptions.SendFailedException;

/**
 * Classe que implementa uma mensagem HTML.
 * 
 * A versão atual desta implementação permite o envio de mensagens simples, com
 * o uso de tags HTML. Referências a imagens internas a mensagem ainda não são
 * suportadas.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class HtmlMessage implements MailMessage {
    
    /**
     * Sessão do serviço de e-mail.
     */
    private Session session;
    /**
     * Coleção de endereços de e-mail dos destinatários.
     */
    private Collection<InternetAddress> to;
    /**
     * Coleção de endereços de e-mail dos destinatários em cópia.
     */
    private Collection<InternetAddress> cc;
    /**
     * Coleção de endereços de e-mail dos destinatários em cópia oculta.
     */
    private Collection<InternetAddress> bcc;
    /**
     * Endereço de e-mail do remetente.
     */
    private InternetAddress from;
    /**
     * Assunto.
     */
    private String subject;
    /**
     * Charset do assunto.
     */
    private String subjectCharset;
    /**
     * Corpo da mensagem.
     */
    private String body;
    /**
     * Charset do corpo da mensagem.
     */
    private String bodyCharset;
    
    /**
     * Construtor da classe.
     * 
     * Exige o fornecimento de um nome JNDI de serviço de e-mail válido já na
     * construção da classe, evitando que a ausência deste parâmetro seja
     * percebida apenas no momento do envio da mensagem.
     * 
     * @param jndiName
     *            Nome JNDI do serviço de e-mail.
     * @throws MailException
     *             Caso ocorra alguma falha na criação da mensagem.
     */
    public HtmlMessage(final String jndiName) throws MailException {
	if (jndiName == null || jndiName.isEmpty())
	    throw new MailException("JNDI Name invalido.");
	
	try {
	    // localiza a sessão do serviço de e-mail através do nome JNDI
	    InitialContext ic = new InitialContext();
	    session = (Session) ic.lookup(jndiName);
	} catch (NamingException e) {
	    throw new MailException(e);
	}
	
	// inicializa as listas de endereço
	to = new ArrayList<InternetAddress>();
	cc = new ArrayList<InternetAddress>();
	bcc = new ArrayList<InternetAddress>();
    } // fim do construtor HtmlMessage(String)
    
    /**
     * Adiciona um destinatário na mensagem.
     * 
     * Utiliza o construtor {@link InternetAddress#InternetAddress(String)} para
     * realizar o parse do endereço de e-mail passado.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage to(final String addr) throws InvalidAddressException {
	InternetAddress ia = parseAddress(addr);
	to.add(ia);
	
	return this;
    } // fim do método to(String)
    
    /**
     * Adiciona uma coleção de destinatários na mensagem.
     * 
     * A implementação deste método consiste em invocar o método
     * {@link HtmlMessage#to(String)} para cada endereço de e-mail da coleção.
     * 
     * Caso algum endereço da coleção não seja válido, uma exceção é lançada e o
     * restante da coleção não é processada. Os itens já incluídos na mensagem,
     * no entanto, não são removidos.
     * 
     * @param addrs
     *            Endereços de e-mail dos destinatários.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage to(final Collection<String> addrs) throws InvalidAddressException {
	// valida para que a coleção não seja nula e não esteja vazia
	if (addrs == null || addrs.isEmpty())
	    throw new InvalidAddressException("Endereco de e-mail invalido");
	
	// invoca o método to(String) para cada endereço da coleção
	Iterator<String> i = addrs.iterator();
	while (i.hasNext()) {
	    to(i.next());
	}
	
	return this;
    } // fim do método to(Collection<String>)
    
    /**
     * Adiciona um destinatário na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage to(final InternetAddress addr) throws InvalidAddressException {
	// valida para que o endereço passado não seja nulo ou vazio
	if (addr == null || addr.getAddress() == null || addr.getAddress().isEmpty())
	    throw new InvalidAddressException("Endereco de e-mail invalido");
	
	to.add(addr);
	
	return this;
    } // fim do método to(InternetAddress)
    
    /**
     * Adiciona um destinatário em cópia na mensagem.
     * 
     * Utiliza o construtor {@link InternetAddress#InternetAddress(String)} para
     * realizar o parse do endereço de e-mail passado.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage cc(final String addr) throws InvalidAddressException {
	InternetAddress ia = parseAddress(addr);
	cc.add(ia);
	
	return this;
    } // fim do método cc(String)
    
    /**
     * Adiciona uma coleção de destinatários em cópia na mensagem.
     * 
     * A implementação deste método consiste em invocar o método
     * {@link HtmlMessage#cc(String)} para cada endereço de e-mail da coleção.
     * 
     * Caso algum endereço da coleção não seja válido, uma exceção é lançada e o
     * restante da coleção não é processada. Os itens já incluídos na mensagem,
     * no entanto, não são removidos.
     * 
     * @param addrs
     *            Endereços de e-mail dos destinatários.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage cc(final Collection<String> addrs) throws InvalidAddressException {
	// valida para que a coleção não seja nula e não esteja vazia
	if (addrs == null || addrs.isEmpty())
	    throw new InvalidAddressException("Endereco de e-mail invalido");
	
	// invoca o método cc(String) para cada endereço da coleção
	Iterator<String> i = addrs.iterator();
	while (i.hasNext()) {
	    cc(i.next());
	}
	
	return this;
    } // fim do método cc(Collection<String)
    
    /**
     * Adiciona um destinatário em cópia na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage cc(final InternetAddress addr) throws InvalidAddressException {
	// valida para que o endereço passado não seja nulo ou vazio
	if (addr == null || addr.getAddress() == null || addr.getAddress().isEmpty())
	    throw new InvalidAddressException("Endereco de e-mail invalido");
	
	cc.add(addr);
	
	return this;
    } // fim do método cc(InternetAddress)
    
    /**
     * Adiciona um destinatário em cópia oculta na mensagem.
     * 
     * Utiliza o construtor {@link InternetAddress#InternetAddress(String)} para
     * realizar o parse do endereço de e-mail passado.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage bcc(final String addr) throws InvalidAddressException {
	InternetAddress ia = parseAddress(addr);
	bcc.add(ia);
	
	return this;
    } // fim do método bcc(String)
    
    /**
     * Adiciona uma coleção de destinatários em cópia oculta na mensagem.
     * 
     * A implementação deste método consiste em invocar o método
     * {@link HtmlMessage#bcc(String)} para cada endereço de e-mail da coleção.
     * 
     * Caso algum endereço da coleção não seja válido, uma exceção é lançada e o
     * restante da coleção não é processada. Os itens já incluídos na mensagem,
     * no entanto, não são removidos.
     * 
     * @param addrs
     *            Endereços de e-mail dos destinatários.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage bcc(final Collection<String> addrs) throws InvalidAddressException {
	// valida para que a coleção não seja nula e não esteja vazia
	if (addrs == null || addrs.isEmpty())
	    throw new InvalidAddressException("Endereco de e-mail invalido");
	
	// invoca o método bcc(String) para cada endereço da coleção
	Iterator<String> i = addrs.iterator();
	while (i.hasNext()) {
	    bcc(i.next());
	}
	
	return this;
    } // fim do método bcc(Collection<String>)
    
    /**
     * Adiciona um destinatário em cópia oculta na mensagem.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage bcc(final InternetAddress addr) throws InvalidAddressException {
	// valida para que o endereço passado não seja nulo ou vazio
	if (addr == null || addr.getAddress() == null || addr.getAddress().isEmpty())
	    throw new InvalidAddressException("Endereco de e-mail invalido");
	
	bcc.add(addr);
	
	return this;
    } // fim do método bcc(InternetAddress)
    
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
     * Utiliza o construtor {@link InternetAddress#InternetAddress(String)} para
     * realizar o parse do endereço de e-mail passado.
     * 
     * @param addr
     *            Endereço de e-mail do remetente.
     * @return Instância da própria mensagem
     * @throws InvalidAddressException
     *             Caso o endereço fornecido não seja válido
     * @see InternetAddress
     */
    @Override
    public MailMessage from(final String addr) throws InvalidAddressException {
	InternetAddress ia = parseAddress(addr);
	from = ia;
	
	return this;
    } // fim do método from(String)
    
    /**
     * Define o assunto da mensagem.
     * 
     * O assunto não deve ser obrigatório para o envio da mensagem.
     * 
     * @param subject
     *            Assunto da mensagem.
     * @return Instância da própria mensagem
     */
    @Override
    public MailMessage subject(final String subject) {
	this.subject = subject;
	
	return this;
    } // fim do método subject(String)
    
    /**
     * Define o charset do assunto da mensagem.
     * 
     * @param charset
     *            Charset do assunto da mensagem.
     * @return Instância da própria mensagem.
     */
    @Override
    public MailMessage subjectCharset(final String charset) {
	subjectCharset = charset;
	
	return this;
    } // fim do método subjectCharset(String)
    
    /**
     * Define o corpo da mensagem.
     * 
     * @param body
     *            Corpo da mensagem.
     * @return Instância da própria mensagem
     */
    @Override
    public MailMessage body(final String body) {
	this.body = body;
	
	return this;
    } // fim do método body(String)
    
    /**
     * Define o charset do corpo da mensagem.
     * 
     * @param charset
     *            Charset do corpo da mensagem.
     * @return Instância da própria mensagem.
     */
    @Override
    public MailMessage bodyCharset(final String charset) {
	bodyCharset = charset;
	
	return this;
    } // fim do método bodyCharset(String)
    
    /**
     * Realiza o envio da mensagem através do serviço de e-mail.
     * 
     * Esta etapa valida para que a mensagem possua ao menos um destinatário,
     * seja ele direto, em cópia ou em cópia oculta. Os demais parâmetros são
     * opcionais, sendo que se o remetente não for informado, considera o
     * parâmetro "mail.from" configurado no serviço de e-mail.
     * 
     * @throws SendFailedException
     *             Caso ocorra alguma falha no envio do e-mail.
     */
    @Override
    public void send() throws SendFailedException {
	// se a mensagem não for válida, lança SendFailedException
	validate();
	
	// prepare uma mensagem para envio
	Message message = prepareMessage();
	
	try {
	    // realiza o envio da mensagem
	    Transport.send(message);
	} catch (MessagingException e) {
	    throw new SendFailedException(e);
	}
    } // fim do método send()
    
    /**
     * Realiza a validação da mensagem.
     * 
     * Garante que a mensagem possui ao menos um destinatário para envio,
     * podendo ele ser direto, em cópia ou em cópia oculta.
     * 
     * @throws SendFailedException
     *             Caso a mensagem seja inválida.
     */
    private void validate() throws SendFailedException {
	if (to.isEmpty() && cc.isEmpty() && bcc.isEmpty())
	    throw new SendFailedException("Mensagem sem destinatario");
    } // fim do método validate()
    
    /**
     * Prepara a mensagem para envio.
     * 
     * Cria uma instância da classe {@link Message} da API JavaMail, definindo
     * os parâmetros necessários para o envio de uma mensagem HTML.
     * 
     * @return Mensagem pronta para o envio.
     * @throws SendFailedException
     *             Caso ocorra alguma falha na preparação da mensagem.
     * @see Message
     */
    private Message prepareMessage() throws SendFailedException {
	MimeMessage message = new MimeMessage(session);
	
	try {
	    // insere os destinatários da mensagem
	    Iterator<InternetAddress> iTo = to.iterator();
	    while (iTo.hasNext()) {
		message.addRecipient(Message.RecipientType.TO, iTo.next());
	    }
	    
	    // insere os destinatários em cópia da mensagem
	    Iterator<InternetAddress> iCc = cc.iterator();
	    while (iCc.hasNext()) {
		message.addRecipient(Message.RecipientType.CC, iCc.next());
	    }
	    
	    // insere os destinatários em cópia oculta
	    Iterator<InternetAddress> iBcc = bcc.iterator();
	    while (iBcc.hasNext()) {
		message.addRecipient(Message.RecipientType.BCC, iBcc.next());
	    }
	    
	    if (from != null) {
		// se houver um remetente, insere na mensagem
		message.setFrom(from);
	    } else {
		// caso contrário, utiliza o parâmetro "mail.from" configurado
		// no serviço de e-mail
		message.setFrom();
	    }
	    
	    // insere o assunto
	    if (subjectCharset != null) {
		message.setSubject(subject, subjectCharset);
	    } else {
		message.setSubject(subject);
	    }
	    
	    // insere o corpo da mensagem do e-mail, definindo o tipo MIME para
	    // HTML
	    String mimeType = "text/html";
	    if (bodyCharset != null) {
		mimeType += "; charset=\"" + bodyCharset + "\"";
	    }
	    message.setContent(body, mimeType);
	} catch (MessagingException e) {
	    throw new SendFailedException(e);
	}
	
	return message;
    } // fim do método prepareMessagem()
    
    /**
     * Realiza o parse de um endereço de e-mail.
     * 
     * O parse do endereço é realizado pelo construtor
     * {@link InternetAddress#InternetAddress(String)}.
     * 
     * @param addr
     *            Endereço do e-mail do destinatário.
     * @return Instância de InternetAddress
     * @throws InvalidAddressException
     *             Caso ocorra falha no parse do endereço de e-mail.
     * @see InternetAddress
     */
    private InternetAddress parseAddress(final String addr) throws InvalidAddressException {
	InternetAddress ia;
	try {
	    ia = new InternetAddress(addr);
	} catch (AddressException e) {
	    throw new InvalidAddressException(e);
	}
	
	return ia;
    } // fim do método parseAddress(String)
} // fim da classe HtmlMessage
