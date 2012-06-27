package com.stubhub.ticketscan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ebay.redlasersdk.BarcodeResult;
import com.itwizard.mezzofanti.CameraManager;
import com.itwizard.mezzofanti.Mezzofanti;
import com.stubhub.entities.MobileListing;

public class ScanTicketActivity extends Activity {

	private static final int REQUEST_CODE_SCAN_BARCODE = 1234;

	private static final String TAG = "ScanTicketActivity";

	private String eventId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sp = getSharedPreferences("counters", 0);
		int launchTimes = sp.getInt("launch", 0);

		// XXX TEST
		if (true || launchTimes == 0) {
			// initial the languages

			try {
				copyFileOrDir("languages/eng.zip", Mezzofanti.DATA_PATH);

				ZipFile zipFile = new ZipFile(Mezzofanti.DATA_PATH + "eng.zip");

				Enumeration entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) entries.nextElement();
					InputStream in = zipFile.getInputStream(entry);
					FileOutputStream out = new FileOutputStream(
							Mezzofanti.DATA_PATH + entry.getName());

					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) >= 0) {
						out.write(buffer, 0, len);
					}
					out.flush();

					in.close();
					out.close();
				}

				zipFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		launchTimes++;

		sp.edit().putInt("launch", launchTimes).commit();

		setContentView(R.layout.scan_ticket);
		TextView textView = (TextView) findViewById(R.id.TextTitle);

		textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

		textView.getPaint().setFakeBoldText(true);
		CameraManager.Initialize(getApplication());

		// XXX test
		if (false) {
			((EditText) findViewById(R.id.text_barcode)).setText("");
			((EditText) findViewById(R.id.text_Event_name))
					.setText("Arizona Diamondbacks at San Francisco Giants Tickets\nThursday September 27, 2012 at 12:45p.m.\nAt&T Park");
			((EditText) findViewById(R.id.text_scan_Section))
					.setText("Field Club 121");
			((EditText) findViewById(R.id.text_Row)).setText("4");
			((EditText) findViewById(R.id.text_scan_Seat)).setText("10");
			((EditText) findViewById(R.id.text_Trait_Discosure))
					.setText("The barcode only allow one entry per person");
			((EditText) findViewById(R.id.text_Ticket_price_Value))
					.setText("60");
		}

		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_barcode);
			
			
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
						bundle1.putBoolean(RedLaserSDK.DO_DATAMATRIX,
								doDataMatrix);
						bundle1.putBoolean(RedLaserSDK.DO_RSS14, doRSS14);
						bundle1.putBoolean(RedLaserSDK.DO_ITF, doITF);
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								RedLaserSDK.class);
						scanIntent.putExtras(bundle1);
						startActivityForResult(scanIntent,
								REQUEST_CODE_SCAN_BARCODE);
					} catch (Exception e) {
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_Event_name);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(0.6f);
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanHeightRatio(0.2f);

						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						// just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_Event_name);
					} catch (Exception e) {
						e.printStackTrace();
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_Section);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(0.2f);
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						// just use the id of the text view as request id
						startActivityForResult(scanIntent,
								R.id.text_scan_Section);
					} catch (Exception e) {
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_Row);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(0.06f);
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						// just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_Row);
					} catch (Exception e) {
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_Seat);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(0.06f);
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						// just use the id of the text view as request id
						startActivityForResult(scanIntent, R.id.text_scan_Seat);
					} catch (Exception e) {
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_Ticket_Price);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(0.1f);
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						// just use the id of the text view as request id
						startActivityForResult(scanIntent,
								R.id.text_Ticket_price_Value);
					} catch (Exception e) {
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_scan_Trait);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(0.65f);
						com.itwizard.mezzofanti.CameraManager.get()
							.setScanHeightRatio(0.12f);
						Intent scanIntent = new Intent(ScanTicketActivity.this,
								Mezzofanti.class);
						// just use the id of the text view as request id
						startActivityForResult(scanIntent,
								R.id.text_Trait_Discosure);
					} catch (Exception e) {
						Log.d(TAG, e.getLocalizedMessage() + " " + e.getCause());
					}
				}
			});
		}
		{
			ImageButton button = (ImageButton) findViewById(R.id.button_sell);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// do in background

					final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_addlisting);

					progressBar.setVisibility(View.VISIBLE);

					AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {

						@Override
						protected String doInBackground(String... arg0) {
							final String listingId = addListing();

							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									progressBar.setVisibility(View.GONE);

									if (listingId != null) {

										new AlertDialog.Builder(
												ScanTicketActivity.this)
												.setTitle(
														"Ticket on Shelf Now!")
												.setMessage(
														"Would you like to view it?")
												.setPositiveButton(
														"Yes",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																Intent intent = new Intent(
																		ScanTicketActivity.this,
																		TicketListingActivity.class);
																intent.putExtra(
																		"event_id",
																		eventId);
																intent.putExtra(
																		"ticket_id",
																		listingId);
																startActivity(intent);
															}
														})
												.setNegativeButton("No", null)
												.show();

									} else {
										Toast.makeText(ScanTicketActivity.this,
												"Failed!", Toast.LENGTH_LONG)
												.show();
									}
								}
							});

							return listingId;
						}

					};

					task.execute();

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

		// set default value
		com.itwizard.mezzofanti.CameraManager.get().resetScanSize();

		if (resultCode == Activity.RESULT_OK) {

			boolean isScanBarcode = requestCode == REQUEST_CODE_SCAN_BARCODE;

			if (isScanBarcode) {

				String barcode = data.getAction();
				String barcodeType = data
						.getStringExtra(BarcodeResult.BARCODE_TYPE);
				Log.d(TAG, "BARCODE: " + barcode);

				TextView textView = (TextView) findViewById(R.id.text_barcode);
				textView.setText(barcode);

			} else {

				TextView textView = (TextView) findViewById(requestCode);
				textView.setText(data.getExtras().getString("content"));

			}

		}

	}

	private String addListing() {

		if (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return "";
		}

		try {

			String deviceId = ((TelephonyManager) getBaseContext()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

			String username = "test@sh.com";
			String password = "123456";

			String eventName = ((EditText) findViewById(R.id.text_Event_name))
					.getText().toString();

			// TODO from event name
			// http://www.stubhub.com/san-francisco-giants-tickets/giants-vs-diamondbacks-5-29-2012-2031178/
			eventId = "2031178";

			MobileListing sellerListing = new MobileListing();

//			sellerListing
//					.addBarcode(((EditText) findViewById(R.id.text_barcode))
//							.getText().toString());

			sellerListing.setQuantity(1);
			sellerListing
					.setSection(((EditText) findViewById(R.id.text_scan_Section))
							.getText().toString());
			sellerListing.setRow(((EditText) findViewById(R.id.text_Row))
					.getText().toString());
			sellerListing
					.addSeat(((EditText) findViewById(R.id.text_scan_Seat))
							.getText().toString());
			sellerListing
					.setPrice(((EditText) findViewById(R.id.text_Ticket_price_Value))
							.getText().toString());

			List<String> disclosures = new ArrayList<String>();
			disclosures
					.add(((EditText) findViewById(R.id.text_Trait_Discosure))
							.getText().toString());
			sellerListing.setDisclosuresList(disclosures);

			// TODO EventInfo.getDeliveryOptions.getName
			sellerListing.setDeliveryMethod("PDF");

			sellerListing.setIsInHand(true);

			String listingId = SellService.sell(deviceId, username, password,
					sellerListing, eventId);

			return listingId;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void copyFileOrDir(String path, String targetPath) {
		AssetManager assetManager = this.getAssets();
		String assets[] = null;
		try {
			assets = assetManager.list(path);
			if (assets.length == 0) {
				String fileName = path.substring(path.lastIndexOf("/") + 1);
				String sourcePath = path.substring(0, path.lastIndexOf("/"));
				copyFile(sourcePath, fileName, targetPath);
			} else {
				String fullPath = targetPath + "/" + path;
				File dir = new File(fullPath);
				if (!dir.exists())
					dir.mkdir();
				for (int i = 0; i < assets.length; ++i) {
					copyFileOrDir(path + "/" + assets[i], targetPath);
				}
			}
		} catch (IOException ex) {
			Log.e("tag", "I/O Exception", ex);
		}
	}

	private void copyFile(String sourcePath, String filename, String targetPath) {
		AssetManager assetManager = this.getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(sourcePath + "/" + filename);

			new File(targetPath).mkdirs();

			String newFileName = targetPath + "/" + filename;
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}
}
