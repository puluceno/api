package br.com.redefood.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

public class SendSms {

	static public void main(String[] args) {
		try {
			Date begin = new Date();
			// Construct data
			String data = "";
			/*
			 * Note the suggested encoding for certain parameters, notably the
			 * username, password and especially the message. ISO-8859-1 is
			 * essentially the character set that we use for message bodies,
			 * with a few exceptions for e.g. Greek characters. For a full list,
			 * see:
			 * http://bulksms.vsms.net/docs/eapi/submission/character_encoding/
			 */
			/*data += "username=" + URLEncoder.encode("puluceno", "ISO-8859-1");
			data += "&password=" + URLEncoder.encode("123456", "ISO-8859-1");
			data += "&message=" + URLEncoder.encode("Seu código de acesso RedeFood é X6AU9", "ISO-8859-1");
			data += "&want_report=1";
			data += "&msisdn=554884220993";*/
			
			data += "fuse=" + URLEncoder.encode("send_msg", "UTF-8");
			data += "&id=" + URLEncoder.encode("80059", "UTF-8");
			data += "&from=" + URLEncoder.encode("123456", "UTF-8");
			data += "&msg=" + URLEncoder.encode("123456", "UTF-8");
			data += "&number=" + URLEncoder.encode("123456", "UTF-8");

				URL url = new URL("http://webapi.comtele.com.br/api/api_fuse_connection.php");
			
			// Send data
//			URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
			/*
			 * If your firewall blocks access to port 5567, you can fall back to
			 * port 80: URL url = new
			 * URL("http://bulksms.vsms.net/eapi/submission/send_sms/2/2.0");
			 * (See FAQ for more details.)
			 */

			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				// Print the response output...
				System.out.println(line);
			}
			wr.close();
			rd.close();
			Date end = new Date();
			System.out.println(end.getTime() - begin.getTime());
			System.out.println(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}