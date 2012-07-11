package com.stubhub.ticketscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class Login extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		final ImageButton sellButton = (ImageButton) findViewById(R.id.Sell_Button);
		sellButton.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sellButton.setBackgroundResource(R.drawable.sell_clicked);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					sellButton.setBackgroundResource(R.drawable.sell);
				}
				return false;
			}
		});
		sellButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sellButton.setBackgroundResource(R.drawable.sell_clicked);
				Intent intent = new Intent();
				intent.setClass(Login.this, ScanTicketActivity.class);
				startActivity(intent);
				sellButton.setBackgroundResource(R.drawable.sell);
			}
		});

	}
}