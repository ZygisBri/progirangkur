package praktikosDarbas.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import praktikosDarbas.entity.AirPressure;
import praktikosDarbas.entity.Temperature;
import praktikosDarbas.entity.Visibility;
import praktikosDarbas.entity.Wind;
import praktikosDarbas.repository.MeteoRepository;

@Service
public class MeteoService {

	@Autowired
	MeteoRepository rep;

	public List<Wind> getAllWindData() throws ParseException{ 
		Date currentDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12));
		List<Object[]> allData = rep.findWind(currentDate);
		ArrayList<Wind> wind=new ArrayList<Wind>();
		for (Object[] object : allData) {
			wind.add(new Wind((Date)object[3], DoubleRounder.round((double)object[0], 2), DoubleRounder.round((double)object[2],2), DoubleRounder.round((double)object[1],2)));
		}
		return wind;
	}
	public List<AirPressure> getAllAirPresure() throws ParseException{ 
		Date currentDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12));
		List<Object[]> allData = rep.findPressure(currentDate);
		ArrayList<AirPressure> presure=new ArrayList<AirPressure>();
		for (Object[] object : allData) {
			presure.add(new AirPressure( (Date) object[1], DoubleRounder.round((double) object[0],2)));
		}
		return presure;
	}
	public List<Temperature> getAllTemperature() throws ParseException{ 
		Date currentDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12));
		List<Object[]> allData = rep.findTemperature(currentDate);
		ArrayList<Temperature> temp=new ArrayList<Temperature>();
		for (Object[] object : allData) {
			temp.add(new Temperature( (Date) object[1], DoubleRounder.round((double) object[0],2)));
		}
		return temp;
	}
	public List<Visibility> getAllVisibility() throws ParseException{ 
		Date currentDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12));
		List<Object[]> allData = rep.findVisibility(currentDate);
		ArrayList<Visibility> visibility=new ArrayList<Visibility>();
		for (Object[] object : allData) {
			visibility.add(new Visibility( (Date) object[1], DoubleRounder.round((double) object[0],2)));
		}
		return visibility;
	}
	
}
