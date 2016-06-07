package com.example.predictionbustime;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import method.MenuSpinnerListener;
import method.MyAdapter;
import network.SendPostThread;
import refresh.RefreshPrintThread;

import org.json.JSONException;
import org.json.JSONObject;

import chara.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PrintSearchByIdResult extends Activity
{
	private static final int BUS_FORWARD = 0;
	private static final int BUS_INVERSE = 1;

	private static final int SELECT_START_STATION = 0;
	private static final int SELECT_END_STATION = 1;

	public static final int THROUGH_TEST = 0;
	public static final int LISTVIEW_UPDATE = 1;
	public static final int TAKE_POST_RETURN_RESULT = 2;
	
	TextView printSearchTitle;
	Button switchButton,signButton;
	Spinner menuSpinner;
	String[] list;
	ArrayAdapter<String> listAdapter2;

	ListView listview;
	List<BusDetail> bus_list = new ArrayList<BusDetail>();
	private MyAdapter listAdapter;

	RefreshPrintThread refreshPrintThread;
	SendPostThread sendPostThread;
	
	String routeNumber;
	String[] mTestArray;
	int busDirection;
	
	
	int fakeTime = 0;
	
	int listViewState = SELECT_START_STATION;
	int startStationID = -1;
	int endStationID = -1;
	
	//B397A7F7
	//BOBTEST01
	//BOBTEST02
	//BOBTEST03
	
	User user = User.getUniqueUser();
	
	private final static int REQUEST_CODE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_by_id_end_layout);

		printSearchTitle = (TextView) findViewById(R.id.printSearchTitle);
		listview = (ListView) findViewById(R.id.busDataListView);
		switchButton = (Button) findViewById(R.id.switchButton);
		signButton = (Button) findViewById(R.id.signButton);
		menuSpinner = (Spinner) findViewById(R.id.menuSpinner);
		
		busDirection = BUS_FORWARD;
		routeNumber = null;

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		refreshPrintThread = new RefreshPrintThread(runHandle);

		routeNumber = bundle.getString("Route");
