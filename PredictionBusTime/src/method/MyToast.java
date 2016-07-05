package method;

import android.content.Context;
import android.widget.Toast;

public class MyToast
{
	Context nowActivity;
	Toast msgToast = null;
	
	public MyToast(Context nowActivity)
	{
		this.nowActivity = nowActivity;
	}
	
	public void msgToast(String msg)
	{
		// CKJ: to show the short message using TOAST UI Component
		
		
		if (msgToast != null)
		{
			msgToast.setText(msg);
			msgToast.setDuration(Toast.LENGTH_SHORT);
			msgToast.show();
		} 
		else
		{
			msgToast = Toast.makeText(nowActivity, msg, Toast.LENGTH_SHORT);
			msgToast.show();
		}
	}
}
