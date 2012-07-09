package com.stubhub.queryhandlers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.stubhub.Constants;
import com.stubhub.entities.DeliveryOption;
import com.stubhub.entities.EventInfo;
import com.stubhub.entities.ListingResponse;

import android.util.Log;

public class SellingHandler {

	public static String getAllSessionCookie(String username, String password) {
		HttpURLConnection con = null;

		try {
			URL url = new URL(Constants.MYACCOUNT_REST_SERVER
					+ "/myaccountapi/myaccount/user/userGuid/session");

			con = (HttpURLConnection) url.openConnection();
			System.out.println("start");
			con.setRequestMethod("GET");
			con.setRequestProperty("accept", "application/xml");
			con.setRequestProperty("username", username);
			con.setRequestProperty("password", password);

			con.setReadTimeout(Constants.SELLER_PRO_READ_TIMEOUT);
			con.setConnectTimeout(Constants.SELLER_PRO_CONNECT_TIMEOUT);
			con.setDoInput(true);

			Map<String, List<String>> headers = con.getHeaderFields();
			List<String> values = (List<String>) headers.get("set-cookie");
			String s = "";
			for (int i = 0; i < values.size() - 1; i++) {
				s = s + values.get(i) + "; ";
			}
			s = s + values.get(values.size() - 1);
			return s;// should not reach here
		} catch (Exception e) {
			Log.e(Constants.TAG,
					"Error getting session cookie: "
							+ Log.getStackTraceString(e));
			return "";
		}
	}

