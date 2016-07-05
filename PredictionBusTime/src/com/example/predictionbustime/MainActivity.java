package com.example.predictionbustime;

import chara.User;
import method.MenuListener;
import method.MyToast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	MenuListener menuListener;
	
	private final static int REQUEST_CODE = 0;
	
	MyToast myToast = new MyToast(MainActivity.this);

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
				R.layout.myspinner, list);
		
		menuListener = new MenuListener(signButton,menuSpinner,MainActivity.this);
		
		menuSpinner.setAdapter(listAdapter);
		
		menuSpinner.setOnItemSelectedListener(menuListener);
		
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
				startActivity(intent);
			}
		});

		signButton.setOnClickListener(menuListener);
		
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
}