/*
 * Copyright (C) 2009 IT Wizard.
 * http://www.itwizard.ro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Class description:
 * 		responsible for camera management: driver initialization, taking pictures etc.
 */

package com.itwizard.mezzofanti;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.view.View.OnTouchListener;
import com.stubhub.ticketscan.R;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 */
public final class CameraManager {

	private static final String TAG = "MLOG: CameraManager.java: ";

	private static byte m_cImgDivisor = 2; // given the limited memory space, we
											// cannot allocate memory for all
											// the image
											// thus we lower a bit the
											// resolution by a factor of 2/4

	private static CameraManager m_CameraManager; // the camera manager itself
	private Camera m_Camera; // the camera
	private final Context m_Context; // screen context
	private Point m_ptScreenResolution; // the screen resolution
	// private Rect m_FramingRect; // the framing rectangle
	private boolean m_bInitialized; // is the driver initialized
	private boolean m_bPreviewing; // is camera in preview mode
	private Handler m_ParentMessageHandler; // the parent's message handler
	private SurfaceHolder m_ParentSurfaceHolder = null; // the parent's surface
														// holder
	 private static final String TAG1 = "Touch";
	   // These matrices will be used to move and zoom image
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

	/**
	 * called when jpeg-image ready, just send to the parent handler the whole
	 * image to be processed
	 */
	Camera.PictureCallback m_PictureCallbackJPG = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {

			Log.i(TAG, "pcjpg - started");
			Mezzofanti.CompareTime(TAG + "just started picture callback");
			if (data == null) {
				Log.i(TAG, "data is null");
			} else {
				Message message = m_ParentMessageHandler.obtainMessage(
						R.id.cameramanager_requestpicture, data);
				message.sendToTarget();
				m_ParentMessageHandler = null;
				Log.i(TAG, "pcjpg - finish");
				m_Camera.startPreview();
				Mezzofanti.CompareTime(TAG + "just sent picture to handler");
			}

		}
	};

	/**
	 * save the parent handler, in order to process the image-request
	 */
	public void RequestPicture(Handler handler) {
		if (m_Camera != null && m_bPreviewing) {
			m_ParentMessageHandler = handler;
		}
	}

	/**
	 * called on autofocus
	 */
	private Camera.AutoFocusCallback m_AutoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				Log.v(TAG, " Focus succeded.");
				m_ParentMessageHandler
						.sendEmptyMessage(R.id.cameramanager_focus_succeded);
			} else {
				Log.v(TAG, " Focus failed.");
				m_ParentMessageHandler
						.sendEmptyMessage(R.id.cameramanager_focus_failed);
			}
		}
	};

	/**
	 * Save the parent message handler.
	 * 
	 * @param handler
	 *            parent message handler
	 */
	public void RequestCameraFocus(Handler handler) {
		if (m_Camera != null && m_bPreviewing)
			m_ParentMessageHandler = handler;
	}

	/**
	 * Allocate the camera manager
	 */
	public static void Initialize(Context context) {
		if (m_CameraManager == null) {
			m_CameraManager = new CameraManager(context);
		}
	}

	/**
	 * set the local image divisor
	 */
	public static void SetImgDivisor(int imgDivisor) {
		if (imgDivisor != 1 && imgDivisor != 2 && imgDivisor != 4)
			m_cImgDivisor = (byte) 2;
		else
			m_cImgDivisor = (byte) imgDivisor;
	}

	/**
	 * Retrieve the private camera manager
	 */
	public static CameraManager get() {
		return m_CameraManager;
	}

	/**
	 * constructor
	 */
	private CameraManager(Context context) {
		m_Context = context;
		GetScreenResolution();
		m_Camera = null;
		m_bInitialized = false;
		m_bPreviewing = false;
	}

	/**
	 * set the parent surface holder
	 */
	public void SetSurfaceHolder(SurfaceHolder holder) {
		m_ParentSurfaceHolder = holder;
	}

	/**
	 * open the camera driver, using the saved parent-surface-holder
	 * 
	 * @return a boolean variable indicating if the open-driver procedure
	 *         succeeded
	 */
	public boolean OpenDriver() {
		if (m_ParentSurfaceHolder == null)
			return false;

		if (m_Camera == null) {
			m_Camera = Camera.open();
			try {
				m_Camera.setPreviewDisplay(m_ParentSurfaceHolder);
			} catch (IOException e) {
				Log.v(TAG, e.toString());
				return false;
			}

			if (!m_bInitialized) {
				m_bInitialized = true;
				GetScreenResolution();
			}

			SetCameraParameters();
		}
		return true;
	}

	/**
	 * open camera driver using a parameter surface-holder
	 * 
	 * @return a boolean variable indicating if the open-driver procedure
	 *         succeeded
	 */
	public void OpenDriver(SurfaceHolder holder) {
		if (m_Camera == null) {
			m_Camera = Camera.open();
			try {
				m_Camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.v(TAG, e.toString());
			}

			if (!m_bInitialized) {
				m_bInitialized = true;
				GetScreenResolution();
			}

			SetCameraParameters();

		}
	}

	/**
	 * close the camera driver
	 */
	public void CloseDriver() {
		if (m_Camera != null) {
			m_Camera.release();
			m_Camera = null;
			m_ParentSurfaceHolder = null;
		}
	}

	/**
	 * start camera preview mode
	 */
	public void StartPreview() {
		if (m_Camera != null && !m_bPreviewing) {
			m_Camera.startPreview();
			m_bPreviewing = true;
		}
	}

	/**
	 * stop camera preview mode
	 */
	public void StopPreview() {
		if (m_Camera != null && m_bPreviewing) {
			m_Camera.setPreviewCallback(null);
			m_Camera.stopPreview();
			m_bPreviewing = false;
		}
	}

	/**
	 * set the camera auto-focus callback
	 */
	public void RequestAutoFocus() {
		if (m_Camera != null && m_bPreviewing) {
			m_Camera.autoFocus(m_AutoFocusCallback);
		}
	}

	
	private Size previewSize;

	private Size pictureSize;

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the text. The actual captured image should be a bit larger
	 * than indicated because they might frame the shot too tightly. This target
	 * helps with alignment as well as forces the user to hold the device far
	 * enough away to ensure the image will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public Rect GetFramingRect(boolean linemode) {
		Rect getRect = GetRect(linemode, m_ptScreenResolution.x,
				m_ptScreenResolution.y);
	
		Log.w(TAG, "GetFramingRect:" + getRect);
		return getRect;
	}

	private Rect GetRect(boolean linemode, int totalWidth, int totalHeight) {
		Rect m_FramingRect;
		int border = 0;
		if (linemode) {
			int height = (int) (totalHeight * scanHeightRatio);
			int width = (int) (totalWidth * scanWidthRatio);

			m_FramingRect = new Rect((totalWidth - width) / 2, totalHeight / 2
					- height / 2, (totalWidth + width) / 2, totalHeight / 2
					+ height / 2);
		
			 
		} else {
			m_FramingRect = new Rect(border, border, totalWidth - 2 * border,
					totalHeight - 2 * border);
		}
        
		return m_FramingRect;
	}



	public Rect GetCaptureRect(boolean linemode) {
	//TODO fix it, in case of different width height ratio
//	 ,
//	 m_ptScreenResolution.y
		double Picture_Radio= 1d * pictureSize.width/pictureSize.height;
		double Screen_Radio= 1d * m_ptScreenResolution.x/m_ptScreenResolution.y;
	    double ScreenToPictureHight=0;
	    double ScreenToPictureWidth=0;
	    
		if(Picture_Radio<Screen_Radio)
	    {
			double mul=1d*pictureSize.width/m_ptScreenResolution.x;
			ScreenToPictureHight=m_ptScreenResolution.y*Picture_Radio*mul;
			Rect getRect = GetRect(linemode, (int)(pictureSize.width), (int)(ScreenToPictureHight));
			Log.w(TAG, "GetCaptureRect:" + getRect);
			return getRect;
	    }
		else
		{	double mul=1d*pictureSize.height/m_ptScreenResolution.y;
		     ScreenToPictureHight=m_ptScreenResolution.y*Picture_Radio*mul;
			ScreenToPictureWidth=m_ptScreenResolution.x*Picture_Radio;
			Rect getRect = GetRect(linemode, (int)(ScreenToPictureWidth), (int)(pictureSize.height));
			Log.w(TAG, "GetCaptureRect:" + getRect);
			return getRect;
		}
		
		
	}

   
	private static final float DEFAULT_WIDTH_RATIO = 0.5f;
	private static final float DEFAULT_HEIGHT_RATIO = 0.1f;

	private float scanWidthRatio = DEFAULT_WIDTH_RATIO;
	private float scanHeightRatio = DEFAULT_HEIGHT_RATIO;

	public void setScanHeightRatio(float heightRatio) {
		this.scanHeightRatio = heightRatio;
	}

	public void setScanWidthRatio(float widthRatio) {
		this.scanWidthRatio = widthRatio;
	}
    public float GetScanWidthRatio()
    {
    	return this.scanWidthRatio;
    }
	
    public float GetScanHeightRation()
    {
    	return this.scanHeightRatio;
    }
	public void resetScanSize() {
		scanWidthRatio = DEFAULT_WIDTH_RATIO;
		scanHeightRatio = DEFAULT_HEIGHT_RATIO;
	}

	/**
	 * take a picture, and set the jpg callback
	 */
	public void GetPicture() {
		m_Camera.takePicture(null, null, m_PictureCallbackJPG);
	}

	/**
	 * Sets the camera up to take preview images which are used for both preview
	 * and decoding.
	 */
	public void SetCameraParameters() {
		if (m_ptScreenResolution == null)
			return;
		Camera.Parameters parameters = m_Camera.getParameters();

		// XXX
		List<Size> supportedPreviewSizes = parameters
				.getSupportedPreviewSizes();

		previewSize = null;
		for (Size size : supportedPreviewSizes) {
			if (size.width == m_ptScreenResolution.x
					&& size.height == m_ptScreenResolution.y) {
				previewSize = size;
			}
		}

		if (previewSize == null) {
			previewSize = supportedPreviewSizes.get(supportedPreviewSizes
					.size() / 2);
		}

		parameters.setPreviewSize(previewSize.width, previewSize.height);

		List<Size> supportedPictureSizes = parameters
				.getSupportedPictureSizes();
		// parameters.setPictureSize(2048/m_cImgDivisor, 1536/m_cImgDivisor);

		int widthHeightRatio = m_ptScreenResolution.x / m_ptScreenResolution.y;

		pictureSize = null;

		for (Size size : supportedPictureSizes) {

			if (size.width < m_ptScreenResolution.x
					|| size.height < m_ptScreenResolution.y) {
				break;
			}

			if (Math.abs(size.width / size.height - widthHeightRatio) < 0.01f) {
				pictureSize = size;
				break;
			}
		}

		if (pictureSize == null) {
			Log.w(TAG,
					"Not found PictureSize the same widthHeightRatio with PreviewSize");
			pictureSize = supportedPictureSizes.get(supportedPictureSizes
					.size() / 2);
		}

		parameters.setPictureSize(pictureSize.width, pictureSize.height);

		Log.w(TAG, "screenSize:" + m_ptScreenResolution.x + ","
				+ m_ptScreenResolution.y);

		Log.w(TAG, "previewSize:" + previewSize.width + ","
				+ previewSize.height);

		Log.w(TAG, "pictureSize:" + pictureSize.width + ","
				+ pictureSize.height);

		m_Camera.setParameters(parameters);
		Log.v(TAG, parameters.flatten());
	}

	/**
	 * @return the screen resolution
	 */
	private Point GetScreenResolution() {
		if (m_ptScreenResolution == null) {
			WindowManager manager = (WindowManager) m_Context
					.getSystemService(Context.WINDOW_SERVICE);
			
			Display display = manager.getDefaultDisplay();
			// XXX cause is landscape, need assure width is bigger than height
			m_ptScreenResolution = new Point(
					display.getHeight() > display.getWidth() ? display.getHeight()
							: display.getWidth(),
					display.getHeight() < display.getWidth() ? display
							.getHeight() : display.getWidth());
		}
		return m_ptScreenResolution;
	}

}
