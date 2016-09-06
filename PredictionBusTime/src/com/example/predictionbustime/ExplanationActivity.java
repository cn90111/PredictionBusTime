package com.example.predictionbustime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExplanationActivity extends Activity
{
	Button previousButton,nextPageButton;
	Button returnButton;
	
	ListView ContentListView;
	
	int count = 0;
	
	String[][] explanationContentArray;
	String[] list;
	
	ArrayAdapter<String> listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explanation);
		returnButton = (Button) findViewById(R.id.returnButton);
		previousButton = (Button) findViewById(R.id.previousButton);
		nextPageButton = (Button) findViewById(R.id.nextPageButton);

		ContentListView = (ListView) findViewById(R.id.listView1);
		
		explanationContentArray  = new String[][]
		{
			getResources().getStringArray(R.array.explanationContent1),
			getResources().getStringArray(R.array.explanationContent2),
			getResources().getStringArray(R.array.explanationContent3)
		};
	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		
		refresh();
		
		returnButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		nextPageButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				count++;
				refresh();
			}
		});
		
		previousButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				count--;
				refresh();
			}
		});
	}
	
	public void refresh()
	{
		list = explanationContentArray[count];
		listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
		ContentListView.setAdapter(listAdapter);
		
		if(count<=0)
		{
			previousButton.setEnabled(false);
		}
		else if(count >= list.length-1)
		{
			nextPageButton.setEnabled(false);
		}
		else
		{
			previousButton.setEnabled(true);
			nextPageButton.setEnabled(true);
		}
	}
}
