package method;

import chara.User;

import com.example.predictionbustime.ChangePasswordActivity;
import com.example.predictionbustime.MainActivity;
import com.example.predictionbustime.R;
import com.example.predictionbustime.SearchRouteByIdLayout;
import com.example.predictionbustime.SignInActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MenuListener implements AdapterView.OnItemSelectedListener,OnClickListener
{
	private static final int MENU = 0;
	private static final int LOG_OUT = 1;
	private static final int CHANGE_PASSWORD = 2;
	private static final int DELETE_ACCOUNT = 3;
	
	Button signButton;
	Spinner menuSpinner;
	User user = User.getUniqueUser();
	Context nowActivity;
	
	MyToast myToast;
	
	public MenuListener(Button signButton,Spinner menuSpinner,Context nowActivity)
	{
		this.signButton = signButton;
		this.menuSpinner = menuSpinner;
		this.nowActivity = nowActivity;
		myToast = new MyToast(nowActivity);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id)
	{
		// TODO Auto-generated method stub
		
		switch(position)
		{
			case LOG_OUT:
				signButton.setVisibility(View.VISIBLE);
				menuSpinner.setVisibility(View.GONE);
				menuSpinner.setSelection(0);
				user.setUUID("");
				break;

			case CHANGE_PASSWORD:
				changePassword();
				break;

			case DELETE_ACCOUNT:
				deleteAccount();
				break;
				
			default:
				System.out.println("程式錯誤");
				Log.e("error","MenuSpinnerListener,程式錯誤");
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void changePassword()
	{
		menuSpinner.setSelection(0);
		Intent intent = new Intent();	
		intent.setClass(nowActivity, ChangePasswordActivity.class);
		nowActivity.startActivity(intent);
	}
	
	public void deleteAccount()
	{
		
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(nowActivity, SignInActivity.class);
		nowActivity.startActivity(intent);
	}
	
	
}
