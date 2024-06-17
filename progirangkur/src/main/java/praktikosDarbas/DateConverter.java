package praktikosDarbas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
	
	int year;
	int month;
	int day;
	int hour;
	int minute;
	int second;
	int hundredths;
	
	public DateConverter(int year, int month, int day, int hour, int minute, int second, int hundredths) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.hundredths = hundredths;
	}
	public DateConverter() {
		super();
	}

	public Date GetDate() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String date = "20"+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+"."+hundredths;
		Date formatedDate = dateFormat.parse(date);
		return formatedDate;
		
	}
	
}
