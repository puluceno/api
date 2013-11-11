package br.com.redefood.util;

import br.com.redefood.model.Rating;

/**
 * Constantes da aplicação.
 * 
 * @author Thiago Vieira Puluceno <thiago@redefood.com.br>
 * 
 */
public class RedeFoodConstants {
    
    /**
     * Default Expiration time in miliseconds of a user token = 3 hours
     */
    public static final String DEFAULT_TOKEN_EXPIRATION_TIME = "10800000";
    
    /**
     * Default path to the e-mails images
     */
    public static final String REDEFOOD_PATH_IMAGES = "http://www.redefood.com.br/images/email/";
    
    /**
     * Default path to the restaurants images
     */
    public static final String RESTAURANT_LOGO_PATH = "http://www.redefood.com.br/images/cache/60x60/";
    
    /**
     * Default path to user uploaded images
     */
    public static final String DEFAULT_UPLOADED_FILE_PATH = "/var/www/images/";
    
    /**
     * Default RedeFood logo icon
     */
    public static final String REDEFOOD_LOGO_ICON = "email/icon-42x42.png";
    
    /**
     * Default Hashing Algorithm used to encrypt RedeFood's Data.
     */
    public static final String DEFAULT_HASHING_ALGORITHM = "SHA-256";
    
    /**
     * Default Client IP Address
     */
    public static final String DEFAULT_CLIENT_HOST = "127.0.0.1";
    
    /**
     * Default Brazilian date format.
     */
    public static final String BR_DATE_FORMAT = "dd/MM/yyyy";
    
    /**
     * Default Brazilian datetime format.
     */
    public static final String BR_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    
    /**
     * Default US date format.
     */
    public static final String US_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Default US datetime format.
     */
    public static final String US_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Default token URI parameter name
     */
    public static final String DEFAULT_TOKEN_IDENTIFICATOR = "token";
    
    /**
     * Default AND character for URLs
     */
    public static final String DEFAULT_AND_CHARACTER = "&";
    
    /**
     * Default Mail Address that System uses to send an Email
     */
    public static final String DEFAULT_FROM_EMAIL = "noreply@redefood.com.br";
    
    /**
     * Defaut Mail Address where contact emails should be delivered
     */
    public static final String DEFAULT_CONTACT_EMAIL = "contato@redefood.com.br";
    
    /**
     * Default Name set to the default mail address that System uses to send an
     * Email
     */
    public static final String DEFAULT_FROM_SENDER = "RedeFood";
    
    /**
     * Nome JNDI do serviço de e-mail.
     */
    public static final String JNDI_MAIL_SERVICE = "java:/RedeFoodMail";
    
    /**
     * Nome JNDI do connection factory do JMS.
     */
    public static final String JNDI_JMS_CONNECTION_FACTORY = "/ConnectionFactory";
    
    /**
     * Nome JNDI da fila de notificação.
     */
    public static final String JNDI_JMS_QUEUE_NOTIFICACAO = "java:jboss/exported/jms/queue/EmailsQueue";
    
    /**
     * Nome base do arquivo de properties de mensagens.
     */
    public static final String PROP_MESSAGES_BASENAME = "messages";
    
    /**
     * Wildcard SQL para uso em expressões com operador LIKE.
     */
    public static final String SQL_LIKE_WILDCARD = "%";
    
    /**
     * Tamanho padrão para senhas geradas pelo sistema.
     */
    public static final int DEFAULT_PASSWORD_SIZE = 6;
    
    /**
     * Charset das mensagens de e-mail.
     */
    public static final String MAIL_CHARSET = "utf-8";
    
    /**
     * Default url that the user could access our Demonstration Store.
     */
    public static final String DEFAULT_DEMO_URL = "http://www.demo.redefood.com.br/admin";
    
    /**
     * Default URL for admin login page.
     */
    public static final String DEFAULT_ADMIN_URL = "http://www.admin.redefood.com.br";
    
    /**
     * Default Redefood URL
     */
    public static final String DEFAULT_REDEFOOD_URL = "http://www.delivery.redefood.com.br";
    
