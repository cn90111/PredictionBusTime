package com.example.predictionbustime;

import chara.User;
import method.MenuSpinnerListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity
{
	Button searchBusByIDButton;
	Button signButton;
	Button explanationButton;

	Spinner menuSpinner;
	ArrayAdapter<String> listAdapter;
	String[] list;
	
	User user = User.getUniqueUser();
	
	private final static int REQUEST_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchBusByIDButton = (Button) findViewById(R.id.searchBusByID);
		signButton = (Button) findViewById(R.id.signButton);
		explanationButton = (Button) findViewById(R.id.explanationButton);
		menuSpinner = (Spinner) findViewById(R.id.menuSpinner);

		list = getResources().getStringArray(R.array.menu);
		listAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, list);
		menuSpinner.setAdapter(listAdapter);
		menuSpinner.setOnItemSelectedListener(
				new MenuSpinnerListener(signButton,menuSpinner,user));
		
		

	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();

		if(user.getUUID().equals(""))
		{
			menuSpinner.setVisibility(View.GONE);
			signButton.setVisibility(View.VISIBLE);
		}
		else
		{
			menuSpinner.setVisibility(View.VISIBLE);
			signButton.setVisibility(View.GONE);
		}
		
		searchBusByIDButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				Intent intent = new Intent();	
				intent.setClass(MainActivity.this, SearchRouteByIdLayout.class);
				Bundle bundle = new Bundle();
			    bundle.putString("UUID", user.getUUID());
			    intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		signButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SignInActivity.class);
				startActivityForResult(intent,REQUEST_CODE);
			}
		});
		
		explanationButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();	
				intent.setClass(MainActivity.this, ExplanationActivity.class);
				startActivity(intent);
			}
		});
	}

	public void msgToast(String msg)
	{
		// CKJ: to show the short message using TOAST UI Component
		Toast msgToast;
		msgToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		msgToast.setText(msg);
		msgToast.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		
		if (requestCode == REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				Bundle extras = data.getExtras();
				if (extras != null)
				{
					user.setUUID(extras.getString("UUID"));
					signButton.setVisibility(View.GONE);
					menuSpinner.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
