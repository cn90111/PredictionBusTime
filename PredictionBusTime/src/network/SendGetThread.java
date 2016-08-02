package network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import method.MyToast;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.predictionbustime.PrintSearchByIdResult;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SendGetThread extends Thread
{
	URL url;
	HttpURLConnection urlConnection;
	String returnString;
	Handler runHandle;
	JSONArray jsonArray;
	
	public SendGetThread(URL url,Handler runHandle)
	{
		this.url = url;
		this.runHandle = runHandle;
	}
	
	public void run()
	{
		try
		{
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET"); 
			urlConnection.connect();
			
			InputStream inputStream = urlConnection.getInputStream();
			
			byte[] data = new byte[1024]; 
			
	        int num = inputStream.read(data); 
	        
	        returnString = "";
	        
	        while(num!=(-1))
	        {
	        	returnString = returnString + new String(data, 0, num);
	        	
	        	Log.v("Add String","num:"+num);
	        	Log.v("Add String",new String(data, 0, num));
				
	        	num = inputStream.read(data); 
				
				Log.v("InputStream","end get data");
				
				urlConnection.disconnect();
				
				jsonArray=new JSONArray(returnString);
				
				Message msg = new Message();
				msg.what = PrintSearchByIdResult.TAKE_GET_RETURN_RESULT;
				runHandle.sendMessage(msg);
				
	        }
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("urlConnection錯誤","SendGetThread");
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("JSON格式錯誤","SendGetThread");
		}
	}
	
	public JSONArray getResult()
	{
		return jsonArray;
	}
}
