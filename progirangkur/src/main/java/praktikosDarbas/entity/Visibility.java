package praktikosDarbas.entity;

import java.util.Date;

public class Visibility {
	private Date date;
	private double visibility;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getVisibility() {
		return visibility;
	}
	public void setVisibility(double visibility) {
		this.visibility = visibility;
	}
	public Visibility(Date date, double visibility) {
		super();
		this.date = date;
		this.visibility = visibility;
	}

	
	
}
