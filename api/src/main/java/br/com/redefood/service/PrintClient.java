package br.com.redefood.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import javax.naming.NoInitialContextException;

import br.com.redefood.model.Printer;

public class PrintClient {
	private static Socket requestSocket;
	private static ObjectOutputStream out;
	private static ObjectInputStream in;
	private static String message;

	public static void sendToPrinter(String clientIP, Printer printer, List<String> printerData, String locale)
			throws Exception {

		try {

			if (printer == null)
				throw new NoInitialContextException();

			// 1. creating a socket to connect to the server
			SocketAddress sockaddr = new InetSocketAddress(clientIP, 43520);
			requestSocket = new Socket();
			requestSocket.connect(sockaddr, 3000);
			System.out.println("Connected to " + clientIP + " in port 43520");

			// 2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			// 3: Communicating with the server

			do {
				try {
					sendMessage(printer.getIp());
					message = (String) in.readObject();
					if (message.contentEquals("connected")) {
						System.out.println("received depois de mandar ip: " + message);
					}
				} catch (Exception e) {
					System.err.println("data received in unknown format");
					throw e;
				}
			} while (!message.contentEquals("connected"));

			// 3.1: Once it is connected, send data to the printer
			try {
				for (String bs : printerData) {
					sendMessage(bs);
				}
				sendMessage("done");
			} catch (Exception e) {
				System.err.println("data received in unknown format");
				throw e;
			}

		} catch (Exception exception) {
			throw new Exception("failed to print");
		} finally {

			// 4: Closing connection
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (requestSocket != null)
					requestSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}

	}

	private static void sendMessage(Object msg) throws Exception {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
