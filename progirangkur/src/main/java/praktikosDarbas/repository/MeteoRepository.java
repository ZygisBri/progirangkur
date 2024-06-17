package praktikosDarbas.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import praktikosDarbas.entity.MeteoObject;

@Repository
public interface MeteoRepository extends JpaRepository<MeteoObject ,Integer>{

	 @Query(value = "SELECT `vejo_greitis`,`vejo_gusiu_greitis`,`vejo_kryptis`,`meteo_laikas` FROM `meteo` WHERE `meteo_laikas`>=?1" , nativeQuery = true)
	  List findWind(Date date);
	 
	 @Query(value = "SELECT `oro_slegis`,`meteo_laikas` FROM `meteo` WHERE `meteo_laikas`>=?1" , nativeQuery = true)
	  List findPressure (Date date);
	 
	 @Query(value = "SELECT `temparatura`,`meteo_laikas` FROM `meteo` WHERE `meteo_laikas`>=?1" , nativeQuery = true)
	  List findTemperature(Date date);
	 
	 @Query(value = "SELECT `matomumas`,`meteo_laikas` FROM `meteo`  WHERE `meteo_laikas`>=?1" , nativeQuery = true)
	  List findVisibility (Date date);
}
