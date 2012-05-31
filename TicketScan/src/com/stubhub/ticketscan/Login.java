package com.stubhub.ticketscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Login extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	
		Button Login_Button = (Button) findViewById(R.id.Login_Button);
		Login_Button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Login.this, ScanTicketActivity.class);
				startActivity(intent);
				// Login.this.finish();
			}
		});

	}
}