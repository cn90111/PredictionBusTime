package com.example.predictionbustime;

import method.MenuSpinnerListener;
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
		
		listAdapter = new ArrayAdapter(this,R.layout.myspinner,list);
		spinnerBusID.setAdapter(listAdapter);
		
//		Intent intent = this.getIntent();
//		Bundle bundle = intent.getExtras();
//		user.setUUID(bundle.getString("UUID"));
		
		list = getResources().getStringArray(R.array.menu);
		listAdapter = new ArrayAdapter(this,
				R.layout.myspinner, list);
		menuSpinner.setAdapter(listAdapter);
//		menuSpinner.setOnItemSelectedListener(
//				new MenuSpinnerListener(signButton,menuSpinner,user));
		
		menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					final int position, long id) 
			{
				// TODO Auto-generated method stub
				switch(parent.getSelectedItem().toString())
				{
					case "登出":
						System.out.println("登出");
						signButton.setVisibility(View.VISIBLE);
						menuSpinner.setVisibility(View.GONE);
						menuSpinner.setSelection(0);
						user.setUUID("");
						break;

					case "更改密碼":
						System.out.println("更改密碼");
						Intent intent = new Intent();	
						intent.setClass(SearchRouteByIdLayout.this, ChangePasswordActivity.class);
//						Bundle bundle = new Bundle();
//					    bundle.putString("UUID", user.getUUID());
//					    intent.putExtras(bundle);
						startActivity(intent);
						break;

					case "刪除帳號":
						System.out.println("刪除帳號");
						break;
						
					default:
						System.out.println("程式錯誤");
						Log.e("error","MenuSpinnerListener,程式錯誤");
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
//				if(getInputIdEditText.getText().toString().equals(""))
//				{
//					
//				}
//				else
//				{
//					Intent intent = new Intent();
//					intent.setClass(SearchRouteByIdLayout.this, PrintSearchByIdResult.class);
//					Bundle bundle = new Bundle();
//					bundle.putString("Route", getInputIdEditText.getText().toString());
//					intent.putExtras(bundle);
//					startActivity(intent);
//				}
				
				Intent intent = new Intent();
				intent.setClass(SearchRouteByIdLayout.this, PrintSearchByIdResult.class);
				Bundle bundle = new Bundle();
				bundle.putString("Route", spinnerBusID.getSelectedItem().toString());
//				bundle.putString("UUID", user.getUUID());
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
				intent.setClass(SearchRouteByIdLayout.this, SignInActivity.class);
				startActivity(intent);
//				startActivityForResult(intent,REQUEST_CODE);
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
	
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//
//		
//		if (requestCode == REQUEST_CODE)
//		{
//			if (resultCode == RESULT_OK)
//			{
//				Bundle extras = data.getExtras();
//				if (extras != null)
//				{
//					user.setUUID(extras.getString("UUID"));
//					signButton.setVisibility(View.GONE);
//					menuSpinner.setVisibility(View.VISIBLE);
//				}
//			}
//		}
//	}
}
