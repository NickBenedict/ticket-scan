package com.stubhub.ticketscan;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

import com.stubhub.Constants;
import com.stubhub.entities.ListingResponse;
import com.stubhub.entities.MobileListing;
import com.stubhub.queryhandlers.SellingHandler;

public class SellService {

	/**
	 * 
	 * @param deviceId
	 * @param username
	 * @param password
	 * @param sellerListing
	 * @param eventId
	 * @return listingId
	 */
	public static String sell(String deviceId, String username, String password,
			MobileListing sellerListing, String eventId) {

		// 1.
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("username", username);
		headers.put("password", password);
		headers.put("accept", "application/xml");
		String sellerGuid = getUserGuid(headers);

		// 2.
		String sessionCookie = SellingHandler.getSessionCookie(username,
				password);
		if (sessionCookie.equals("")) {
			return null;
		}

		// 3.
		ListingResponse response = SellingHandler.addListing(sellerGuid,
				sessionCookie, eventId,
				String.valueOf(sellerListing.getQuantity()),
				sellerListing.getSection(), sellerListing.getRow(),
				sellerListing.getSeats(), sellerListing.getFaceValue(),
				sellerListing.getPrice(),
				//TODO US or UK?
				getCurrencyName(Constants.US_BOOK_OF_BUSINESS_ID),
				sellerListing.getSplit(), sellerListing.getTihDate(),
				sellerListing.getDeliveryMethod(),
				sellerListing.getDisclosuresList());

		// 4.
		response = SellingHandler.attachBarcodeToListing(sellerGuid,
				sessionCookie, response.getListingId(),
				sellerListing.getBarcodeList());
		
		return response.getListingId();
	}

	private static String getUserGuid(HashMap<String, String> headers) {
		Document doc = getMyAccountResponse(Constants.MYACCOUNT_REST_SERVER
				+ "/myaccountapi/myaccount/user/userGuid", headers, "GET", null);
		// Document doc =
		// getMyAccountsResponse("https://myaccount.srwd33.com/myaccountapi/myaccount/user/userGuid/session",headers,
		// "GET", null);
		try {
			NodeList nodeList = doc.getDocumentElement().getElementsByTagName(
					"UserGuid");
			return getTextContent(nodeList.item(0));
		} catch (Exception e) {
			Log.e(Constants.TAG,
					"Exception while retrieving userGuid: "
							+ Log.getStackTraceString(e));
			return null;
		}
	}

	private static String getTextContent(Node node) {
		if (node.getFirstChild() == null
				|| node.getFirstChild().getNodeValue() == null) {
			return "";
		}
		return node.getFirstChild().getNodeValue();

	}

	private static Document getMyAccountResponse(String query,
			HashMap<String, String> headers, String requestMethod,
			String actionContent) {
		Log.d("Koson", "myaccount query is " + query);
		Document doc = null;
		HttpURLConnection con = null;
		try {
			if (Thread.interrupted())
				Log.d(Constants.TAG, "My Account query interrupted");

			URL url = new URL(query);

			System.setProperty("http.keepAlive", "false");

			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(Constants.MYACCOUNT_READ_TIMEOUT);
			con.setConnectTimeout(Constants.MYACCOUNT_CONNECT_TIMEOUT);
			con.setRequestMethod(requestMethod);

			Set<String> keySet = headers.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				con.setRequestProperty(key, headers.get(key));
			}
			con.setDoInput(true);
			if (actionContent != null) {
				con.setDoOutput(true);
				con.setFixedLengthStreamingMode(actionContent.length());
				con.setRequestProperty("Content-Type", "application/json");

				DataOutputStream out = new DataOutputStream(
						con.getOutputStream());
				out.writeBytes(actionContent);
				out.flush();
				out.close();
			}

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				doc = db.parse(new InputSource(con.getInputStream()));
				doc.getDocumentElement().normalize();
			} catch (Exception e) {
				Log.e(Constants.TAG,
						"XML Parsing exception in My Account query: "
								+ Log.getStackTraceString(e));
				return null;
			}
		} catch (Exception e) {
			Log.e(Constants.TAG,
					"exception in My Account query "
							+ Log.getStackTraceString(e));
			return null;
		} finally {
			if (con != null)
				con.disconnect();
		}
		return doc;
	}

	private static String getCurrencyName(int bookOfBusinessId) {
		if (bookOfBusinessId == Constants.UK_BOOK_OF_BUSINESS_ID) {
			return "GBP";
		} else {
			return "USD";
		}
	}
}
