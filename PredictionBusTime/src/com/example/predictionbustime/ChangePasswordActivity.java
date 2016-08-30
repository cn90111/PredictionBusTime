package com.example.predictionbustime;

import method.MyToast;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity
{
	EditText oldPasswordEdit;
	EditText newPasswordEdit,reIntputNewPasswordEdit;
	Button okButton,returnButton;
	MyToast myToast = new MyToast(ChangePasswordActivity.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password_main);
		
		oldPasswordEdit = (EditText) findViewById(R.id.oldPasswordEdit);
		newPasswordEdit = (EditText) findViewById(R.id.newPasswordEdit);
		reIntputNewPasswordEdit = (EditText) findViewById(R.id.reIntputNewPasswordEdit);
		okButton = (Button) findViewById(R.id.okButton);
		returnButton = (Button) findViewById(R.id.returnButton);
	}
	
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		
		okButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				if( checkOldPasswordCorrect() )
				{
					if(newPasswordEdit.getText().toString().
							equals(reIntputNewPasswordEdit.getText().toString()))
					{
						myToast.msgToast(ChangePasswordActivity.this.getString
								(R.string.change_password_complete));
						finish();
					}
					else
					{
						myToast.msgToast(ChangePasswordActivity.this.getString
								(R.string.new_and_confirm_password_are_different));
					}
				}
				else
				{
					myToast.msgToast(ChangePasswordActivity.this.getString
							(R.string.old_password_not_match));
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
	
	public boolean checkOldPasswordCorrect()
	{
		return true;
	}
}
