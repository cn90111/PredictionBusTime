package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;

import com.example.predictionbustime.PrintSearchByIdResult;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SendPostThread extends Thread
{
	URL url;
	StringBuilder body;
	Handler handler;
	String returnResult;

	public SendPostThread(URL url, StringBuilder body, Handler handler)
	{
		this.url = url;
		this.body = body;
		this.handler = handler;
	}

	public void run()
	{
		// StringBuilder uriparameters = new StringBuilder();
		// uriparameters.append("route=" + URLEncoder.encode(user, "UTF-8"));
		// uriparameters.append("&password="+ URLEncoder.encode(pass, "UTF-8"));
		int parameterLen = body.length();
		/** sURL 是想要 post 的網址 **/
		// URL url = new URL(sURL);
		HttpURLConnection urlConnection = null;

		try
		{
			urlConnection = (HttpURLConnection) url.openConnection();

			/** 假裝成瀏覽器 **/
			urlConnection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11");
			urlConnection.setConnectTimeout(15000);
			urlConnection.setReadTimeout(15000);
			urlConnection.setRequestMethod("POST");

//			urlConnection
//					.setRequestProperty("X-Gallery-Request-Method", "post");
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Content-Length",
					Integer.toString(parameterLen));

			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();

			DataOutputStream wr = null;

			wr = new DataOutputStream(urlConnection.getOutputStream());
			wr.writeBytes(body.toString());
			wr.flush();
			wr.close();
			wr = null;

			/** 取得 post 後所得到的 response **/
			InputStream inputStream = urlConnection.getInputStream();
			
			Log.e("InputStream","start get data");
			byte[] data = new byte[1024]; 
			
	        int num = inputStream.read(data); 
	        
	        returnResult = "";
	        
	        while(num!=(-1))
	        {
	        	returnResult = returnResult + new String(data, 0, num);
	        	
	        	Log.v("Add String","num:"+num);
	        	Log.v("Add String",new String(data, 0, num));
				
	        	num = inputStream.read(data); 
	        }
	        
	        Message msg = new Message();
			msg.what = PrintSearchByIdResult.TAKE_POST_RETURN_RESULT;
			handler.sendMessage(msg);
	        
			Log.v("InputStream","end get data");
			urlConnection.disconnect();
		}
		catch (ProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getResult()
	{
		return returnResult;
	}
}
