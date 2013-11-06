package br.com.redefood.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

public class Url_sender {

	public static void main(String[] args) throws Exception {
		Date begin = new Date();
		URL yahoo = new URL(
				"http://webapi.comtele.com.br/api/api_fuse_connection.php?fuse=get_id&user=80059&pwd=zon58wil");
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

		// Construct data
		// String data = "";
		/*
		 * Note the suggested encoding for certain parameters, notably the
		 * username, password and especially the message. ISO-8859-1 is
		 * essentially the character set that we use for message bodies, with a
		 * few exceptions for e.g. Greek characters. For a full list, see:
		 * http://bulksms.vsms.net/docs/eapi/submission/character_encoding/
		 */
		/*
		 * data += "username=" + URLEncoder.encode("puluceno", "ISO-8859-1");
		 * data += "&password=" + URLEncoder.encode("123456", "ISO-8859-1");
		 * data += "&message=" +
		 * URLEncoder.encode("Seu código de acesso RedeFood é X6AU9",
		 * "ISO-8859-1"); data += "&want_report=1"; data +=
		 * "&msisdn=554884220993";
		 */
		//
		// data += "fuse=" + URLEncoder.encode("send_msg", "UTF-8");
		// data += "&id=" + URLEncoder.encode(ID.replace("=", ""), "UTF-8");
		// data += "&from=" + URLEncoder.encode("TestE", "UTF-8");
		// data += "&msg=" +
		// URLEncoder.encode("Seu Código de ativação é X6AUBH", "UTF-8");
		// data += "&number=" + URLEncoder.encode("4884220993", "UTF-8");
		//
		// URL url = new
		// URL("http://webapi.comtele.com.br/api/api_fuse_connection.php");

		String Origem = "80059";
		String Mensagem = "Seu+codigo+de+ativacao+e+X6AUBH";
		String Destino = "4884220993";
		URL url = new URL("http://webapi.comtele.com.br/api/api_fuse_connection.php?fuse=send_msg&id="
				+ ID.replace("=", "") + "&from=" + Origem + "&msg=" + Mensagem + "&number=" + Destino);

		try (BufferedReader i = new BufferedReader(new InputStreamReader(url.openStream()))) {
			String inputLine2;
			while ((inputLine2 = i.readLine()) != null) {
				System.out.println("****" + inputLine2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Send data
		// URL url = new
		// URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
		/*
		 * If your firewall blocks access to port 5567, you can fall back to
		 * port 80: URL url = new
		 * URL("http://bulksms.vsms.net/eapi/submission/send_sms/2/2.0" ); (See
		 * FAQ for more details.)
		 */

		/*
		 * URLConnection conn = url.openConnection(); conn.setDoOutput(true);
		 * OutputStreamWriter wr = new
		 * OutputStreamWriter(conn.getOutputStream()); wr.write(data);
		 * wr.flush();
		 * 
		 * // Get the response BufferedReader rd = new BufferedReader(new
		 * InputStreamReader(conn.getInputStream())); String line; while ((line
		 * = rd.readLine()) != null) { // Print the response output...
		 * System.out.println(line); } wr.close(); rd.close();
		 */
		Date end = new Date();
		System.out.println("Tempo total: " + (end.getTime() - begin.getTime()));
		System.out.println(new Date());
	}
}