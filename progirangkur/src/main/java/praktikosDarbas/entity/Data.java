package praktikosDarbas.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Data")
@Table(name="horizontal")
public class Data {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id",nullable=false)
	private int id;
	@Column(name="P_DAV_ID",nullable=false,unique=false)
	private int davID;
	@Column(name="P_PAKETO_NR",nullable=false)
	private int paketoNR;
	@Column(name="P_LAIKAS",nullable=false)
	private Date pLAIKAS;
	@Column(name="P_TEMPERATURA",nullable=true)
	private int temparatura;
	@Column(name="P_DAV_GYLIS",nullable=true)
	private double gylis;
	@Column(name="P_DAV_KRYPTIS",nullable=true)
	private double kriptis;
	@Column(name="P_DAV_NUOLYDIS",nullable=true)
	private double nuolidis;
	@Column(name="P_DAV_PASVIRIMAS",nullable=true)
	private double pasvirimas;
	@Column(name="P_CELIU_KIEKIS",nullable=true)
	private int celiuKiekis;
	@Column(name="P_EIL_NR",nullable=true)
	private int eilNr;
	@Column(name="P_GREITIS1",nullable=true)
	private int greitis1;
	@Column(name="P_GREITIS2",nullable=true)
	private int greitis2;
	@Column(name="P_GREITIS3",nullable=true)
	private int greitis3;
	@Column(name="P_GREITIS4",nullable=true)
	private int greitis4;
	@Column(name="D_TIPAS",nullable=true)
	private int tipas;
	@Column(name="D_PAVADINIMAS",nullable=true)
	private String pavadinimas;
	@Column(name="D_ATSTUMAS",nullable=true)
	private int atstumas;
	@Column(name="D_ATST_BIN",nullable=true)
	private int atstumasBin;
	@Column(name="D_GEDIMAS",nullable=true)
	private int gedimas;
	@Column(name="P_KOORD_TIPAS",nullable=true)
	private String kordTipas;
	@Column(name="D_KOORDX",nullable=true)
	private String kordX;
	@Column(name="D_KOORDY",nullable=true)
	private String kordY;
	public Data(int davID, int paketoNR, Date pLAIKAS, int temparatura, int gylis, int kriptis, int nuolidis,
			int pasvirimas, int celiuKiekis, int eilNr, int greitis1, int greitis2, int greitis3, int greitis4,
			int tipas, String pavadinimas, int atstumas, int atstumasBin, int gedimas, String kordTipas, String kordX,
			String kordY) {
		super();
		this.davID = davID;
		this.paketoNR = paketoNR;
		this.pLAIKAS = pLAIKAS;
		this.temparatura = temparatura;
		this.gylis = gylis;
		this.kriptis = kriptis;
		this.nuolidis = nuolidis;
		this.pasvirimas = pasvirimas;
		this.celiuKiekis = celiuKiekis;
		this.eilNr = eilNr;
		this.greitis1 = greitis1;
		this.greitis2 = greitis2;
		this.greitis3 = greitis3;
		this.greitis4 = greitis4;
		this.tipas = tipas;
		this.pavadinimas = pavadinimas;
		this.atstumas = atstumas;
		this.atstumasBin = atstumasBin;
		this.gedimas = gedimas;
		this.kordTipas = kordTipas;
		this.kordX = kordX;
		this.kordY = kordY;
	}
		
	public Data() {
		super();
	}



	public int getDavID() {
		return davID;
	}
	public void setDavID(int davID) {
		this.davID = davID;
	}
	public int getPaketoNR() {
		return paketoNR;
	}
	public void setPaketoNR(int paketoNR) {
		this.paketoNR = paketoNR;
	}
	public Date getpLAIKAS() {
		return pLAIKAS;
	}
	public void setpLAIKAS(Date pLAIKAS) {
		this.pLAIKAS = pLAIKAS;
	}
	public int getTemparatura() {
		return temparatura;
	}
	public void setTemparatura(int temparatura) {
		this.temparatura = temparatura;
	}
	public double getGylis() {
		return gylis;
	}
	public void setGylis(double deapth) {
		this.gylis = deapth;
	}
	public double getKriptis() {
		return kriptis;
	}
	public void setKriptis(double heading) {
		this.kriptis = heading;
	}
	public double getNuolidis() {
		return nuolidis;
	}
	public void setNuolidis(double pitchFixed) {
		this.nuolidis = pitchFixed;
	}
	public double getPasvirimas() {
		return pasvirimas;
	}
	public void setPasvirimas(double roolFixed) {
		this.pasvirimas = roolFixed;
	}
	public int getCeliuKiekis() {
		return celiuKiekis;
	}
	public void setCeliuKiekis(int celiuKiekis) {
		this.celiuKiekis = celiuKiekis;
	}
	public int getEilNr() {
		return eilNr;
	}
	public void setEilNr(int eilNr) {
		this.eilNr = eilNr;
	}
	public int getGreitis1() {
		return greitis1;
	}
	public void setGreitis1(int greitis1) {
		this.greitis1 = greitis1;
	}
	public int getGreitis2() {
		return greitis2;
	}
	public void setGreitis2(int greitis2) {
		this.greitis2 = greitis2;
	}
	public int getGreitis3() {
		return greitis3;
	}
	public void setGreitis3(int greitis3) {
		this.greitis3 = greitis3;
	}
	public int getGreitis4() {
		return greitis4;
	}
	public void setGreitis4(int greitis4) {
		this.greitis4 = greitis4;
	}
	public int getTipas() {
		return tipas;
	}
	public void setTipas(int tipas) {
		this.tipas = tipas;
	}
	public String getPavadinimas() {
		return pavadinimas;
	}
	public void setPavadinimas(String pavadinimas) {
		this.pavadinimas = pavadinimas;
	}
	public int getAtstumas() {
		return atstumas;
	}
	public void setAtstumas(int atstumas) {
		this.atstumas = atstumas;
	}
	public int getAtstumasBin() {
		return atstumasBin;
	}
	public void setAtstumasBin(int atstumasBin) {
		this.atstumasBin = atstumasBin;
	}
	public int getGedimas() {
		return gedimas;
	}
	public void setGedimas(int gedimas) {
		this.gedimas = gedimas;
	}
	public String getKordTipas() {
		return kordTipas;
	}
	public void setKordTipas(String kordTipas) {
		this.kordTipas = kordTipas;
	}
	public String getKordX() {
		return kordX;
	}
	public void setKordX(String kordX2) {
		this.kordX = kordX2;
	}
	public String getKordY() {
		return kordY;
	}
	public void setKordY(String kordY2) {
		this.kordY = kordY2;
	}

	@Override
	public String toString() {
		return "Data [id=" + id + ", davID=" + davID + ", paketoNR=" + paketoNR + ", pLAIKAS=" + pLAIKAS
				+ ", temparatura=" + temparatura + ", gylis=" + gylis + ", kriptis=" + kriptis + ", nuolidis="
				+ nuolidis + ", pasvirimas=" + pasvirimas + ", celiuKiekis=" + celiuKiekis + ", eilNr=" + eilNr
				+ ", greitis1=" + greitis1 + ", greitis2=" + greitis2 + ", greitis3=" + greitis3 + ", greitis4="
				+ greitis4 + ", tipas=" + tipas + ", pavadinimas=" + pavadinimas + ", atstumas=" + atstumas
				+ ", atstumasBin=" + atstumasBin + ", gedimas=" + gedimas + ", kordTipas=" + kordTipas + ", kordX="
				+ kordX + ", kordY=" + kordY + "]";
	}
	
	
}
