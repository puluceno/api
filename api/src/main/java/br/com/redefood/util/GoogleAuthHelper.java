package br.com.redefood.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;

import br.com.redefood.model.complex.GoogleUserDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * A helper class for Google's OAuth2 authentication API.
 * 
 * @version 20130224
 * @author pulu
 */
public final class GoogleAuthHelper {
    
    /**
     * Please provide a value for the CLIENT_ID constant before proceeding, set
     * this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_ID = "125987358336.apps.googleusercontent.com";
    /**
     * Please provide a value for the CLIENT_SECRET constant before proceeding,
     * set this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_SECRET = "E1qygrF6UH-nskwn93XCTpYh";
    
    // start google authentication constants
    private static final Iterable<String> SCOPE = Arrays
	    .asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email"
		    .split(";"));
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    // end google authentication constants
    
    private String stateToken;
    
    private final GoogleAuthorizationCodeFlow flow;
    
    /**
     * Constructor initializes the Google Authorization Code Flow with CLIENT
     * ID, SECRET, and SCOPE
     */
    public GoogleAuthHelper() {
	flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET,
		(Collection<String>) SCOPE).build();
	generateStateToken();
    }
    
    /**
     * Builds a login URL based on client ID, secret, callback URI, and scope
     * 
     * @param redirectUrl
     *            Callback URI that google will redirect to after successful
     *            authentication
     * @return login URL based on client ID, secret, callback URI, and scope
     */
    public String buildLoginUrl(String redirectUrl) {
	final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
	return url.setRedirectUri(redirectUrl).setState(stateToken).build();
    }
    
    /**
     * Generates a secure state token
     */
    private void generateStateToken() {
	SecureRandom sr1 = new SecureRandom();
	stateToken = "google;" + sr1.nextInt();
    }
    
    /**
     * Accessor for state token
     */
    public String getStateToken() {
	return stateToken;
    }
    
    /**
     * Setter for state token, used for maintaining user session
     * 
     * @param stateToken
     */
    public void setStateToken(String stateToken) {
	this.stateToken = stateToken;
    }
    
    /**
     * Expects an Authentication Code, and makes an authenticated request for
     * the user's profile information
     * 
     * @return JSON formatted user profile information
     * @param authCode
     *            authentication code provided by google
     */
    public GoogleUserDTO getUserInfoJson(final String authCode, String redirectUrl) throws IOException {
	
	final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(redirectUrl).execute();
	final Credential credential = flow.createAndStoreCredential(response, null);
	final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
	// Make an authenticated request
	final GenericUrl url = new GenericUrl(USER_INFO_URL);
	final HttpRequest request = requestFactory.buildGetRequest(url);
	request.getHeaders().setContentType("application/json");
	String answer = request.execute().parseAsString();
	ObjectMapper mapper = new ObjectMapper();
	return mapper.readValue(answer, GoogleUserDTO.class);
	
    }
}