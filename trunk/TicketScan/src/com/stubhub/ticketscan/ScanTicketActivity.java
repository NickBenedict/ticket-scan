package com.stubhub.ticketscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ebay.redlasersdk.BarcodeResult;
import com.itwizard.mezzofanti.Mezzofanti;
//import com.itwizard.mezzofanti.R;
import com.stubhub.ticketscan.R;
public class ScanTicketActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scan_ticket);

		{
		Button button = (Button) findViewById(R.id.button_scan_barcode);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					Bundle bundle1 = new Bundle();
					bundle1.putBoolean(RedLaserSDK.DO_UPCE, doUpce);
					bundle1.putBoolean(RedLaserSDK.DO_EAN8, doEan8);
					bundle1.putBoolean(RedLaserSDK.DO_EAN13, doEan13);
					bundle1.putBoolean(RedLaserSDK.DO_STICKY, doSticky);
					bundle1.putBoolean(RedLaserSDK.DO_QRCODE, doQRCode);
					bundle1.putBoolean(RedLaserSDK.DO_CODE128, doCode128);
					bundle1.putBoolean(RedLaserSDK.DO_CODE39, doCode39);
					bundle1.putBoolean(RedLaserSDK.DO_CODE93, doCode93);
					bundle1.putBoolean(RedLaserSDK.DO_DATAMATRIX, doDataMatrix);
					bundle1.putBoolean(RedLaserSDK.DO_RSS14, doRSS14);
					bundle1.putBoolean(RedLaserSDK.DO_ITF, doITF);
					Intent scanIntent = new Intent(ScanTicketActivity.this,
							RedLaserSDK.class);
					scanIntent.putExtras(bundle1);
					startActivityForResult(scanIntent, 1234);
				} catch (Exception e) {
					Log.d("RLSample",
							e.getLocalizedMessage() + " " + e.getCause());
				}
			}
		});
		}
		{
			Button button = (Button) findViewById(R.id.button_scan_Event_name);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						 //just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_Event_name);
					} catch (Exception e) {
						Log.d("RLSample",
								e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			Button button = (Button) findViewById(R.id.button_scan_Section);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						 //just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_scan_Section);
					} catch (Exception e) {
						Log.d("RLSample",
								e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			Button button = (Button) findViewById(R.id.button_scan_Row);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						 //just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_Row);
					} catch (Exception e) {
						Log.d("RLSample",
								e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			Button button = (Button) findViewById(R.id.button_scan_Seat);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						 //just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_scan_Seat);
					} catch (Exception e) {
						Log.d("RLSample",
								e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			Button button = (Button) findViewById(R.id.button_scan_Ticket_Price);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						 //just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_Ticket_price_Value);
					} catch (Exception e) {
						Log.d("RLSample",
								e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			Button button = (Button) findViewById(R.id.button_scan_Trait);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						 //just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_Trait_Discosure);
					} catch (Exception e) {
						Log.d("RLSample",
								e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
	}

	// TODO let user customize?
	private boolean doUpce = true;
	private boolean doEan8 = true;
	private boolean doEan13 = true;
	private boolean doSticky = true;
	private boolean doQRCode = true;
	private boolean doCode128 = true;
	private boolean doCode39 = true;
	private boolean doCode93 = true;
	private boolean doDataMatrix = true;
	private boolean doRSS14 = true;
	private boolean doITF = true;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			boolean isScanBarcode = requestCode == 1234;

			if (isScanBarcode) {

				String barcode = data.getAction();
				String barcodeType = data
						.getStringExtra(BarcodeResult.BARCODE_TYPE);
				Log.d("RLSample", "BARCODE: " + barcode);

				TextView textView = (TextView) findViewById(R.id.text_barcode);
				textView.setText(barcode);

			} else {
				Bundle show=data.getExtras();
				TextView textView = (TextView) findViewById(requestCode);
				//TextView textView = (TextView) findViewById(R.id.button_scan_field1);
				textView.setText(show.getString("content"));
				
			}

		}

	}

}