	public static String getSessionCookie(String username, String password) {
		HttpURLConnection con = null;

		try {
			URL url = new URL(Constants.MYACCOUNT_REST_SERVER
					+ "/myaccountapi/myaccount/user/userGuid/session");

			con = (HttpURLConnection) url.openConnection();
			System.out.println("start");
			con.setRequestMethod("GET");
			con.setRequestProperty("accept", "application/xml");
			con.setRequestProperty("username", username);
			con.setRequestProperty("password", password);

			con.setReadTimeout(Constants.SELLER_PRO_READ_TIMEOUT);
			con.setConnectTimeout(Constants.SELLER_PRO_CONNECT_TIMEOUT);
			con.setDoInput(true);

			Map<String, List<String>> headers = con.getHeaderFields();
			List<String> values = (List<String>) headers.get("set-cookie");
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i).startsWith(
						Constants.SELLER_PRO_SESSION_COOKIE)) {
					StringTokenizer st = new StringTokenizer(values.get(i), ";");
					return st.nextToken();
				}
			}
			return "";// should not reach here
		} catch (Exception e) {
			Log.e(Constants.TAG,
					"Error getting session cookie: "
							+ Log.getStackTraceString(e));
			return "";
		}
	}

	public static ListingResponse attachBarcodeToListing(String sellerGuid,
			String sessionCookie, String listingId, List<String> barcodes) {
		HttpURLConnection con = null;
		ListingResponse response = new ListingResponse();

		try {
			URL url = new URL(Constants.SELLER_PRO_SERVICE_ROOT
					+ "/api/fulfillment/seller/" + sellerGuid + "/listing/"
					+ listingId + "/barcodes");

			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("accept", "application/xml");
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Cookie", sessionCookie);

			con.setReadTimeout(Constants.SELLER_PRO_READ_TIMEOUT);
			con.setConnectTimeout(Constants.SELLER_PRO_CONNECT_TIMEOUT);
			// String body = "<listingRequest>";
			StringBuffer sb = new StringBuffer("<addBarcodeRequest>");

			for (int i = 0; i < barcodes.size(); i++) {
				sb.append("<barcodeSeat><barcode>" + (String) barcodes.get(i)
						+ "</barcode></barcodeSeat>");
			}

			sb.append("</addBarcodeRequest>");
			con.setDoInput(true);
			con.setDoOutput(true);

			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes(sb.toString());
			out.flush();
			out.close();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(new InputSource(con.getInputStream()));
			doc.getDocumentElement().normalize();

			NodeList errorCodeList = doc.getElementsByTagName("errorCode");
			if (errorCodeList == null || errorCodeList.getLength() == 0) {
				response.setSuccessFlag(true);
				response.setListingId(getTextContent((Element) doc
						.getElementsByTagName("listingId").item(0)));
			} else {
				response.setSuccessFlag(false);
				response.setErrorCode(getTextContent((Element) errorCodeList
						.item(0)));
				response.setErrorType(getTextContent((Element) doc
						.getElementsByTagName("errorType").item(0)));
			}
			return response;

		} catch (Exception e) {
			Log.e(Constants.TAG, Log.getStackTraceString(e));
			return null;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	public static ListingResponse addListing(String sellerId,
			String sessionCookie, String eventId, String quantity,
			String section, String row, String seats, String faceValue,
			String ticketPrice, String currency, String splits,
			String inHandDate, String fulfillmentType, List<String> disclosures) {
		ListingResponse response = new ListingResponse();

		HttpsURLConnection con = null;

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(Constants.SELLER_PRO_SERVICE_ROOT
					+ "/api/listings/seller/" + sellerId);

			con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("accept", "application/xml");
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Cookie", sessionCookie);

			Log.d("Koson", "session cookie is " + sessionCookie);

			con.setReadTimeout(Constants.SELLER_PRO_READ_TIMEOUT);
			con.setConnectTimeout(Constants.SELLER_PRO_CONNECT_TIMEOUT);

			/*
			 * String cookie =
			 * "STUB_PERSISTENT=filler%7E%5E%7E1%7Cstub_uid%7E%5E%7E13877497%7E%5E%7E05%2F04%2F2012; Domain=.stubhub.com; Expires=Sun, 27-Apr-2042 06:24:02 GMT; Path=/; "
			 * +
			 * "STUB_MYACT_INFO=filler%7E%5E%7E0%7Cmyact_beta_pref%7E%5E%7EOUT%7E%5E%7E05%2F04%2F2012; Domain=.stubhub.com; Path=/; "
			 * +
			 * "STUB_SESSION=filler%7E%5E%7E0%7Cstub_sid%7E%5E%7E3775459192%7E%5E%7E05%2F04%2F2012; Domain=.stubhub.com; Path=/; "
			 * +
			 * "STUB_SECR=filler%7E%5E%7E0%7Cguid%7E%5E%7EBF301134AF6A547EE04400212868B1B6%7E%5E%7E05%2F04%2F2012; Domain=.stubhub.com; Path=/; Secure; "
			 * +
			 * "STUB_INFO=filler%7E%5E%7E0%7Cu2%7E%5E%7E8D08E516A9467090E0440021286899D6%7E%5E%7E05%2F04%2F2012; Domain=.stubhub.com; Expires=Sun, 27-Apr-2042 06:24:02 GMT; Path=/; "
			 * +
			 * "STUB_SESS=filler%7E%5E%7E0%7Cguid%7E%5E%7EBF301134AF6A547EE04400212868B1B6%7E%5E%7E05%2F04%2F2012; Domain=.stubhub.com; Path=/; "
			 * +
			 * "TLTSID=BDA64CF295B11095012089DB3CA206A8; Path=/; Domain=.stubhub.com; "
			 * +
			 * "TLTHID=BDA64CF295B11095012089DB3CA206A8; Path=/; Domain=.stubhub.com; "
			 * ; con.setRequestProperty("Cookie",cookie);
			 */
			// con.setRequestProperty("username","yancao@ebay.com");//koson
			// con.setRequestProperty("password","123456");//koson

			StringBuffer sb = new StringBuffer("<listingRequest>");
			sb.append("<eventId>" + eventId + "</eventId>");
			sb.append("<section>" + section + "</section>");
			sb.append("<row>" + row + "</row>");
			if (seats != null) {
				sb.append("<seats>" + seats + "</seats>");
			}
			sb.append("<pricePerTicket><amount>" + ticketPrice
					+ "</amount><currency>" + currency
					+ "</currency></pricePerTicket>");
			if (faceValue != null) {
				sb.append("<ticketFaceValue><amount>" + ticketPrice
						+ "</amount><currency>" + currency
						+ "</currency></ticketFaceValue>");
			}
			if (splits != null) {
				sb.append("<splitQuantity>" + splits + "</splitQuantity>");
			}
			sb.append("<quantity>" + quantity + "</quantity>");
			sb.append("<ticketInHandDate>" + inHandDate + "</ticketInHandDate>");
			sb.append("<deliveryOption>" + fulfillmentType
					+ "</deliveryOption>");
			for (int i = 0; i < disclosures.size(); i++) {
				sb.append("<ticketTrait><name>"
						+ disclosures.get(i)
						+ "</name><type>Listing Disclosure</type></ticketTrait>");
			}
			sb.append("</listingRequest>");
			con.setDoInput(true);
			con.setDoOutput(true);

			DataOutputStream out = new DataOutputStream(con.getOutputStream());

			// String ss = "<listingRequest><eventId>"+ eventId +
			// "</eventId><section>2</section><row>3</row><pricePerTicket><amount>9999.00</amount><currency>USD</currency></pricePerTicket><quantity>1</quantity><ticketInHandDate>"
			// + inHandDate + "</ticketInHandDate><deliveryOption>" +
			// fulfillmentType + "</deliveryOption></listingRequest>";
			// Log.d("Koson","the body is " + ss);
			String body = sb.toString();
			Log.d("Koson", "the body is " + body);
			out.writeBytes(body);
			out.flush();
			out.close();

			con.connect();// koson
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Log.d("Koson", "response code is " + con.getResponseCode());
			Log.d("Koson", "response code is " + con.getResponseMessage());
			int responseCode = con.getResponseCode();

			/*
			 * InputStream is; if(responseCode < 400) { is =
			 * con.getInputStream(); } else { is = con.getErrorStream(); }
			 * 
			 * InputStreamReader reader= new InputStreamReader(is);
			 * BufferedReader stdin = new BufferedReader(reader);
			 * Log.d("Koson","=====Start printing==="); StringBuffer sbb = new
			 * StringBuffer(); String s = stdin.readLine(); while(s!=null) {
			 * sbb.append(s); s = stdin.readLine(); }
			 * Log.d("koson","message is " + sbb.toString());
			 */

			Document doc;
			if (responseCode < 400) {
				doc = db.parse(new InputSource(con.getInputStream()));
			} else {
				doc = db.parse(new InputSource(con.getErrorStream()));
				Log.d("Koson", "=======in error stream");
			}
			doc.getDocumentElement().normalize();

			NodeList errorCodeList = doc.getElementsByTagName("errorCode");
			if (errorCodeList == null || errorCodeList.getLength() == 0) {
				response.setSuccessFlag(true);
				response.setListingId(getTextContent((Element) doc
						.getElementsByTagName("listingId").item(0)));
			} else {
				response.setSuccessFlag(false);
				response.setErrorCode(getTextContent((Element) errorCodeList
						.item(0)));
				response.setErrorType(getTextContent((Element) doc
						.getElementsByTagName("errorType").item(0)));
			}
			return response;

		} catch (Exception e) {
			Log.e(Constants.TAG,
					"Error adding listing: " + Log.getStackTraceString(e));
			return null;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	public static EventInfo getEventInfo(String eventId, String sessionCookie) {
		EventInfo eventInfo = new EventInfo();
		List<DeliveryOption> dOptions = new ArrayList<DeliveryOption>();
		List<String> disclosuresList = new ArrayList<String>();

		HttpsURLConnection con = null;

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			// SSLContext sc = SSLContext.getInstance("SSL");
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

			/*
			 * setting this because the SelerPro certificates are not recognized
			 * by Android.
			 * 
			 * Android 2.X does not support Server Name Indication so if your
			 * server is relying on it for SSL handshaking, Android may not be
			 * getting the certificates you're expecting. Do you have
			 * certificate chain on the server installed, and is it ordered
			 * correctly? Most browsers handle out-of-order certificate chains
			 * but Android 2.X does not.
			 */

			URL url = new URL(Constants.SELLER_PRO_SERVICE_ROOT
					+ "/api/events/" + eventId);
			Log.d("Koson", "get eventInfo url is: "
					+ Constants.SELLER_PRO_SERVICE_ROOT + "/api/events/"
					+ eventId);

			con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("accept", "application/xml");
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Cookie", sessionCookie);

			con.setReadTimeout(Constants.SELLER_PRO_READ_TIMEOUT);
			con.setConnectTimeout(Constants.SELLER_PRO_CONNECT_TIMEOUT);

			con.setDoInput(true);

			con.setHostnameVerifier(new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					return true;
				}
			});

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			/*
			 * InputStreamReader reader= new
			 * InputStreamReader(con.getInputStream()); BufferedReader stdin =
			 * new BufferedReader(reader);
			 * Log.d("Koson","=====Start printing==="); StringBuffer sb = new
			 * StringBuffer(); String s = stdin.readLine(); while(s!=null) {
			 * sb.append(s); s = stdin.readLine(); } Log.d("koson","message is "
			 * + sb.toString());
			 */

			Document doc = db.parse(new InputSource(con.getInputStream()));
			doc.getDocumentElement().normalize();

			NodeList deliveryOptionList = doc
					.getElementsByTagName("fulfillmentMethod");
			Log.d("Koson", "the length is " + deliveryOptionList.getLength());
			for (int i = 0; i < deliveryOptionList.getLength(); i++) {
				Element name = (Element) ((Element) deliveryOptionList.item(i))
						.getElementsByTagName("name").item(0);
				Element endDate = (Element) ((Element) deliveryOptionList
						.item(i)).getElementsByTagName("endDate").item(0);
				DeliveryOption option = new DeliveryOption();
				option.setName(name.getFirstChild().getNodeValue());
				option.setEndDate(endDate.getFirstChild().getNodeValue());
				dOptions.add(option);

			}
			eventInfo.setDeliveryOptions(dOptions);

			NodeList ticketTraitList = doc.getElementsByTagName("ticketTrait");
			for (int i = 0; i < ticketTraitList.getLength(); i++) {
				String name = ((Element) ((Element) ticketTraitList.item(i))
						.getElementsByTagName("name").item(0)).getFirstChild()
						.getNodeValue();
				String type = ((Element) ((Element) ticketTraitList.item(i))
						.getElementsByTagName("type").item(0)).getFirstChild()
						.getNodeValue();
				Log.d("Koson", name + "====" + type);
				if (type.equalsIgnoreCase("Listing Disclosure")) {
					disclosuresList.add(name);
				}
			}
			eventInfo.setDisclosures(disclosuresList);

			return eventInfo;
		} catch (Exception e) {
			Log.e(Constants.TAG,
					"Error getting event info " + Log.getStackTraceString(e));
			return eventInfo;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private static String getTextContent(Node node) {
		if (node.getFirstChild() == null
				|| node.getFirstChild().getNodeValue() == null) {
			return "";
		}
		return node.getFirstChild().getNodeValue();

	}
}
