package com.stubhub.ticketscan;

import com.stubhub.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class TicketListingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_listing);

		WebView webView = (WebView) findViewById(R.id.webView);

		String eventId = getIntent().getStringExtra("event_id");
		String ticketId = getIntent().getStringExtra("ticket_id");

		if (true) {
			webView.loadUrl("http://www.stubhub.com/san-francisco-giants-tickets/giants-vs-diamondbacks-9-27-2012-2032198/");
		} else {
			// http://www.stubhub.com/?event_id=2031179&ticket_id=439999751
			webView.loadUrl(Constants.LCS_ROOT_SERVER + "/?event_id=" + eventId
					+ "&ticket_id=" + ticketId);
		}
	}
	
}
