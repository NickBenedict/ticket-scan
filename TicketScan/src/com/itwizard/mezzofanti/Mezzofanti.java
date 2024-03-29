/*
 * Copyright (C) 2009 The ITWizard
 *
 */

package com.itwizard.mezzofanti;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;

import com.stubhub.Constants;
import com.stubhub.ticketscan.R;
import com.stubhub.ticketscan.ScanTicketActivity;

public class Mezzofanti extends Activity implements SurfaceHolder.Callback,
		View.OnTouchListener, View.OnClickListener, Runnable {
	/*
	 * --------------------------------------------------------------------------
	 * -------------- the CONSTANTS
	 * ----------------------------------------------
	 * ------------------------------------------
	 */
	// language files that are stored in "assets"
	public static final String svLangExtNames[] = { ".DangAmbigs",
			".freq-dawg", ".fst", ".inttemp", ".ngram_triv", ".normproto",
			".pffmtable", ".unicharset", ".user-words", ".word-dawg" };

	// global variables used throughout the code
	public static final String PACKAGE_NAME = "com.itwizard.mezzofanti";
	public static final String DATA_PATH = "/sdcard/tessdata/";
	public static final String RESULTS_PATH = DATA_PATH + "out/";
	public static final String TEMP_PATH = DATA_PATH + "temp/";
	public static final String PACKAGE_DATA = "/data/app/" + PACKAGE_NAME
			+ ".apk";
	public static final String DOWNLOAD_URL = "http://www.itwizard.ro/mezzolang/";
	public static final String CONFIG_FILE = "config.txt";

	// public variables
	public static boolean m_bSkipIntroAtStartup = true; // skip the introduction
														// at startup (loaded
														// from the xml
														// prefs-file)

	private boolean m_bHorizontalDisplay = true; // true=horizontal /
													// false=vertical (this is
													// updated all the time)
	private Bitmap m_bmOCRBitmap; // bitmap we get the image from camera
	private boolean m_bHorizDispAtPicTaken = true; // this is updated just
													// before the picture taken,
													// it will be sent as a
													// param to the OCR
													// processing
	private boolean m_bScreenRequestPicture = false;

	/*
	 * --------------------------------------------------------------------------
	 * -------------- local variables
	 * --------------------------------------------
	 * --------------------------------------------
	 */
	private static final String TAG = "MLOG: Mezzofonti.java: ";

	private boolean m_bIntroWasDisplayedAtStartup = false; // only true when the
															// app first starts
															// (false at resume)
	private boolean m_bHasSurface = false; // used to init the camera
	private boolean m_bSdcardMounted = true; // checks if the sdcard is mounted
												// or not
	private boolean m_bPreviewReady = false; // is the preview ready
	private boolean m_bFocusButtonPressed = false; // focus button is pressed

	private ProgressDialog m_pdOCRInProgress; // display this progress dialog
												// while OCR runs
	private OrientationEventListener m_OEListener = null; // used to get
															// vertical/horizontal
															// camera position
	private OnScreenHint m_oshStorageHint; // a persistent-toast(message)
											// displayed on the screen
	private CaptureLayout m_clCapture; // Capture-mode

	private boolean m_bDidRegister = false; // was the broadcast receiver
											// registered or not
	private boolean m_bOCRInProgress = false; // if OCR is processing or not

	private boolean m_bLineMode = true; // capture-mode: line/all
	private String m_sOCRResultLineMode = ""; // the ocr string in line mode

	// 'Menu' items
	private Menu m_sPreferencesMenu = null;
	private static final int PREFERENCES_GROUP_ID = 0;
	private static final int SETTINGS_ID = Menu.FIRST;
	private static final int HELP_ID = Menu.FIRST + 1;
	private static final int FEEDBACK_ID = Menu.FIRST + 2;
	private static final int ABOUT_ID = Menu.FIRST + 3;
	private static int ScreenLength = 0;
	private static int ScreenWidth = 0;
	// the buttons on small-capture mode
	private ImageButton m_btSwitch;
	private ImageButton m_btDelOne;
	private ImageButton m_btGotoResults;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float oldLength = 1f;
	float oldWidth = 1f;

	/*
	 * --------------------------------------------------------------------------
	 * -------------- Overrides
	 * --------------------------------------------------
	 * --------------------------------------
	 */
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String value = getIntent().getExtras().getString(Constants.CURRENT_SECTION);

		try {
			Log.v(TAG, "Starting app");

			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

			// never enter standby when camera open
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			CameraManager.Initialize(getApplication());

			// set the layout to the xml definition
			setContentView(R.layout.main);
			
			TextView currentSectionTextView = (TextView)findViewById(R.id.mezzofanti_current_section);
			currentSectionTextView.setText(value);
			
			final ImageButton bt = (ImageButton) findViewById(R.id.mezzofanti_button_camerabutton);
			bt.setBackgroundResource(R.drawable.camera_64);
			bt.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View arg0, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						bt.setBackgroundResource(R.drawable.camera_64_clicked);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						bt.setBackgroundResource(R.drawable.camera_64);
					}
					return false;
				}
			});
			bt.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					bt.setBackgroundResource(R.drawable.camera_64_clicked);
					Log.v(TAG, "CameraButton OnClick");
					m_bScreenRequestPicture = true;
					RequestCameraFocus();
					bt.setBackgroundResource(R.drawable.camera_64);
				}
			});

			m_clCapture = (CaptureLayout) findViewById(R.id.mezzofanti_capturelayout_view);
			m_clCapture.setOnTouchListener(this);

			m_bSdcardMounted = CheckSDCardState();

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			OCR.mConfig.GetSettings(prefs);

			if (m_bLineMode)
				CameraManager.SetImgDivisor(2);
			else
				CameraManager.SetImgDivisor(OCR.mConfig.GetImgDivisor());

			m_bHasSurface = false;

			// register to the orientation events
			CreateOrientationListener(this);
			OrientationEnable(true);

			RefreshStatusBar("");

			m_bSkipIntroAtStartup = prefs.getBoolean(
					PreferencesActivity.KEY_SKIP_INTRO_AT_STARTUP, true);
			Log.v(TAG, "skipintro=" + m_bSkipIntroAtStartup
					+ " introwasdisplayed=" + m_bIntroWasDisplayedAtStartup);
			// StartInstallActivity(); // note: this should be called only after
			// OCR.mConfig.GetSettings(prefs);

			m_MezzofantiMessageHandler
					.sendEmptyMessage(R.id.mezzofanti_startCamera);

			CreateStatusbarButtonsCaptureMode();
			if (m_bLineMode)
				ShowLineModeButtons(true);
		} catch (Exception ex) {
			Log.e(TAG, "exception: onCreate():" + ex.toString());
		} catch (Throwable t) {
			Log.e(TAG, "exception: onCreate():" + t.toString());
		}
	}

	@Override
	protected void onPostResume() {
		Log.v(TAG, "onPostResume()");
		super.onPostResume();
	}

	@Override
	public void onDestroy() {
		StopCamera();
		OrientationEnable(false);

		OCR ocr = OCR.get();
		if (ocr != null)
			ocr.Destructor();

		super.onDestroy();
		Log.d(TAG, "onDestroy() ENDING.");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume() ----------------------------");
		Log.v(TAG, "mconfig lang=" + OCR.mConfig.GetLanguageMore());

		// remove ocr results
		m_bmOCRBitmap = null;
		System.gc();

		RefreshStatusBar("");
		m_bSdcardMounted = CheckSDCardState();

		// install an intent filter to receive SD card related events.
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");
		registerReceiver(m_brSDcardEvent, intentFilter);

		m_bDidRegister = true;
		Log.v(TAG,
				"onResume() filter to receive SD card related events----------------------------");

		// read the new settings, maybe the user changed something in results
		// mode
		// so we store all the settings data in the local variables
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		OCR.mConfig.GetSettings(prefs);
		m_bSkipIntroAtStartup = prefs.getBoolean(
				PreferencesActivity.KEY_SKIP_INTRO_AT_STARTUP, true);

		if (m_bSdcardMounted && AssetsManager.IsInstalled()) {
			OCR.Initialize();
			OCR.get().SetLanguage(OCR.mConfig.GetLanguage());
		}

		if (m_bLineMode)
			CameraManager.SetImgDivisor(2);
		else
			CameraManager.SetImgDivisor(OCR.mConfig.GetImgDivisor());

		m_MezzofantiMessageHandler
				.sendEmptyMessage(R.id.mezzofanti_startCamera);

		// returned in Capture-mode
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.mezzofanti_preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (m_bHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			InitCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	private void GetScreenSize() {
		ScreenWidth = this.getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		Log.e(" ScreenWidth9999", "Screen" + ScreenWidth);
		ScreenLength = this.getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		Log.e(" ScreenLength9999", "Screen" + ScreenLength);
	}

	public boolean onTouch(View v, MotionEvent event) {

		Log.e(TAG, "MezzofantiOnTouch");
		// Dump touch event to log
		GetScreenSize();
		// Handle touch events here...
		// The Icons field which are used for operating
		if (Width(event) > ScreenWidth * 0.86				|| Heigth(event) > ScreenLength * 0.85
				|| Heigth(event) < ScreenLength * 0.2) {

			return false;
		} else {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				// setting the initial sides
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldLength = CaptureScreenLength(event);
				oldWidth = CaptureScreenWidth(event);
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;

				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				} else if (mode == ZOOM) {
					float enlargeWidth = 0;
					float enlaregeLength = 0;
					float newWidth = CaptureScreenWidth(event);
					float newLength = CaptureScreenLength(event);
					int JudgeOrientation = MoveOrientation(oldLength, oldWidth,
							newLength, newWidth);
					matrix.set(savedMatrix);
					enlargeWidth = newWidth / oldWidth;
					enlaregeLength = newLength / oldLength;
					Log.e("EnlaregeLength", "EnlaregeLength" + enlaregeLength);

					float currentwidth = 0;
					float currentlength = 0;
					currentwidth = com.itwizard.mezzofanti.CameraManager.get()
							.GetScanWidthRatio();
					currentlength = com.itwizard.mezzofanti.CameraManager.get()
							.GetScanHeightRation();

					if (enlargeWidth > 1 && JudgeOrientation == 0) {
						Log.e("mode=ZOOM", "enlargeWidth" + enlargeWidth);
						currentwidth += 0.02f;
					} // zoom size
					if (enlaregeLength > 1 && JudgeOrientation == 1) {
						Log.e("mode=ZOOM", "enlaregeLength" + enlaregeLength);
						currentlength += 0.02f; // zoom size
					}

					if (enlargeWidth < 1 && JudgeOrientation == 0) {
						currentwidth -= 0.02f;
					}
					if (enlaregeLength < 1 && JudgeOrientation == 1) {
						Log.e("mode=MINE", "enlaregeLength" + enlaregeLength);
						currentlength -= 0.02f;
					}
					if ((currentwidth >= 0.05f && currentlength >= 0.05f)
							&& (currentlength <= 0.55f && currentwidth <= 0.95f)) {
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanWidthRatio(currentwidth);
						com.itwizard.mezzofanti.CameraManager.get()
								.setScanHeightRatio(currentlength);
						ShowLineModeButtons(true);
					}

				}
				break;
			}
			return true; // indicate event was handled
		}

	}

	private float CaptureScreenLength(MotionEvent event) {
		float y = event.getY(0) - event.getY(1);
		Log.e("Position", "Y0" + event.getY(0));
		Log.e("Position", "Y1" + event.getY(1));
		Log.e("Length2233", "Length" + y);
		return FloatMath.sqrt(y * y);
	}

	// To judge whether the move direction is larger X pixel or Y pixel
	private int MoveOrientation(float oldlength, float oldwidth,
			float newlength, float newwidth) {
		int largeSize = 0;
		float distanceLength = FloatMath.sqrt((newlength - oldlength)
				* (newlength - oldlength));
		float distanceWidth = FloatMath.sqrt((newwidth - oldwidth)
				* (newwidth - oldwidth));
		if (distanceLength > distanceWidth) {
			largeSize = 1;
		} else {
			largeSize = 0;
		}

		return largeSize;
	}

	private float CaptureScreenWidth(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		Log.e("Position", "X0" + event.getX(0));
		Log.e("Position", "X1" + event.getX(1));
		Log.e("Width1122", "width" + x);
		return FloatMath.sqrt(x * x);
	}

	private float Width(MotionEvent event) {
		float x = event.getX();
		return FloatMath.sqrt(x * x);
	}

	private float Heigth(MotionEvent event) {
		float y = event.getY();
		return FloatMath.sqrt(y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@Override
	protected void onPause() {
		// unregister the sdcard broadcast receiver
		if (m_bDidRegister) {
			unregisterReceiver(m_brSDcardEvent);
			m_bDidRegister = false;
		}

		UpdateStorageHint(false);
		StopCamera();

		super.onPause();
	}

	// @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	// @Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surfaceCreated.");
		if (!m_bHasSurface) {
			// set the surface holder for the camera's use
			m_bHasSurface = true;
			SetCameraSurfaceHolder(holder);
			m_MezzofantiMessageHandler
					.sendEmptyMessage(R.id.mezzofanti_startCamera);
			Log.v(TAG, "Call startCamera.");
		}
	}

	// @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		m_bHasSurface = false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------- The preferences menu
	 * --------------------------------------
	 * --------------------------------------------------
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		m_sPreferencesMenu = menu;

		menu.add(PREFERENCES_GROUP_ID, SETTINGS_ID, 0, R.string.menu_settings)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(PREFERENCES_GROUP_ID, HELP_ID, 0, R.string.menu_help).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(PREFERENCES_GROUP_ID, FEEDBACK_ID, 0, R.string.menu_feedback)
				.setIcon(android.R.drawable.ic_menu_send);
		menu.add(PREFERENCES_GROUP_ID, ABOUT_ID, 0, R.string.menu_about)
				.setIcon(android.R.drawable.ic_menu_info_details);

		if (m_bSdcardMounted == false)
			menu.setGroupVisible(PREFERENCES_GROUP_ID, false);

		return true;
	}

	/**
	 * Show/Hide the Options menu
	 * 
	 * @param val
	 *            true=show, false=hide
	 */
	private void ShowOptionsMenu(boolean val) {
		if (m_sPreferencesMenu == null)
			return;
		m_sPreferencesMenu.setGroupVisible(PREFERENCES_GROUP_ID, val);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS_ID: {
			OCR.ReadAvailableLanguages();
			StopCamera();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this, PreferencesActivity.class.getName());
			startActivity(intent);

			break;
		}

		case HELP_ID:
			AlertDialog.Builder builderH = new AlertDialog.Builder(this);
			builderH.setIcon(R.drawable.wizard_48);
			builderH.setTitle(getString(R.string.preferences_helpTitle));
			builderH.setMessage(getString(R.string.preferences_msg_help_step1)
					+ "\n" + getString(R.string.preferences_itwiz_url));
			builderH.setNegativeButton(R.string.preferences_button_cancel, null);
			builderH.show();
			break;

		case FEEDBACK_ID:
			Intent intent2 = new Intent(Intent.ACTION_SENDTO,
					Uri.parse("mailto:office@itwizard.ro"));
			intent2.putExtra("subject", "[MezzofantiFeedback]");
			intent2.putExtra("body", "");
			startActivity(intent2);
			break;

		case ABOUT_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.wizard_48);
			builder.setTitle(getString(R.string.preferences_aboutTitle));
			builder.setMessage(getString(R.string.preferences_msg_about) + "\n"
					+ getString(R.string.preferences_itwiz_url));
			builder.setPositiveButton(
					getString(R.string.preferences_button_open_browser),
					mAboutListener);
			builder.setNegativeButton(
					getString(R.string.preferences_button_cancel), null);
			builder.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * User requests to connect to company website.
	 */
	private final DialogInterface.OnClickListener mAboutListener = new DialogInterface.OnClickListener() {
		public void onClick(android.content.DialogInterface dialogInterface,
				int i) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(getString(R.string.preferences_itwiz_url)));
			startActivity(intent);
		}
	};

	/*
	 * --------------------------------------------------------------------------
	 * -------------- Camera
	 * ----------------------------------------------------
	 * ------------------------------------
	 */

	/**
	 * Set the camera surface holder.
	 */
	private void SetCameraSurfaceHolder(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null || CameraManager.get() == null)
			return;

		CameraManager.get().SetSurfaceHolder(surfaceHolder);
	}

	/**
	 * Initialize the camera, open the driver.
	 * 
	 * @param surfaceHolder
	 *            the local surface holder.
	 */
	private void InitCamera(SurfaceHolder surfaceHolder) {
		if (CameraManager.get() == null)
			return;

		m_bPreviewReady = true;
		CameraManager.get().OpenDriver(surfaceHolder);
		CameraManager.get().StartPreview();
	}

	/**
	 * Initialize the camera, open the driver (no params), we assume the surface
	 * holder was set apriori with SetCameraSurfaceHolder.
	 */
	private void InitCamera() {
		Log.v(TAG, "InitCamera: start");
		if (CameraManager.get() == null)
			return;

		Log.v(TAG, "InitCamera: OpenDriver");

		m_bPreviewReady = true;
		if (CameraManager.get().OpenDriver()) {
			Log.v(TAG, "InitCamera: StartPreview");
			CameraManager.get().StartPreview();
		}
		Log.v(TAG, "InitCamera: end");
	}

	/**
	 * Stop the camera preview and driver.
	 */
	private void StopCamera() {
		CameraManager.get().StopPreview();
		CameraManager.get().CloseDriver();
		m_bPreviewReady = false;
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------- Keyboard
	 * --------------------------------------------------
	 * --------------------------------------
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		switch (keyCode) {
		case KeyEvent.KEYCODE_FOCUS: {
			if (event.getRepeatCount() == 0)
				return RequestCameraFocus();
			return true;
		}

		case KeyEvent.KEYCODE_CAMERA: {
			if (event.getRepeatCount() == 0)
				return RequestCameraTakePicture();
			return true;
		}
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Request the camera focus
	 * 
	 * @return if function call succeeded
	 */
	private boolean RequestCameraFocus() {
		String ss[] = OCR.getLanguagesNative();
		if (ss == null || ss.length == 0) {
			ShowLanguageMissingAlertDialog();
			return false;
		}

		if (!m_bSdcardMounted)
			return false;
		if (m_bOCRInProgress == true)
			return false;

		m_bFocusButtonPressed = true;
		m_clCapture.DrawFocusIcon(true, m_bHorizontalDisplay);
		CameraManager.get().RequestCameraFocus(m_MezzofantiMessageHandler);
		CameraManager.get().RequestAutoFocus();
		return true;
	}

	/**
	 * Request camera to take the picture
	 * 
	 * @return if function call succeeded
	 */
	private boolean RequestCameraTakePicture() {
		if (!m_bSdcardMounted)
			return false;
		if (m_bOCRInProgress == true)
			return false;

		m_bOCRInProgress = true;
		if (m_bPreviewReady) {
			m_bHorizDispAtPicTaken = m_bHorizontalDisplay;

			CompareTime(TAG + "request a picture");
			m_clCapture.DrawFocused(false, false);
			m_clCapture.DrawFocusIcon(false, m_bHorizontalDisplay);
			CameraManager.get().RequestPicture(m_MezzofantiMessageHandler);
			CameraManager.get().GetPicture();
			if (m_bLineMode)
				m_clCapture
						.ShowWaiting(getString(R.string.mezzofanti_capturelayout_takingpicture));

			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_FOCUS:
			m_bFocusButtonPressed = false;
			m_clCapture.DrawFocusIcon(false, m_bHorizontalDisplay);
			m_clCapture.DrawFocused(false, false);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void onClick(View arg0) {
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------------------- OCR Thread
	 * ------------------------------------
	 * ----------------------------------------------------------------
	 */

	/**
	 * Show a progress bar and start the OCR thread.
	 * 
	 * @param bm
	 *            the bitmap to be OCR-ized.
	 */
	public void DoOCRJob(Bitmap bm) {
		m_bmOCRBitmap = bm;
		if (bm == null)
			return;

		if (!m_bLineMode) {
			m_pdOCRInProgress = ProgressDialog
					.show(this,
							this.getString(R.string.mezzofanti_ocr_processing_title),
							this.getString(R.string.mezzofanti_ocr_processing_body_begin)
									+ " "
									+ OCR.mConfig.GetLanguageMore()
									+ " "
									+ this.getString(R.string.mezzofanti_ocr_processing_body_end),
							true, true);
			m_pdOCRInProgress.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
		} else {
			m_clCapture
					.ShowWaiting(getString(R.string.mezzofanti_capturelayout_processing)
							+ " "
							+ OCR.mConfig.GetLanguageMore()
							+ " "
							+ getString(R.string.mezzofanti_capturelayout_processing2));
		}

		Thread theOCRthread = new Thread(this);
		theOCRthread.start();
	}

	/**
	 * start the OCR thread
	 */
	public void run() {
		CompareTime(TAG + "STARTING ocr processing");

		// called by the OCR thread
		int iPicWidth = m_bmOCRBitmap.getWidth();
		int iPicHeight = m_bmOCRBitmap.getHeight();
		int[] iImage = null;
		try {
			iImage = new int[iPicWidth * iPicHeight];
			Log.v(TAG, "allocated img buffer: " + iPicWidth + ", " + iPicHeight);
			m_bmOCRBitmap.getPixels(iImage, 0, iPicWidth, 0, 0, iPicWidth,
					iPicHeight);
			Log.v(TAG, "pix1=" + Integer.toHexString(iImage[0]));
		} catch (Exception ex) {
			Log.v(TAG, "exception: run():" + ex.toString());
			m_bmOCRBitmap = null;
			System.gc();
		}

		if (iImage != null) {
			String m_sOCRResult = OCR.get().ImgOCRAndFilter(iImage, iPicWidth,
					iPicHeight, m_bHorizDispAtPicTaken, m_bLineMode);
			Log.v(TAG, "ocr done text= [" + m_sOCRResult + "]");
			// force free the mem
			iImage = null;
			m_bmOCRBitmap = null;
			System.gc();

			// bad results, get the (internal) image
			OCR.get().SaveMeanConfidence();

			Log.v(TAG, "starting results handler");
		} else {
			System.gc();
			m_bOCRInProgress = false;
			m_MezzofantiMessageHandler
					.sendEmptyMessage(R.id.mezzofanti_restartCaptureMode);
			return;
		}

		m_MezzofantiMessageHandler
				.sendEmptyMessage(R.id.mezzofanti_ocrFinished);
		Log.i(TAG, "pcjpg - finish startPreview()");

		CompareTime(TAG + "finished the ocr processing");
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------------------- Events Handler
	 * --------------------------------
	 * --------------------------------------------------------------------
	 */

	private Handler m_MezzofantiMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ---------------------------------------------------------------
			case R.id.installactivity_killApp:
				Log.v(TAG, "Killed by installer.");
				android.os.Process.killProcess(android.os.Process.myPid());
				break;

			// ---------------------------------------------------------------
			case R.id.mezzofanti_restartCaptureMode:
				// we had a problem in the OCR thread, so we get back to capture
				// mode
				// (mainly this is due to OutOfMemory exceptions)

				m_bmOCRBitmap = null;
				System.gc();

				if (m_bLineMode)
					m_clCapture.ShowWaiting("");
				else {
					if (m_pdOCRInProgress != null)
						m_pdOCRInProgress.dismiss();
				}

				break;

			// ---------------------------------------------------------------
			case R.id.mezzofanti_startCamera:
				InitCamera();
				if (!m_bSdcardMounted)
					UpdateStorageHint(true);
				break;

			// ---------------------------------------------------------------
			case R.id.cameramanager_requestpicture:

				Log.v(TAG, "handleMessage() R.id.decode");
				CompareTime(TAG + "in handler, just received the picture");

				// do ocr
				Bitmap mBitmap = null;
				try {
					// save the file on disk
					// only for debug
					boolean debug = true;
					if (debug) {
						try {
							FileOutputStream fs = new FileOutputStream(
									DATA_PATH + "img.jpg");
							fs.write((byte[]) msg.obj, 0,
									((byte[]) msg.obj).length);
							fs.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mBitmap = BitmapFactory.decodeByteArray((byte[]) msg.obj,
							0, ((byte[]) msg.obj).length);
					msg.obj = null;
					System.gc();

					Log.v(TAG,
							"w=" + mBitmap.getWidth() + " h="
									+ mBitmap.getHeight());

					if (m_bLineMode) { // we crop just the image of interest
						// need use relative values
						// mBitmap = Bitmap.createBitmap(mBitmap, 256, 768/2-30,
						// 512, 60, null, false);
						Rect frame = CameraManager.get().GetCaptureRect(
								m_bLineMode);
						int widthPadding = 20;
						int heightPadding = 10;
						mBitmap = Bitmap.createBitmap(mBitmap, frame.left
								- widthPadding, frame.top - heightPadding,
								frame.right - frame.left + 2 * widthPadding,
								frame.bottom - frame.top + 2 * heightPadding);

						if (debug) {
							try {
								ByteArrayOutputStream bytes = new ByteArrayOutputStream();
								mBitmap.compress(Bitmap.CompressFormat.JPEG,
										90, bytes);

								File f = new File(DATA_PATH + "frame_img.jpg");
								f.createNewFile();
								// write the bytes in file
								FileOutputStream fo = new FileOutputStream(f);
								fo.write(bytes.toByteArray());
								fo.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					// otherwise, we use all image
				} catch (Throwable th) {
					Log.v(TAG,
							"exception: handler-cmrequestpic: " + th.toString());
					m_bOCRInProgress = false;
					if (m_bLineMode)
						m_clCapture.ShowWaiting("");
					else
						m_MezzofantiMessageHandler
								.sendEmptyMessage(R.id.mezzofanti_restartCaptureMode);
					break;
				}

				CompareTime(TAG + "starting the thread");

				OCR.Initialize();
				OCR.get().SetLanguage(OCR.mConfig.GetLanguage());

				DoOCRJob(mBitmap);

				break;

			// ---------------------------------------------------------------
			case R.id.mezzofanti_ocrFinished:
				m_bOCRInProgress = false;
				if (!m_bLineMode) {
					m_pdOCRInProgress.dismiss();
					System.gc();

					StopCamera();
					StartResultsActivity();
				} else {
					Log.v(TAG, "before processing results");
					String padding = " ";
					if (m_sOCRResultLineMode.length() == 0)
						padding = "";

					String s = OCR.m_ssOCRResult.toString().replaceAll(
							"[\r\n]+", "");
					while (s.length() > 0 && s.charAt(0) == ' ')
						s = s.substring(1);
					while (s.length() > 0 && s.charAt(s.length() - 1) == ' ')
						s = s.substring(0, s.length() - 1);

					m_sOCRResultLineMode = m_sOCRResultLineMode.concat(padding
							+ s);

					m_clCapture.ShowWaiting("");
					m_clCapture.SetText(m_sOCRResultLineMode);
				}
				OCR.get().OCRClean();
				break;

			// ---------------------------------------------------------------
			case R.id.cameramanager_focus_succeded:
				if (!m_bFocusButtonPressed)
					return;
				m_clCapture.DrawFocused(true, true);
				PlaySoundOnFocus();

				if (m_bScreenRequestPicture)
					RequestCameraTakePicture();
				m_bScreenRequestPicture = false;
				break;

			// ---------------------------------------------------------------
			case R.id.cameramanager_focus_failed:
				if (!m_bFocusButtonPressed)
					return;
				m_clCapture.DrawFocused(true, false);
				Vibrate();
				m_bScreenRequestPicture = false;
				break;

			default:
				break;

			}

			super.handleMessage(msg);
		}

	};

	private void ShowLanguageMissingAlertDialog() {
		AlertDialog.Builder builderH = new AlertDialog.Builder(this);
		builderH.setIcon(R.drawable.alert32);
		builderH.setTitle(getString(R.string.mezzofanti_nolanginstalled_title));
		builderH.setMessage(getString(R.string.mezzofanti_nolanginstalled_body));
		builderH.setPositiveButton(R.string.mezzofanti_nolanginstalled_ok, null);
		builderH.show();
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------- Starting the activities
	 * ------------------------------------
	 * ----------------------------------------------------
	 */
	/**
	 * Starts the results activity
	 */
	private void StartResultsActivity() {
		if (m_bSdcardMounted) {
			// Intent intent = new Intent(Intent.ACTION_VIEW);
			// Bundle bun = new Bundle();
			// // if not in line-mode, the activity will get the results
			// directly from the OCR results
			// bun.putString("LineModeText", m_sOCRResultLineMode);
			// bun.putBoolean("bLineMode", m_bLineMode);
			// intent.putExtras(bun);
			// intent.setClassName(this, ResultsActivity.class.getName());
			// startActivity(intent);

		}

		// return scan result
		// if (m_sOCRResultLineMode == null || m_sOCRResultLineMode.length() ==
		// 0){
		// return;
		// }

		Intent data = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("content", m_sOCRResultLineMode);
		bundle.putBoolean("bLineMode", m_bLineMode);
		data.putExtras(bundle);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	/**
	 * Start the InstallActivity if possible and needed.
	 */
	/*
	 * private void StartInstallActivity() { if (m_bSdcardMounted && (
	 * AssetsManager.IsInstalled()==false || (m_bSkipIntroAtStartup==false &&
	 * !m_bIntroWasDisplayedAtStartup) ) ) { // install the languages if needed,
	 * create directory structure (one time) Intent intent = new
	 * Intent(Intent.ACTION_VIEW);
	 * InstallActivity.SetParentMessageHandler(m_MezzofantiMessageHandler);
	 * intent.setClassName(this, InstallActivity.class.getName());
	 * startActivity(intent); m_bIntroWasDisplayedAtStartup = true; // only
	 * "true" at startup } }
	 */

	/*
	 * --------------------------------------------------------------------------
	 * -------------- SDCard functions
	 * ------------------------------------------
	 * ----------------------------------------------
	 */

	/**
	 * Start the InstallActivity if possible and needed.
	 */
	private void StartInstallActivity() {
		if (m_bSdcardMounted
				&& (AssetsManager.IsInstalled() == false || (m_bSkipIntroAtStartup == false && !m_bIntroWasDisplayedAtStartup))) {
			// install the languages if needed, create directory structure (one
			// time)
			Intent intent = new Intent(Intent.ACTION_VIEW);
			InstallActivity.SetParentMessageHandler(m_MezzofantiMessageHandler);
			intent.setClassName(this, InstallActivity.class.getName());
			startActivity(intent);
			m_bIntroWasDisplayedAtStartup = true; // only "true" at startup
		}
	}

	/**
	 * Displays the sdcard-state message.
	 * 
	 * @param show
	 *            true=display, false=remove
	 */
	private void UpdateStorageHint(boolean show) {
		String state = Environment.getExternalStorageState();
		int resID = 0;

		if (state == Environment.MEDIA_CHECKING)
			resID = R.string.mezzofanti_sdcard_preparing;
		else
			resID = R.string.mezzofanti_sdcard_unmount;

		if (show) {
			if (m_oshStorageHint == null) {
				m_oshStorageHint = OnScreenHint.makeText(this, resID);
			} else {
				m_oshStorageHint.setText(resID);
			}
			m_oshStorageHint.show();
		} else if (m_oshStorageHint != null) {
			m_oshStorageHint.cancel();
			m_oshStorageHint = null;
		}
	}

	/**
	 * The sdcard event for the broadcast receiver
	 */
	private final BroadcastReceiver m_brSDcardEvent = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.v(TAG, "SD card activity received");
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				// SD card available
				// activate mezzofanti
				m_bSdcardMounted = true;
				ShowOptionsMenu(true);
				StartInstallActivity();

				OCR.Initialize();
				OCR.get().SetLanguage(OCR.mConfig.GetLanguage());
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_CHECKING)) {
				// SD card unavailable
				UpdateStorageHint(true);
				m_bSdcardMounted = false;
				ShowOptionsMenu(false);
			} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
				UpdateStorageHint(false);
				Toast.makeText(Mezzofanti.this,
						R.string.mezzofanti_sdcard_wait, 5000);
			} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
				UpdateStorageHint(false);
			}
		}

	};

	/**
	 * Get the SDCard state from the Environment.
	 */
	public static boolean CheckSDCardState() {
		boolean ret = true;
		String sdcardState = Environment.getExternalStorageState();
		if (sdcardState.compareTo(Environment.MEDIA_MOUNTED) != 0) {
			Log.v(TAG, "error: sdcard not mounted! [" + sdcardState + "]");
			ret = false;
		}
		return ret;
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------- Orientation listner
	 * ----------------------------------------
	 * ------------------------------------------------
	 */

	/**
	 * Creates the orientation listner, in order to check for
	 * vertical/horizontal picture capture.
	 */
	private void CreateOrientationListener(Context context) {
		m_OEListener = new OrientationEventListener(context,
				SensorManager.SENSOR_DELAY_UI) {
			public void onOrientationChanged(int orientation) {
				if (m_bSdcardMounted == false)
					return;

				if ((orientation >= 0 && orientation <= 10)
						|| (orientation >= 350 && orientation <= 360)) {
					if (m_bHorizontalDisplay) {
						m_bHorizontalDisplay = false;
					}

				} else {
					if (orientation >= 260 && orientation <= 280) {
						if (!m_bHorizontalDisplay) {
							m_bHorizontalDisplay = true;
						}
					} else if (orientation == -1) {
					} else {
					}
				}
			} // end public void onOrientationChanged (int orientation)
		}; // end mListener = new ...
	}

	/**
	 * Enable/disable orientation listener.
	 * 
	 * @param enable
	 *            true/false
	 */
	public void OrientationEnable(boolean enable) {
		if (m_OEListener == null)
			return;

		if (enable)
			m_OEListener.enable();
		else
			m_OEListener.disable();
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------------- Aux functions
	 * ----------------------------------------------
	 * ------------------------------------------
	 */

	/**
	 * Capture-mode: Refresh the bottom status bar.
	 * 
	 * @param angle
	 *            the orientation angle.
	 */
	private void RefreshStatusBar(String angle) {
		TextView tbox = (TextView) findViewById(R.id.mezzofanti_status_text_view);
		if (!m_bLineMode) {
			tbox.setText("  << "
					+ getString(R.string.mezzofanti_capturemode_statustext_switchtosmall));
		} else {
			tbox.setText("  << "
					+ getString(R.string.mezzofanti_capturemode_statustext_switchtolarge)
					+ "\n"
					+ "\t\t\t\t\t\t\t\t"
					+ getString(R.string.mezzofanti_capturemode_statustext_switchtolarge2)
					+ " >>" + "  ");
		}

	}

	/**
	 * Play a predefined sound, when camera focused.
	 */
	private void PlaySoundOnFocus() {
		ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
		tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
	}

	/**
	 * Vibrate mobile, when camera not-focused.
	 */
	private void Vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	private static long m_lTime = 0;

	public static void CompareTime(String status) {
		long prevtime = m_lTime;
		Calendar now = Calendar.getInstance();
		long currenttime = now.getTimeInMillis();

		Log.v(TAG, "---------------------");
		Log.v(TAG, "Status: " + status);
		Log.v(TAG, "Compare prev=" + m_lTime + " to current=" + currenttime
				+ " diff=" + (currenttime - prevtime));
		Log.v(TAG, "---------------------");
		m_lTime = currenttime;
	}

	/**
	 * Adds the 3 buttons needed in small-capture-mode
	 */
	private void CreateStatusbarButtonsCaptureMode() {
		// create the status bar buttons
		m_btSwitch = (ImageButton) findViewById(R.id.mezzofanti_button_switch);
		m_btSwitch.setBackgroundResource(R.drawable.switch_32);

		m_btDelOne = (ImageButton) findViewById(R.id.mezzofanti_button_delone);
		m_btGotoResults = (ImageButton) findViewById(R.id.mezzofanti_button_gotoresults);

		m_btDelOne.setBackgroundResource(R.drawable.delone_32);
		m_btGotoResults.setBackgroundResource(R.drawable.gotoresults_32);

		m_btSwitch.setOnClickListener(new OnClickListener() {
			public void onClick(View clickedView) {
				Log.v(TAG, "switch button: onClick line_mode = " + m_bLineMode);
				m_bLineMode = !m_bLineMode;
				m_clCapture.SetLineMode(m_bLineMode);

				RefreshStatusBar("");

				if (m_bLineMode) {
					CameraManager.SetImgDivisor(2);
					CameraManager.get().SetCameraParameters();
					ShowLineModeButtons(true);
				} else {
					CameraManager.SetImgDivisor(OCR.mConfig.GetImgDivisor());
					CameraManager.get().SetCameraParameters();
					ShowLineModeButtons(false);
					m_sOCRResultLineMode = "";
					m_clCapture.SetText("");
				}
			}
		});

		m_btDelOne.setOnClickListener(new OnClickListener() {
			public void onClick(View clickedView) {
				Log.v(TAG, "m_btDelOne: onClick line_mode = " + m_bLineMode);
				int iLastSpace = m_sOCRResultLineMode.lastIndexOf(' ');
				if (iLastSpace <= 0)
					m_sOCRResultLineMode = "";
				else
					m_sOCRResultLineMode = m_sOCRResultLineMode.substring(0,
							iLastSpace);
				m_clCapture.SetText(m_sOCRResultLineMode);
			}
		});

		m_btGotoResults.setOnClickListener(new OnClickListener() {
			public void onClick(View clickedView) {
				Log.v(TAG, "m_btGotoResults: onClick line_mode = "
						+ m_bLineMode);
				StartResultsActivity();
				System.gc();
			}
		});

	}

	/**
	 * Show/hide the small-capture-mode buttons
	 * 
	 * @param val
	 *            show/hide
	 */
	private void ShowLineModeButtons(boolean val) {
		if (val) {
			m_btDelOne.setVisibility(View.VISIBLE);
			m_btGotoResults.setVisibility(View.VISIBLE);
		} else {
			m_btDelOne.setVisibility(View.GONE);
			m_btGotoResults.setVisibility(View.GONE);
		}

		m_clCapture.invalidate();

	}

} // end class Mezzofanti

