package praktikosDarbas.entity;

import java.util.Date;

public class Wind {
	private Date date;
	private double windSpeed;
	private double windDirection;
	private double windGust;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}
	public double getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(double windDirection) {
		this.windDirection = windDirection;
	}
	public double getWindGust() {
		return windGust;
	}
	public void setWindGust(double windGust) {
		this.windGust = windGust;
	}
	public Wind(Date date, double windSpeed, double windDirection, double windGust) {
		super();
		this.date = date;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
		this.windGust = windGust;
	}
	
}
