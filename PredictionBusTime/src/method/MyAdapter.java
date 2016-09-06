package method;

import java.util.List;

import com.example.predictionbustime.BusDetail;
import com.example.predictionbustime.PrintSearchByIdResult;
import com.example.predictionbustime.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
	private LayoutInflater myInflater;
	private List<BusDetail> busDetailList;
	BusDetail busDetail;
	
	ViewHolder holder = null;
	
	public MyAdapter(Context context, List<BusDetail> busDetailList)
	{
		myInflater = LayoutInflater.from(context);
		this.busDetailList = busDetailList;
	}

	private class ViewHolder
	{
		TextView txtStation;
		TextView txtMinute;
		TextView txtType;
		TextView txtStartEndStation;
		RelativeLayout background;

		public ViewHolder(TextView txtStation, TextView txtMinute,
				TextView txtType,TextView txtStartEndStation,RelativeLayout background)
		{
			this.txtStation = txtStation;
			this.txtMinute = txtMinute;
			this.txtType = txtType;
			this.txtStartEndStation = txtStartEndStation;
			this.background = background;
		}
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return busDetailList.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return busDetailList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return busDetailList.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (convertView == null)
		{
			convertView = myInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder(
					(TextView) convertView.findViewById(R.id.station),
					(TextView) convertView.findViewById(R.id.minute),
					(TextView) convertView.findViewById(R.id.goToStationType),
					(TextView) convertView.findViewById(R.id.startEndStation),
					(RelativeLayout) convertView.findViewById(R.id.background));
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		busDetail = (BusDetail)getItem(position);

        holder.txtStation.setText(busDetail.getStation());
        holder.txtStation.setTextColor(0xFFFFFFFF);
        
        if(busDetail.getMinute() > -10 && busDetail.getMinute() < 0)
        {
        	holder.txtMinute.setText(R.string.approach);
        }
        else if(busDetail.getMinute() < -10 )
        {
        	holder.txtMinute.setText("");
        }
        else
        {
        	holder.txtMinute.setText(new Integer(busDetail.getMinute()).toString() + 
        									"min");
        }
        
        holder.txtMinute.setTextColor(0xFFFFFFFF);
        
        switch(busDetail.getGoToStationType())
        {
        	case BusDetail.EARLY:
        		holder.txtType.setText(R.string.early);
        		break;
        	case BusDetail.NORMAL:
        		holder.txtType.setText(R.string.normal);
        		break;
        	case BusDetail.LATE:
        		holder.txtType.setText(R.string.late);
        		break;
        	case BusDetail.NO_BUS:
        		holder.txtType.setText(R.string.no_bus);
        		break;
        	default:
        		holder.txtType.setText(R.string.error);
	    		break;
        		
        }
        holder.txtType.setTextColor(0xFFFFFFFF);
        
        switch(busDetail.getReservationStation())
        {
        	case BusDetail.NONE_RESERVATION:
        		holder.txtStartEndStation.setText("　");
        		break;
        	case BusDetail.RESERVATION_START_STATION:
        		holder.txtStartEndStation.setText(R.string.origin);
        		holder.txtStartEndStation.setTextColor(0xFF0066FF);
        		break;
        	case BusDetail.RESERVATION_END_STATION:
        		holder.txtStartEndStation.setText(R.string.destination);
        		holder.txtStartEndStation.setTextColor(0xFF0066FF);
        		break;
        }
        
        switch(busDetail.getBusApartHowManyStation())
        {
        	case 1:
        		holder.background.setBackgroundColor(0xFFFF0000);
        		break;
        	case 2:
        		holder.background.setBackgroundColor(0xFFFF8000);
        		break;
        	case 3:
        		holder.background.setBackgroundColor(0xFFFFD306);
        		break;
        	default:
        		holder.background.setBackgroundColor(0xFF00DD00);
        		break;
        		
        }
        
		return convertView;
	}
}
