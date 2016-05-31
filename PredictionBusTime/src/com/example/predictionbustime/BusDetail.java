package com.example.predictionbustime;

public class BusDetail
{
	public static final int EARLY = 1;
	public static final int NORMAL = 2;
	public static final int LATE = 3;
	public static final int NO_BUS = 4;
	
	private String station;
	private int minute;
	private int goToStationType;
	
	public BusDetail(String station,int minute,int goToStationType)
	{
		setStation(station);
		setMinute(minute);
		setGoToStationType(goToStationType);
	}
	
	public void setStation(String station)
	{
		this.station = station;
	}
	public String getStation()
	{
		return station;
	}
	
	public void setMinute(int minute)
	{
		this.minute = minute;
	}
	public int getMinute()
	{
		return minute;
	}
	
	public void setGoToStationType(int goToStationType)
	{
		this.goToStationType = goToStationType;
	}
	public int getGoToStationType()
	{
		return goToStationType;
	}
	
	public String toString()
	{
		return getStation() +" "+ getMinute() +" "+ getGoToStationType();
	}
}
