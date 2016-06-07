package com.example.predictionbustime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends Activity
{
	EditText oldPasswordEdit;
	EditText newPasswordEdit,reIntputNewPasswordEdit;
	Button okButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password_main);
	}
}
