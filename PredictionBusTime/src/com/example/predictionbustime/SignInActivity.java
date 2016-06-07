package com.example.predictionbustime;

import chara.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends Activity
{
	EditText uuidEdit;
	EditText passwordEdit;
	Button sendButton,registeredButton,returnButton;
	
	User user = User.getUniqueUser();
	
	private final static int REQUEST_CODE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in2);
		
		uuidEdit = (EditText) findViewById(R.id.uuidEditText);
		passwordEdit = (EditText) findViewById(R.id.passwordEditText);
		
		sendButton = (Button) findViewById(R.id.sendButton);
		registeredButton = (Button) findViewById(R.id.registeredButton);
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
				
				if(uuidEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals(""))
				{
					
				}
				else
				{

//					Bundle bundle = new Bundle();
//					bundle.putString("UUID", uuidEdit.getText().toString());
//					Intent intent = new Intent();
//					intent.putExtras(bundle);
//					setResult(RESULT_OK, intent);
					user.setUUID(uuidEdit.getText().toString());
				    finish();
				}
			}
		});
		
		registeredButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SignInActivity.this, RegisteredActivity.class);
				startActivity(intent);
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