    /**
     * Default login to access our Demonstration Store
     */
    public static final String DEFAULT_DEMO_LOGIN = "demo";
    
    /**
     * Default password to access our Demonstration Store
     */
    public static final String DEFAULT_DEMO_PASS = "demorf1234";
    
    /**
     * Default URL to a user perform a Feedback. Uses idOrder as path parameter.
     * Ex: .../rating/{x}
     */
    public static final String DEFAULT_RATING_URL = "http://www.delivery.redefood.com.br/rating/";
    
    /**
     * Default virtual store percentage to charge from restaurant customers
     */
    public static Double DEFAULT_VIRTUAL_STORE_PERCENTAGE = 0.25;
    
    /**
     * Default name for URL space code
     */
    public static final String URL_SPACE = "%20";
    
    /**
     * Default URL for (RedeFood square redir) resetting pass
     */
    public static final String REDEFOOD_RESET_PASS = "delivery.redefood.com.br/reset?redir=menu";
    
    /**
     * Default URL prefix
     */
    public static final String DEFAULT_URL_PREFIX = "http://www.";
    
    /**
     * Default url suffix for .com.br domains
     */
    public static final String DEFAULT_URL_SUFFIX = ".com.br";
    /**
     * Default RedeFood URL suffix
     */
    public static final String DEFAULT_REDEFOOD_URL_SUFFIX = ".redefood.com.br";
    
    /**
     * Default reset suffix
     */
    public static final String DEFAULT_RESET_SUFFIX = "/reset?redir=menu";
    
    /**
     * Default RedeFood label name
     */
    public static final String REDEFOOD_NAME = "RedeFood";
    
    /**
     * Default RedeFood facebook name
     */
    public static final String REDEFOOD_FACEBOOK = "redefoodbr";
    
    /**
     * Default RedeFood slogan
     */
    public static final String REDEFOOD_SLOGAN = "RedeFood, a sua Praça de Alimentação On Line!";
    
    /**
     * Default Help Suffix for URLs
     */
    public static final String DEFAULT_HELP_SUFFIX = "/help";
    
    /**
     * Default RedeFood support email
     */
    public static final String REDEFOOD_SUPPORT_EMAIL = "suporte@redefood.com.br";
    
    /**
     * Default Suffix for dynamic rating urls
     */
    public static final String DEFAULT_RATING_SUFFIX = "/rating/";
    
    /**
     * Default Suffix for dynamic rating ADMIN urls
     */
    public static final String DEFAULT_ADMIN_RATING_SUFFIX = "/admin/orders/rating";
    
    /**
     * Default email title when a subsidiary receives a {@link Rating}
     */
    public static final String COMMENT_TITLE = "Você recebeu um comentário para o pedido número ";
    
    /**
     * Default email title when a user receives a reply from a {@link Rating}
     */
    public static final String REPLY_TITLE = "Você recebeu uma resposta para seu comentário do pedido número ";
    
    /**
     * Default email title when a subsidiary receives a rejoinder from a
     * {@link Rating}
     */
    public static final String REJOINDER_TITLE = "Seu estabelecimento recebeu uma tréplica para o pedido número ";
    
    /**
     * Default url suffix for users to see their ratings
     */
    public static final String USER_RATING_URL_SUFFIX = "/account/orders";
    
    /**
     * Default message to admin when he receives a comment.
     */
    public static final String ADMIN_COMMENT_ANSWER_MSG = "Caso você queira, você pode realizar uma réplica ao comentário do cliente. Para isto, basta <a href=*>clicar aqui!</a>";
    
    /**
     * Default message to user when he receives a comment.
     */
    public static final String USER_REPLY_ANSWER_MSG = "Caso você queira, você pode entrar realizar uma tréplica à resposta do estabelecimento. Para isto, basta <a href=*>clicar aqui!</a>";
    
    /**
     * Default message to user when he receives a comment.
     */
    public static final String ADMIN_REJOINDER_ANSWER_MSG = "Veja suas avaliações <a href=*>clicando aqui!</a>";
    
} // fim da classe RedeFoodConstants