//		user.setUUID(bundle.getString("UUID"));
		printSearchTitle.setText("" + routeNumber + "號線");
		
		list = getResources().getStringArray(R.array.menu);
		listAdapter2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, list);
		menuSpinner.setAdapter(listAdapter2);
		menuSpinner.setOnItemSelectedListener(
				new MenuSpinnerListener(signButton,menuSpinner,user));

		switch (routeNumber)
		{
			case "33":
				mTestArray = getResources().getStringArray(R.array.id_33_route);
				break;
			case "160":
				mTestArray = getResources().getStringArray(R.array.id_160_route);
				break;
			default:
				msgToast("系統錯誤，請聯絡工程師");;
				break;
		}

		setListViewPrint(mTestArray, busDirection);
		refreshPrintThread.start();
	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();

		if(user.getUUID().equals(""))
		{
			menuSpinner.setVisibility(View.GONE);
			signButton.setVisibility(View.VISIBLE);
		}
		else
		{
			menuSpinner.setVisibility(View.VISIBLE);
			signButton.setVisibility(View.GONE);
		}
		
		switchButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				listViewState = SELECT_START_STATION;
				printSearchTitle.setText("" + routeNumber + "號線");
				switch (busDirection)
				{
				case BUS_FORWARD:
					busDirection = BUS_INVERSE;
					break;
				case BUS_INVERSE:
					busDirection = BUS_FORWARD;
					break;
				default:
					msgToast("busDirection error");
					break;
				}

				setListViewPrint(mTestArray, busDirection);
			}
		});
		
		signButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PrintSearchByIdResult.this, SignInActivity.class);
				startActivityForResult(intent,REQUEST_CODE);
			}
		});
	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		if (refreshPrintThread != null)
		{
			refreshPrintThread.quit();
			refreshPrintThread = null;
		}

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if (refreshPrintThread != null)
		{
			refreshPrintThread.quit();
			refreshPrintThread = null;
		}
	}

	public String[] reverse(String[] a)
	{
		String[] b = new String[a.length];

		for (int i = 0; i < a.length; i++)
		{
			b[i] = a[a.length - 1 - i];
		}
		return b;
	}

	public void setListViewPrint(String[] mTestArray, int busDirection)
	{
		bus_list.clear();

		if (busDirection == BUS_INVERSE)
		{
			mTestArray = reverse(mTestArray);
		}

		for (int i = 0; i < mTestArray.length; i++)
		{
			bus_list.add(new BusDetail(mTestArray[i],
					getNeedMinute(mTestArray[i]),
					getGoToStationType(mTestArray[i])));
		}

		switchButton.setText("往" + mTestArray[0]);

		listAdapter = new MyAdapter(PrintSearchByIdResult.this, bus_list);
		listview.setAdapter(listAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// bus_list.get(position).toString(), Toast.LENGTH_SHORT)
				// .show();
				
				switch(listViewState)
				{
					case SELECT_START_STATION:
						
						if(user.getUUID().equals(""))
						{
							msgToast("要使用預約功能請先登入");
						}
						else
						{
							if(endStationID != -1)
							{
								msgToast("前一次預約將被取消，開始進行新預約");
								endStationID = -1;
							}
							
							startStationID = position;
							listViewState = SELECT_END_STATION;
							msgToast("開始預約，已選擇上車站");
							printSearchTitle.setText("請問要在哪站下車?");
						}
						break;
					case SELECT_END_STATION:
						
						if(user.getUUID().equals(""))
						{
							msgToast("要使用預約功能請先登入");
							listViewState = SELECT_START_STATION;
							printSearchTitle.setText("" + routeNumber + "號線");
						}
						else
						{
							if(startStationID > position)
							{
								msgToast("請選擇上車站後的站");
							}
							else if(startStationID == position)
							{
								msgToast("取消預約程序");
								listViewState = SELECT_START_STATION;
								printSearchTitle.setText("" + routeNumber + "號線");
							}
							else
							{
								msgToast("預約請求已發出");
								listViewState = SELECT_START_STATION;
								printSearchTitle.setText("" + routeNumber + "號線");
								endStationID = position;
								reservation();
							}
						}
						
						break;
					default:
						msgToast("系統錯誤，請聯絡工程師");						
						break;
				}
			}

		});
	}

	public void reservation()
	{
		URL url = null;
		
		try
		{
			url = new URL("http://ibus.team-bob.org:3000/v2/reservation");
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			msgToast("URL錯誤");
		}
		
		StringBuilder uriparameters = new StringBuilder();
		try
		{
			uriparameters.append("route=" + URLEncoder.encode(routeNumber, "UTF-8"));
			uriparameters.append("&UID="+ URLEncoder.encode(user.getUUID(), "UTF-8"));
			uriparameters.append("&from_sn="+ URLEncoder.encode(""+startStationID, "UTF-8"));
			uriparameters.append("&to_sn="+ URLEncoder.encode(""+endStationID, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			msgToast("URLEncoder錯誤");
		}
		
		
		sendPostThread = new SendPostThread(url,uriparameters,runHandle);
		sendPostThread.start();
	}

	public int getNeedMinute(String station)
	{
		int time = 0;
		JSONObject jsonObject = null;
		URL url = null;
		
		try
		{
			url = new URL("");
			time = jsonObject.getInt("time");
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			try
			{
				if(routeNumber.equals("160") && busDirection == BUS_FORWARD)
				{
					time = fakeData(station,fakeTime).getInt("Time");
				}
			}
			catch (JSONException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return time;
	}

	public int getGoToStationType(String station)
	{
		int state = 0;
		JSONObject jsonObject = null;
		URL url = null;

		try
		{
			url = new URL("");
			state = jsonObject.getInt("state");
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

			try
			{
				if(routeNumber.equals("160") && busDirection == BUS_FORWARD)
				{

					state = fakeData(station,fakeTime).getInt("State");
				}
			}
			catch (JSONException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return state;
	}

	public void msgToast(String msg)
	{
		// CKJ: to show the short message using TOAST UI Component
		Toast msgToast;
		msgToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		msgToast.setText(msg);
		msgToast.show();
	}

	public JSONObject fakeData(String station,int fakeTime)
	{
		JSONObject[] fakeSearchData = null;
		JSONObject jsonObject = null;
		boolean find = false;

		String[][] fakeSearchResult = new String[][] {
				{
					"{\"BusID\":2," + "\"Time\":18," + "\"State\":2}",
					"{\"BusID\":0," + "\"Time\":5," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":11," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":16," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":7," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":15," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":22," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":25," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":27," + "\"State\":3}"
				},
				{
					"{\"BusID\":2," + "\"Time\":16," + "\"State\":2}",
					"{\"BusID\":0," + "\"Time\":0," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":10," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":14," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":5," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":13," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":20," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":22," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":24," + "\"State\":3}"
				},
				{
					"{\"BusID\":2," + "\"Time\":14," + "\"State\":2}",
					"{\"BusID\":0," + "\"Time\":0," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":10," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":14," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":0," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":11," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":18," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":21," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":23," + "\"State\":3}"
				},
				{
					"{\"BusID\":2," + "\"Time\":8," + "\"State\":2}",
					"{\"BusID\":2," + "\"Time\":13," + "\"State\":2}",
					"{\"BusID\":0," + "\"Time\":0," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":5," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":13," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":5," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":12," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":15," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":17," + "\"State\":2}"
				},
				{
					"{\"BusID\":2," + "\"Time\":5," + "\"State\":2}",
					"{\"BusID\":2," + "\"Time\":10," + "\"State\":2}",
					"{\"BusID\":2," + "\"Time\":16," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":0," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":10," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":0," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":10," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":12," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":14," + "\"State\":1}"
				},
				{
					"{\"BusID\":3," + "\"Time\":14," + "\"State\":3}",
					"{\"BusID\":3," + "\"Time\":19," + "\"State\":3}",
					"{\"BusID\":3," + "\"Time\":25," + "\"State\":3}",
					"{\"BusID\":3," + "\"Time\":30," + "\"State\":3}",
					"{\"BusID\":0," + "\"Time\":0," + "\"State\":1}",
					"{\"BusID\":0," + "\"Time\":11," + "\"State\":1}",
					"{\"BusID\":1," + "\"Time\":4," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":7," + "\"State\":2}",
					"{\"BusID\":1," + "\"Time\":9," + "\"State\":2}"
				}
		};
		try
		{
			if(fakeTime < fakeSearchResult.length)
			{
				fakeSearchData = new JSONObject[] { 
						
						new JSONObject(fakeSearchResult[fakeTime][0]),
						new JSONObject(fakeSearchResult[fakeTime][1]),
						new JSONObject(fakeSearchResult[fakeTime][2]),
						new JSONObject(fakeSearchResult[fakeTime][3]),
						new JSONObject(fakeSearchResult[fakeTime][4]),
						new JSONObject(fakeSearchResult[fakeTime][5]),
						new JSONObject(fakeSearchResult[fakeTime][6]),
						new JSONObject(fakeSearchResult[fakeTime][7]),
						new JSONObject(fakeSearchResult[fakeTime][8])
						
//						new JSONObject(fakeSearchResult[4][0]),
//						new JSONObject(fakeSearchResult[4][1]),
//						new JSONObject(fakeSearchResult[4][2]),
//						new JSONObject(fakeSearchResult[4][3]),
//						new JSONObject(fakeSearchResult[4][4]),
//						new JSONObject(fakeSearchResult[4][5]),
//						new JSONObject(fakeSearchResult[4][6]),
//						new JSONObject(fakeSearchResult[4][7]),
//						new JSONObject(fakeSearchResult[5][8])
				};
			}
			else
			{
				fakeSearchData  = new JSONObject[] { 
						
						new JSONObject("{\"BusID\":-99," + "\"Time\":-1," + "\"State\":4}")
				};
			}
			
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			msgToast("a");
		}
		
		for(int i=0 ; i<fakeSearchData.length ; i++)
		{
			if(mTestArray[i].equals(station))
			{
//				msgToast(""+fakeSearchData[fakeTime].length());
				jsonObject = fakeSearchData[i];
				find = true;
			}
		}
		
		if(find == false)
		{
			try
			{
				jsonObject = 
						new JSONObject("{\"BusID\":-99," + "\"Time\":-1," + "\"State\":4}");
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				msgToast("b");
			}
		}
		
		return jsonObject;
	}

	private final Handler runHandle = new Handler()
	{
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case LISTVIEW_UPDATE:
					Log.w("Message", "" + msg.what);
					
					fakeTime++;
					
					setListViewPrint(mTestArray, busDirection);
					
//					msgToast(""+fakeTime);
					
					

				break;
				case THROUGH_TEST:
					Log.w("Message", "" + msg.what);
	
				break;
				case TAKE_POST_RETURN_RESULT:
					Log.w("Message", "" + msg.what);
					msgToast(sendPostThread.getResult());
					
					
				break;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		
		if (requestCode == REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				Bundle extras = data.getExtras();
				if (extras != null)
				{
					user.setUUID(extras.getString("UUID"));
					signButton.setVisibility(View.GONE);
					menuSpinner.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
