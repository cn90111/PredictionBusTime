package com.example.predictionbustime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ExplanationActivity extends Activity
{
	Button previousButton,nextPageButton;
	Button returnButton;
	
	TextView titleTextView,ContentTextView1,ContentTextView2,ContentTextView3;
	
	int count = 0;
	
	String[][] explanation;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explanation);
		returnButton = (Button) findViewById(R.id.returnButton);
		previousButton = (Button) findViewById(R.id.previousButton);
		nextPageButton = (Button) findViewById(R.id.nextPageButton);

		titleTextView = (TextView) findViewById(R.id.textView1);
		ContentTextView1 = (TextView) findViewById(R.id.textView2);
		ContentTextView2 = (TextView) findViewById(R.id.textView3);
		ContentTextView3 = (TextView) findViewById(R.id.textView4);
		
		explanation = new String[][]
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
		titleTextView.setText(explanation[count][0]);
		ContentTextView1.setText(explanation[count][1]);
		ContentTextView2.setText(explanation[count][2]);
		ContentTextView3.setText(explanation[count][3]);
		
		if(count<=0)
		{
			previousButton.setEnabled(false);
		}
		else if(count >= explanation.length-1)
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
