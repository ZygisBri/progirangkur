package praktikosDarbas.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity(name="MeteoData")
@Table(name="meteo")
public class MeteoObject {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id",nullable=false)
	private int id;
	@Column(name="meteoLaikas",nullable=false)
	private Date meteoLaikas;
	@Column(name="oroSlegis",nullable=false,unique=false)
	private double oroSlegis;
	@Column(name="vejoGreitis",nullable=false,unique=false)
    private double vejoGreitis;
	@Column(name="vejoGusiuGreitis",nullable=false,unique=false)
    private double vejoGusiuGreitis;
	@Column(name="oroDregnumas",nullable=false,unique=false)
    private double oroDregnumas;
	@Column(name="vejoKryptis",nullable=false,unique=false)
    private double vejoKryptis;
	@Column(name="matomumas",nullable=false,unique=false)
    private double matomumas;
	@Column(name="temparatura",nullable=false,unique=false)
    private double temperatura;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getOroSlegis() {
		return oroSlegis;
	}
	public void setOroSlegis(double oroSlegis) {
		this.oroSlegis = oroSlegis;
	}
	public double getVejoGreitis() {
		return vejoGreitis;
	}
	public void setVejoGreitis(double vejoGreitis) {
		this.vejoGreitis = vejoGreitis;
	}
	public double getVejoGusiuGreitis() {
		return vejoGusiuGreitis;
	}
	public void setVejoGusiuGreitis(double vejoGusiuGreitis) {
		this.vejoGusiuGreitis = vejoGusiuGreitis;
	}
	public double getOroDregnumas() {
		return oroDregnumas;
	}
	public void setOroDregnumas(double oroDregnumas) {
		this.oroDregnumas = oroDregnumas;
	}
	public double getVejoKryptis() {
		return vejoKryptis;
	}
	public void setVejoKryptis(double vejoKryptis) {
		this.vejoKryptis = vejoKryptis;
	}
	public double getMatomumas() {
		return matomumas;
	}
	public void setMatomumas(double matomumas) {
		this.matomumas = matomumas;
	}
	public double getTemperatura() {
		return temperatura;
	}
	public void setTemperatura(double temperatura) {
		this.temperatura = temperatura;
	}

	public Date getMeteoLaikas() {
		return meteoLaikas;
	}
	public void setMeteoLaikas(Date meteoLaikas) {
		this.meteoLaikas = meteoLaikas;
	}
	
	public MeteoObject(Date meteoLaikas, double oroSlegis, double vejoGreitis, double vejoGusiuGreitis,
			double oroDregnumas, double vejoKryptis, double matomumas, double temperatura) {
		super();
		this.meteoLaikas = meteoLaikas;
		this.oroSlegis = oroSlegis;
		this.vejoGreitis = vejoGreitis;
		this.vejoGusiuGreitis = vejoGusiuGreitis;
		this.oroDregnumas = oroDregnumas;
		this.vejoKryptis = vejoKryptis;
		this.matomumas = matomumas;
		this.temperatura = temperatura;
	}
	public MeteoObject() {
	
	}
	
	
}
