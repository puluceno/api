package br.com.redefood.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class GMapService {

	private static final String GEOCODE_REQUEST_URL = "http://maps.googleapis.com/maps/api/geocode/xml?sensor=false&";
	private static HttpClient httpClient = new HttpClient(
			new MultiThreadedHttpConnectionManager());

	public static void main(String[] args) throws Exception {
	}

	public static Map<String, String> getGeocode(String address) {
		HashMap<String, String> coord = new HashMap<String, String>();
		try {
			StringBuilder urlBuilder = new StringBuilder(GEOCODE_REQUEST_URL);
			if (StringUtils.isNotBlank(address)) {
				urlBuilder.append("&address=").append(
						URLEncoder.encode(address, "UTF-8"));
			}

			final GetMethod getMethod = new GetMethod(urlBuilder.toString());
			try {
				httpClient.executeMethod(getMethod);
				Reader reader = new InputStreamReader(
						getMethod.getResponseBodyAsStream(),
						getMethod.getResponseCharSet());

				int data = reader.read();
				char[] buffer = new char[1024];
				Writer writer = new StringWriter();
				while ((data = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, data);
				}

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader("<"
						+ writer.toString().trim()));
				Document doc = db.parse(is);

				String strLatitude = getXpathValue(doc,
						"//GeocodeResponse/result/geometry/location/lat/text()");

				String strLongtitude = getXpathValue(doc,
						"//GeocodeResponse/result/geometry/location/lng/text()");

				coord.put("lat", strLatitude);
				coord.put("lng", strLongtitude);

			} finally {
				getMethod.releaseConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coord;

	}

	private static String getXpathValue(Document doc, String strXpath)
			throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile(strXpath);
		String resultData = null;
		Object result4 = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result4;
		for (int i = 0; i < nodes.getLength(); i++) {
			resultData = nodes.item(i).getNodeValue();
		}
		return resultData;
	}

}