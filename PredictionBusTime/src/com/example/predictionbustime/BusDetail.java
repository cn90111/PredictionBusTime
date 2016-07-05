package com.example.predictionbustime;

public class BusDetail
{
	public static final int EARLY = 1;
	public static final int NORMAL = 2;
	public static final int LATE = 3;
	public static final int NO_BUS = 4;
	
	public static final int NONE_RESERVATION = 1;
	public static final int RESERVATION_START_STATION = 2;
	public static final int RESERVATION_END_STATION = 3;
	
	private String station;
	private int minute;
	private int goToStationType;
	private int reservationStation;
	
	public BusDetail(String station,int minute,int goToStationType,int reservationStation)
	{
		setStation(station);
		setMinute(minute);
		setGoToStationType(goToStationType);
		setReservationStation(reservationStation);
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
	
	public void setReservationStation(int reservationStation)
	{
		this.reservationStation = reservationStation;
	}
	public int getReservationStation()
	{
		return reservationStation;
	}
	
	public String toString()
	{
		return getStation() +" "+ getMinute() +" "+ getGoToStationType();
	}
}
