package com.example.predictionbustime;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import method.MenuListener;
import method.MyAdapter;
import method.MyFormula;
import method.MyToast;
import network.SendPostThread;
import network.SendGetThread;
import refresh.RefreshPrintThread;

import org.json.JSONArray;
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
	public static final int BUS_FORWARD = 0;
	public static final int BUS_INVERSE = 1;

	private static final int SELECT_START_STATION = 0;
	private static final int SELECT_END_STATION = 1;

	public static final int THROUGH_TEST = 0;
	public static final int LISTVIEW_UPDATE = 1;
	public static final int TAKE_POST_RETURN_RESULT = 2;
	public static final int TAKE_GET_RETURN_RESULT = 3;
	
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
	SendGetThread sendGetThread;
	
	JSONArray busAllData;
	
	MyToast myToast = new MyToast(PrintSearchByIdResult.this);
	
	String routeNumber;
	String[] mTestArray;
	Integer[] startEndStationArray;
	
	boolean haveReservation = false;
	int busDirection;
	
	//假資料
	int fakeTime = 0;
	
	//預約
	int listViewState = SELECT_START_STATION;
	int startStationID = -1;
	int endStationID = -1;
	
	//標記公車距站牌還有幾站所用
	int previousArrivalTime = -1;
	int busApartStationCount = -1;
	
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

		routeNumber = bundle.getString("Route");
		printSearchTitle.setText(
				PrintSearchByIdResult.this.getString
				(R.string.search_result_title)+ routeNumber);
		
		list = getResources().getStringArray(R.array.menu);
		listAdapter2 = new ArrayAdapter(this,
				R.layout.myspinner, list);
		menuSpinner.setAdapter(listAdapter2);
		menuSpinner.setOnItemSelectedListener(menuListener);

		
		switch (routeNumber)
		{
			case "33":
				mTestArray = getResources().getStringArray(R.array.id_33_route_zh_tw);
				break;
			case "160":
				mTestArray = getResources().getStringArray(R.array.id_160_route_zh_tw);
				break;
			case "OO":
				mTestArray = getResources().getStringArray(R.array.id_OO_route);
				break;
			default:
				myToast.msgToast(PrintSearchByIdResult.this.getString
						(R.string.system_error_plz_contact_us));
				break;
		}

		if(mTestArray != null)
		{
			startEndStationArray = new Integer[mTestArray.length];
		}
		
		for(int i=0 ; i<startEndStationArray.length ; i++)
		{
			startEndStationArray[i] = BusDetail.NONE_RESERVATION;
		}
		
		myToast.msgToast(PrintSearchByIdResult.this.getString
				(R.string.refresh_screen_plz_wait));
		
		setListViewPrint();
		
	}
	
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		
		refreshPrintThread = new RefreshPrintThread(runHandle);
		refreshPrintThread.start();
		
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
				//printSearchTitle.setText("" + routeNumber + "號線");
				printSearchTitle.setText(
						PrintSearchByIdResult.this.getString
						(R.string.search_result_title)+ routeNumber);
				switch (busDirection)
				{
				case BUS_FORWARD:
					busDirection = BUS_INVERSE;
//					myToast.msgToast("切換逆向");
					break;
				case BUS_INVERSE:
					busDirection = BUS_FORWARD;
//					myToast.msgToast("切換順向");
					break;
				default:
					myToast.msgToast("busDirection error");
					break;
				}
				
				new MyFormula<String>().reverse(mTestArray);
				new MyFormula<Integer>().reverse(startEndStationArray);
				
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
					reservationButton.setText(PrintSearchByIdResult.this.getString
							(R.string.reserve));
					
					myToast.msgToast(PrintSearchByIdResult.this.getString
							(R.string.cancel_reservation));
				
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

					myToast.msgToast(PrintSearchByIdResult.this.getString
							(R.string.open_reserve_plz_click_stop_want_to_get_on));
				}
			}
		});
		
		returnButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
