package br.com.redefood.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

public class SmsGrecia {

	public static void main(String[] args) throws Exception {
		Date begin = new Date();
		URL yahoo = new URL(
				"http://www.login.sms-mass.com/bulksms/submitsms.go?username=puluceno@gmail.com&password=123456&originator=RedeFood&phone=554884220993&msgtext=Seu+código+de+ativação+é+x6ua86&charset=1");
		String ID = "";
		try (BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				ID = inputLine;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-----------" + ID);
		Date end = new Date();
		System.out.println("Tempo total: " + (end.getTime() - begin.getTime()));
		System.out.println(new Date());
	}
}