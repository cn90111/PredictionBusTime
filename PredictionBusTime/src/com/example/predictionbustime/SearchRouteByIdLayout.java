package com.example.predictionbustime;

import method.MenuListener;
import chara.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SearchRouteByIdLayout extends Activity
{
	Button yesButtonInSearchByIdLayout;
	Spinner spinnerBusID,menuSpinner;
//	EditText getInputIdEditText;
	Button signButton,returnButton;
	
	ArrayAdapter<String> listAdapter;
	User user = User.getUniqueUser();
	
	MenuListener menuListener;
	
	String[] list;
	
	private final static int REQUEST_CODE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_by_id_layout);
		
//		getInputIdEditText = (EditText) findViewById(R.id.getInputIdEditText);
		yesButtonInSearchByIdLayout = (Button) findViewById(R.id.yesButtonInSearchByIdLayout);
		signButton = (Button) findViewById(R.id.signButton);
		spinnerBusID = (Spinner) findViewById(R.id.spinnerBusID);
		menuSpinner = (Spinner) findViewById(R.id.menuSpinner);
		returnButton = (Button) findViewById(R.id.returnButton);
		
		list = getResources().getStringArray(R.array.can_run_route);
		
		menuListener = new MenuListener(signButton,menuSpinner,SearchRouteByIdLayout.this);
		
		listAdapter = new ArrayAdapter(this,R.layout.myspinner,list);
		spinnerBusID.setAdapter(listAdapter);
		
		list = getResources().getStringArray(R.array.menu);
		listAdapter = new ArrayAdapter(this,
				R.layout.myspinner, list);
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
		
		yesButtonInSearchByIdLayout.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				intent.setClass(SearchRouteByIdLayout.this, PrintSearchByIdResult.class);
				Bundle bundle = new Bundle();
				bundle.putString("Route", spinnerBusID.getSelectedItem().toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		signButton.setOnClickListener(menuListener);
		
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
