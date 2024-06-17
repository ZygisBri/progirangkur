package praktikosDarbas.entity;

import java.util.Date;

public class AirPressure {
	private Date date;
	private double airPressure;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getAirPressure() {
		return airPressure;
	}
	public void setAirPressure(double airPressure) {
		this.airPressure = airPressure;
	}
	public AirPressure(Date date, double airPressure) {
		super();
		this.date = date;
		this.airPressure = airPressure;
	}
	
	
}
