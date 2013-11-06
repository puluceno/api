package br.com.redefood.provider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomJsonDateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dateString = dateFormat.format(value);
		jgen.writeString(dateString);

	}
}