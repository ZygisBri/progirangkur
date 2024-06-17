package praktikosDarbas.entity;

import java.util.Date;


public class VelocityData {
	
	
	private double velocity;
	private double direction;
	private Date date;
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	public double getDirection() {
		return direction;
	}
	public void setDirection(double direction) {
		this.direction = direction;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public VelocityData(double velocity, double direction, Date date) {
		super();
		this.velocity = velocity;
		this.direction = direction;
		this.date = date;
	}

	
}
