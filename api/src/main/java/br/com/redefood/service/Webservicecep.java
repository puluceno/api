package br.com.redefood.service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Webservicecep {
    
    private String resultado;
    private String uf;
    private String cidade;
    private String bairro;
    private String tpLogradouro;
    private String logradouro;
    
    public String getResultado() {
	return resultado;
    }
    
    public void setResultado(String resultado) {
	this.resultado = resultado;
    }
    
    public String getUf() {
	return uf;
    }
    
    public void setUf(String uf) {
	this.uf = uf;
    }
    
    public String getCidade() {
	return cidade;
    }
    
    public void setCidade(String cidade) {
	this.cidade = cidade;
    }
    
    public String getBairro() {
	return bairro;
    }
    
    public void setBairro(String bairro) {
	this.bairro = bairro;
    }
    
    public String getTpLogradouro() {
	return tpLogradouro;
    }
    
    public void setTpLogradouro(String tpLogradouro) {
	this.tpLogradouro = tpLogradouro;
    }
    
    public String getLogradouro() {
	return logradouro;
    }
    
    public void setLogradouro(String logradouro) {
	this.logradouro = logradouro;
    }
}