//				myToast.msgToast(busAllData.toString());
				finish();
			}
		});
		
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		
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

		previousArrivalTime = -1;
		busApartStationCount = -1;
				
		getBusAllData(routeNumber,busDirection);
	}
	
	public void getBusAllData(String route,int isReverse)
	{
		URL url = null;
		boolean is_reverse = true;
		
		if(isReverse == BUS_INVERSE)
		{
			is_reverse = true;
		}
		else if(isReverse == BUS_FORWARD)
		{
			is_reverse = false;
		}
		else
		{
			Log.e(PrintSearchByIdResult.this.getString(R.string.error),
					PrintSearchByIdResult.this.getString(R.string.bus_direction_error));
			System.exit(1);
			myToast.msgToast(PrintSearchByIdResult.this.getString
					(R.string.bus_direction_error));
		}
		
		try
		{
			url = new URL("http://ibus.team-bob.org:3000/v2/busArrival?"
					+ "route=" + 160
					+ "&is_reverse=" + is_reverse);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			myToast.msgToast(PrintSearchByIdResult.this.getString
					(R.string.url_error));
		}
		
		sendGetThread = new SendGetThread(url, runHandle);
		sendGetThread.start();
	}
	
	public void printBusData()
	{
		if(busAllData != null)
		{
//			myToast.msgToast(busAllData.toString());
		}
		else
		{
			myToast.msgToast(PrintSearchByIdResult.this.getString
					(R.string.get_data_fail_plz_wait));
		}
			
		for (int i = 0; i < mTestArray.length; i++)
		{
			bus_list.add(new BusDetail(mTestArray[i],
					getNeedMinute(mTestArray[i]),
					getGoToStationType(mTestArray[i]),
					getReservationStation(i),
					getBusApartHowManyStation()));
		}

		switchButton.setText(PrintSearchByIdResult.this.getString
				(R.string.to) + " " + mTestArray[0]);

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
								myToast.msgToast(PrintSearchByIdResult.this.getString
										(R.string.want_reserve_need_log_in));
							}
							else
							{
								if(endStationID != -1)
								{
									myToast.msgToast(PrintSearchByIdResult.this.getString
											(R.string.cancel_reservation_finish_can_start_new_reservation));
									
									endStationID = -1;

								}
								
								startStationID = position;
								listViewState = SELECT_END_STATION;
								myToast.msgToast(PrintSearchByIdResult.this.getString
										(R.string.select_first_stop_already_need_select_second));
								
								updateReservation(startStationID,BusDetail.RESERVATION_START_STATION);
								
							}
						}
						else
						{
							if(haveReservation == true)
							{
								myToast.msgToast(PrintSearchByIdResult.this.getString
									(R.string.if_want_to_re_reserve_click_cancel_reserve_button));
							
							}
							else
							{
								myToast.msgToast(PrintSearchByIdResult.this.getString
										(R.string.plz_click_reserve_button));
							}
						}
						break;
					case SELECT_END_STATION:
						if(startReservation == true)
						{
							if(user.getUUID().equals(""))
							{
								myToast.msgToast(PrintSearchByIdResult.this.getString
										(R.string.Want_to_reserve_need_log_in));
								
								listViewState = SELECT_START_STATION;
								printSearchTitle.setText(
										PrintSearchByIdResult.this.getString
										(R.string.search_result_title)+ routeNumber);
							}
							else
							{
								if(startStationID > position)
								{
									myToast.msgToast(PrintSearchByIdResult.this.getString
											(R.string.plz_select_stop_after_stop_get_on));
									
								}
								else if(startStationID == position)
								{
									myToast.msgToast(PrintSearchByIdResult.this.getString
											(R.string.cancel_reserve));
									
									listViewState = SELECT_START_STATION;
									updateReservation(startStationID,BusDetail.NONE_RESERVATION);
									printSearchTitle.setText(
											PrintSearchByIdResult.this.getString
											(R.string.search_result_title)+ routeNumber);
									reservationButton.setEnabled(true);
									startReservation = false;
								}
								else
								{
									myToast.msgToast(PrintSearchByIdResult.this.getString
										(R.string.reservation_request_already_send));
								
									listViewState = SELECT_START_STATION;
									printSearchTitle.setText(
											PrintSearchByIdResult.this.getString
											(R.string.search_result_title)+ routeNumber);
									endStationID = position;
									
									updateReservation(endStationID,BusDetail.RESERVATION_END_STATION);
									
									haveReservation = true;
									startReservation = false;
									
									reservationButton.setEnabled(true);
									
									reservationButton.setText(PrintSearchByIdResult.this.getString
											(R.string.cancel_reserve));
									
									
									reservation();
								}
							}
						}
						else
						{
							myToast.msgToast(PrintSearchByIdResult.this.getString
									(R.string.plz_click_reserve_button));
							listViewState = SELECT_START_STATION;
						}
						break;
					default:
						myToast.msgToast(PrintSearchByIdResult.this.getString
								(R.string.system_error_plz_contact_us));
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
			myToast.msgToast(PrintSearchByIdResult.this.getString
					(R.string.url_error));
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
			myToast.msgToast(PrintSearchByIdResult.this.getString
					(R.string.urlencoder_error));
		}
		
		
		sendPostThread = new SendPostThread(url,uriparameters,runHandle);
		sendPostThread.start();
	}

	public int getStationID(String station)
	{
		int ID = -1;
		
		for(int i=0 ; i<mTestArray.length ; i++)
		{
			if(mTestArray[i].equals(station))
			{
				ID = i;
				
				if(busDirection == BUS_INVERSE)
				{
					ID = mTestArray.length - 1 - i;
				}
				
				ID = ID+1;
						
				break;
			}
		}
		
		return ID;
	}
	
	public int getNeedMinute(String station)
	{
		int time = 0;
		int ID;
		JSONObject jsonObject = null;
		Timestamp arrivalTime;
		Timestamp nowTime; 
		Timestamp resultTime;
		
		ID = getStationID(station);
		
		if(ID <= 0)
		{
			time = -1;
		}
		else
		{
			for(int i=0 ; i<busAllData.length() ; i++)
			{
				try
				{
					if(busAllData.getJSONObject(i).getInt("sn") == ID)
					{
						jsonObject = busAllData.getJSONObject(i);
						Log.v("jsonObject已取得", "ID:"+ID);
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(jsonObject != null)
			{
				String tempString = null;
				try
				{
					tempString = jsonObject.getString("prediction");
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(tempString != null)
				{
					String[] splitString = tempString.split("[-T:Z.]");
//					myToast.msgToast(splitString[0]+","+splitString[1]+","+splitString[2]
//							+","+splitString[3]+","+splitString[4]+","+splitString[5]);
					
					int[] splitTime = new int[]{new Integer(splitString[0])-1900,
							new Integer(splitString[1])-1,
							new Integer(splitString[2]),
							new Integer(splitString[3])+8,
							new Integer(splitString[4]),
							new Integer(splitString[5]),
							new Integer(splitString[6])};
					
					
					arrivalTime = new Timestamp(splitTime[0], splitTime[1], 
							splitTime[2], splitTime[3], splitTime[4], splitTime[5],
							splitTime[6]);
					
					Log.v("公車到站時間─"+station,arrivalTime.toString());
					
//					arrivalTime = new Timestamp(System.currentTimeMillis());
					
//					myToast.msgToast(arrivalTime.toString());
					
					nowTime = new Timestamp(System.currentTimeMillis());
					
//					myToast.msgToast(nowTime.toString());
					
					resultTime = new Timestamp(arrivalTime.getTime() - nowTime.getTime());
					
//					myToast.msgToast(""+resultTime.toString());
					
					time = (int) Math.ceil(resultTime.getTime()/1000/60) ;
					
				}
				else
				{
					Log.e("資料更新失敗", "PrintSearchByIdResult字串無法取得");
					myToast.msgToast(PrintSearchByIdResult.this.getString
							(R.string.data_update_fail));
//					System.exit(1);
				}
			}
			else
			{
				Log.e(station+" jsonObject取得失敗", "PrintSearchByIdResult");
				myToast.msgToast(PrintSearchByIdResult.this.getString
						(R.string.data_update_fail));
//				System.exit(1);
			}
			
			
			
			//arrivalTime = new Timestamp(theYear, theMonth, theDate, theHour, theMinute, theSecond, theNano);
		}
		
		
		//著色
		if(previousArrivalTime < 0)
		{
			previousArrivalTime = time;
			busApartStationCount = 1;
		}
		else
		{
			if(previousArrivalTime > time)
			{
				previousArrivalTime = time;
				busApartStationCount = 1;
			}
			else
			{
				previousArrivalTime = time;
				busApartStationCount++;
				
			}
		}

		return time;
	}

	public int getGoToStationType(String station)
	{
		int state = 0;
//		JSONObject jsonObject = null;
//		URL url = null;
//
//		try
//		{
//			url = new URL("");
//			state = jsonObject.getInt("state");
//		}
//		catch (MalformedURLException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//			try
//			{
//				if(routeNumber.equals("160") && busDirection == BUS_FORWARD)
//				{
//
//					state = fakeData(station,fakeTime).getInt("State");
//				}
//			}
//			catch (JSONException e1)
//			{
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
//		catch (JSONException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		if(haveReservation == false)
		{
			state = BusDetail.NORMAL;
		}
		else
		{
			state = BusDetail.EARLY;
		}
		
		return state;
	}
	
	public int getReservationStation(int stationID)
	{
		return startEndStationArray[stationID];
	}
	
	public int getBusApartHowManyStation()
	{
		return busApartStationCount;
	}

	private final Handler runHandle = new Handler()
	{
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case LISTVIEW_UPDATE:
					Log.w("Message", "" + msg.what);
					
					setListViewPrint();
					
				break;
				
				case THROUGH_TEST:
					Log.w("Message", "" + msg.what);
	
				break;
				
				case TAKE_POST_RETURN_RESULT:
					Log.w("Message", "" + msg.what);
					myToast.msgToast(sendPostThread.getResult());
				break;
				
				case TAKE_GET_RETURN_RESULT:
					Log.w("Message", "" + msg.what);
					busAllData = sendGetThread.getResult();
					sendGetThread = null;
					printBusData();
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
