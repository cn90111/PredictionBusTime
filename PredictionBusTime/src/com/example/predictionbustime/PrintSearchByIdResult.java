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

import method.MenuListener;
import method.MyAdapter;
import method.MyFormula;
import method.MyToast;
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
	Button switchButton,signButton,reservationButton,returnButton;
	Spinner menuSpinner;
	String[] list;
	ArrayAdapter<String> listAdapter2;

	ListView listview;
	List<BusDetail> bus_list = new ArrayList<BusDetail>();
	private MyAdapter listAdapter;

	MenuListener menuListener;
		
	RefreshPrintThread refreshPrintThread;
	SendPostThread sendPostThread;
	
	MyToast myToast = new MyToast(PrintSearchByIdResult.this);
	
	String routeNumber;
	String[] mTestArray;
	Integer[] startEndStationArray;
	
	boolean haveReservation = false;
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
	
	boolean startReservation = false;
	
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
		reservationButton = (Button) findViewById(R.id.reservationButton);
		returnButton = (Button) findViewById(R.id.returnButton);
		menuSpinner = (Spinner) findViewById(R.id.menuSpinner);
		
		busDirection = BUS_FORWARD;
		routeNumber = null;

		menuListener = new MenuListener(signButton,menuSpinner,PrintSearchByIdResult.this);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		refreshPrintThread = new RefreshPrintThread(runHandle);

		routeNumber = bundle.getString("Route");
		printSearchTitle.setText("" + routeNumber + "號線");
		
		list = getResources().getStringArray(R.array.menu);
		listAdapter2 = new ArrayAdapter(this,
				R.layout.myspinner, list);
		menuSpinner.setAdapter(listAdapter2);
		menuSpinner.setOnItemSelectedListener(menuListener);
		
		switch (routeNumber)
		{
			case "33":
				mTestArray = getResources().getStringArray(R.array.id_33_route);
				break;
			case "160":
				mTestArray = getResources().getStringArray(R.array.id_160_route);
				break;
			default:
				myToast.msgToast("系統錯誤，請聯絡工程師");
				break;
		}

		startEndStationArray = new Integer[mTestArray.length];
		
		for(int i=0 ; i<startEndStationArray.length ; i++)
		{
			startEndStationArray[i] = BusDetail.NONE_RESERVATION;
		}
		
		setListViewPrint();
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
					myToast.msgToast("busDirection error");
					break;
				}

				setListViewPrint();
			}
		});
		
		signButton.setOnClickListener(menuListener);
		
		reservationButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(haveReservation == true)
				{
					startReservation = false;
					haveReservation = false;
					reservationButton.setText("開始預約");

					myToast.msgToast("取消之前所做的預約設定");
				
					for(int i=0 ; i<startEndStationArray.length ; i++)
					{
						startEndStationArray[i] = BusDetail.NONE_RESERVATION;
					}
					setListViewPrint();
				}
				else
				{
					startReservation = true;
					reservationButton.setEnabled(false);
					myToast.msgToast("啟動預約功能,請您點擊要上車的起始站(直接點擊顯示時間的地方即可)");
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

	
	//String[] mTestArray, int busDirection,Integer[] startEndStation
	public void setListViewPrint()
	{
		bus_list.clear();

		if (busDirection == BUS_INVERSE)
		{
			mTestArray = new MyFormula<String>().reverse(mTestArray);
			startEndStationArray = new MyFormula<Integer>().reverse(startEndStationArray);
			
		}

		for (int i = 0; i < mTestArray.length; i++)
		{
			bus_list.add(new BusDetail(mTestArray[i],
					getNeedMinute(mTestArray[i]),
					getGoToStationType(mTestArray[i]),
					getReservationStation(i)));
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
						if(startReservation == true)
						{
							if(user.getUUID().equals(""))
							{
								myToast.msgToast("要使用預約功能請先登入");
							}
							else
							{
								if(endStationID != -1)
								{
									myToast.msgToast("前一次預約已被取消，開始進行新預約");
									endStationID = -1;

								}
								
								startStationID = position;
								listViewState = SELECT_END_STATION;
								myToast.msgToast("已選擇上車站，請選擇下車站，若要取消請再點擊一次上車站");
								updateReservation(startStationID,BusDetail.RESERVATION_START_STATION);
								
							}
						}
						else
						{
							if(haveReservation == true)
							{
								myToast.msgToast("若要重新預約，請先按下取消預約");
							}
							else
							{
								myToast.msgToast("請開啟預約按鈕");
							}
						}
						break;
					case SELECT_END_STATION:
						if(startReservation == true)
						{
							if(user.getUUID().equals(""))
							{
								myToast.msgToast("要使用預約功能請先登入");
								listViewState = SELECT_START_STATION;
								printSearchTitle.setText("" + routeNumber + "號線");
							}
							else
							{
								if(startStationID > position)
								{
									myToast.msgToast("請選擇上車站後的站");
								}
								else if(startStationID == position)
								{
									myToast.msgToast("取消預約程序");
									listViewState = SELECT_START_STATION;
									updateReservation(startStationID,BusDetail.NONE_RESERVATION);
									printSearchTitle.setText("" + routeNumber + "號線");
								}
								else
								{
									myToast.msgToast("預約請求已發出");
									listViewState = SELECT_START_STATION;
									printSearchTitle.setText("" + routeNumber + "號線");
									endStationID = position;
									
									updateReservation(endStationID,BusDetail.RESERVATION_END_STATION);
									
									haveReservation = true;
									startReservation = false;
									
									reservationButton.setEnabled(true);
									reservationButton.setText("取消預約");
									
									
									reservation();
								}
							}
						}
						else
						{
							myToast.msgToast("請開啟預約按鈕");
							listViewState = SELECT_START_STATION;
						}
						break;
					default:
						myToast.msgToast("系統錯誤，請聯絡工程師");			
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
			myToast.msgToast("URL錯誤");
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
			myToast.msgToast("URLEncoder錯誤");
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
	
	public int getReservationStation(int stationID)
	{
		return startEndStationArray[stationID];
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
					
					setListViewPrint();
					
//					msgToast(""+fakeTime);
					
					

				break;
				case THROUGH_TEST:
					Log.w("Message", "" + msg.what);
	
				break;
				case TAKE_POST_RETURN_RESULT:
					Log.w("Message", "" + msg.what);
					myToast.msgToast(sendPostThread.getResult());
					
					
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
	
	public void updateReservation(int i,int reservationState)
	{
		startEndStationArray[i] = reservationState;
		setListViewPrint();
	}
}
