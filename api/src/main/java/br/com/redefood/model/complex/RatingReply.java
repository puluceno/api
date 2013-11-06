package br.com.redefood.model.complex;

import java.io.Serializable;

public class RatingReply implements Serializable {
    private static final long serialVersionUID = -7291570722983578296L;
    
    private String reply;
    private String rejoinder;
    
    public RatingReply() {
    }
    
    public RatingReply(String reply, String rejoinder) {
	this.reply = reply;
	this.rejoinder = rejoinder;
    }
    
    public String getReply() {
	return reply;
    }
    
    public void setReply(String reply) {
	this.reply = reply;
    }
    
    public String getRejoinder() {
	return rejoinder;
    }
    
    public void setRejoinder(String rejoinder) {
	this.rejoinder = rejoinder;
    }
}
