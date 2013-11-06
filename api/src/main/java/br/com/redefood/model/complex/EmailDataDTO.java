package br.com.redefood.model.complex;

import java.io.Serializable;
import java.util.HashMap;

public class EmailDataDTO<K, V> extends HashMap<K, V> implements Serializable {
    private static final long serialVersionUID = 295934166224140970L;
    
    protected V defaultValue;
    
    public EmailDataDTO(V defaultValue) {
	this.defaultValue = defaultValue;
    }
    
    public EmailDataDTO(){
    }
    
    @Override
    public V get(Object k) {
	V v = super.get(k);
	return v == null && !containsKey(k) ? this.defaultValue : v;
    }
}
