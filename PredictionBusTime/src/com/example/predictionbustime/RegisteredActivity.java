package com.example.predictionbustime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisteredActivity extends Activity
{
	TextView outputView;
	EditText uuidEdit;
	EditText passwordEdit;
	EditText phoneNumberEdit;
	Button sendButton,returnButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registered2);
		
		outputView = (TextView) findViewById(R.id.outputView);
		
		uuidEdit = (EditText) findViewById(R.id.uuidEditText);
		passwordEdit = (EditText) findViewById(R.id.passwordEditText);
		phoneNumberEdit = (EditText) findViewById(R.id.phoneNumberEditText);
		
		sendButton = (Button) findViewById(R.id.sendButton);
		returnButton = (Button) findViewById(R.id.returnButton);
	}
	
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		
		sendButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(uuidEdit.getText().toString().equals("") || 
						passwordEdit.getText().toString().equals("") ||
						phoneNumberEdit.getText().toString().equals(""))
				{
					outputView.setText(R.string.dataCanNotNull);
				}
				else
				{

					Toast.makeText(RegisteredActivity.this, 
									R.string.registeredSuccess , 
									Toast.LENGTH_SHORT).show();
				    finish();
				}
			}
		});
		
		returnButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
