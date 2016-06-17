package method;

import chara.User;

import com.example.predictionbustime.MainActivity;
import com.example.predictionbustime.SearchRouteByIdLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MenuSpinnerListener implements AdapterView.OnItemSelectedListener
{
	Button signButton;
	Spinner menuSpinner;
	User user;
	
	public MenuSpinnerListener(Button signButton,Spinner menuSpinner,User user)
	{
		this.signButton = signButton;
		this.menuSpinner = menuSpinner;
		this.user = user;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id)
	{
		// TODO Auto-generated method stub
//		switch(list[position])
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
				changePassword();
				break;

			case "刪除帳號":
				System.out.println("刪除帳號");
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
		
	}
	
	public void deleteAccount()
	{
		
	}
}
