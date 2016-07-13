package refresh;

import com.example.predictionbustime.PrintSearchByIdResult;

import android.os.Handler;
import android.os.Message;

public class RefreshPrintThread extends Thread
{
	//15S
	private static final int REFRESH_TIME = 10;	

	boolean exit;
	int count;
	Handler handler;
	
	public RefreshPrintThread(Handler handler)
	{
		count = 0;
		this.handler = handler;
	}
	
	public void run()
	{
		exit = false;
		while(exit == false)
		{
			try
			{
				count++;
				Thread.sleep(1000);
				
				if(count >= REFRESH_TIME)
				{
					count = 0;
					Message msg = new Message();
					msg.what = PrintSearchByIdResult.LISTVIEW_UPDATE;
					handler.sendMessage(msg);
				}
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void quit()
	{
		exit = true;
	}
}
