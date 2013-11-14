package br.com.redefood.model.complex;

import java.io.Serializable;

public class GoogleUserDTO implements Serializable {
    private static final long serialVersionUID = -5013274370270137784L;
    
    private String id;
    private String email;
    private String verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String link;
    private String picture;
    private String gender;
    private String locale;
    
    public GoogleUserDTO(String id, String email, String verified_email, String name, String given_name,
	    String family_name, String link, String picture, String gender, String locale) {
	this.id = id;
	this.email = email;
	this.verified_email = verified_email;
	this.name = name;
	this.given_name = given_name;
	this.family_name = family_name;
	this.link = link;
	this.picture = picture;
	this.gender = gender;
	this.locale = locale;
    }
    
    public GoogleUserDTO() {
    }
    
    public String getId() {
	return id;
    }
    
    public void setId(String id) {
	this.id = id;
    }
    
    public String getEmail() {
	return email;
    }
    
    public void setEmail(String email) {
	this.email = email;
    }
    
    public String getVerified_email() {
	return verified_email;
    }
    
    public void setVerified_email(String verified_email) {
	this.verified_email = verified_email;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public String getGiven_name() {
	return given_name;
    }
    
    public void setGiven_name(String given_name) {
	this.given_name = given_name;
    }
    
    public String getFamily_name() {
	return family_name;
    }
    
    public void setFamily_name(String family_name) {
	this.family_name = family_name;
    }
    
    public String getLink() {
	return link;
    }
    
    public void setLink(String link) {
	this.link = link;
    }
    
    public String getPicture() {
	return picture;
    }
    
    public void setPicture(String picture) {
	this.picture = picture;
    }
    
    public String getGender() {
	return gender;
    }
    
    public void setGender(String gender) {
	this.gender = gender;
    }
    
    public String getLocale() {
	return locale;
    }
    
    public void setLocale(String locale) {
	this.locale = locale;
    }
    
}
