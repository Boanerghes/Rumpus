package util;

import java.util.GregorianCalendar;

/**
 * A simple class for time conversions
 */
public class TimeConv {
	
	public int getGap(){
		GregorianCalendar unixTime = new GregorianCalendar();
		return ((unixTime.get(GregorianCalendar.ZONE_OFFSET) + unixTime.get(GregorianCalendar.DST_OFFSET)) / (60 * 1000) / 60) + 5;
	}
	
	public long getUnixTime(long date){
		String time = Long.toString(date);

		GregorianCalendar unixTime = new GregorianCalendar();
		unixTime.set(Integer.parseInt(time.substring(0,4)), Integer.parseInt(time.substring(4,6)) - 1, Integer.parseInt(time.substring(6,8)), Integer.parseInt(time.substring(8,10)), Integer.parseInt(time.substring(10,12)));
		
		return (unixTime.getTimeInMillis() / 1000) + ( 3600* getGap());
	}
}
