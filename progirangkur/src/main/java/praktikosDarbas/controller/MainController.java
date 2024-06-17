package praktikosDarbas.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import praktikosDarbas.entity.AirPressure;
import praktikosDarbas.entity.Temperature;
import praktikosDarbas.entity.VelocityData;
import praktikosDarbas.entity.Visibility;
import praktikosDarbas.entity.Wind;
import praktikosDarbas.service.DataService;
import praktikosDarbas.service.MeteoService;

@RestController
public class MainController {

	@Autowired
	DataService dataService;
	
	@Autowired
	MeteoService meteoService;
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/hidroData")
	public List<VelocityData> getHidroData() throws ParseException {
	return dataService.getAllVelocityData();
	}
	@CrossOrigin(origins = "*")
	@RequestMapping("/wind")
	public List<Wind> getWind() throws ParseException {
	return meteoService.getAllWindData();
	}
	@CrossOrigin(origins = "*")
	@RequestMapping("/airPresure")
	public List<AirPressure> getAirPresure() throws ParseException {
	return meteoService.getAllAirPresure();
	}
	@CrossOrigin(origins = "*")
	@RequestMapping("/temperature")
	public List<Temperature> getTemperature() throws ParseException {
	return meteoService.getAllTemperature();
	}
	@CrossOrigin(origins = "*")
	@RequestMapping("/visibility")
	public List<Visibility> getVisibility() throws ParseException {
	return meteoService.getAllVisibility();
	}
	
	
}
