package praktikosDarbas.entity;

import java.util.Date;

public class Temperature {
	private Date date;
	private double temperature;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public Temperature(Date date, double temperature) {
		super();
		this.date = date;
		this.temperature = temperature;
	}
}
