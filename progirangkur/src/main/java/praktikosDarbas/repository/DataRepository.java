package praktikosDarbas.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import praktikosDarbas.entity.Data;

@Repository
public interface DataRepository extends JpaRepository<Data,Integer>{

	    
	   
	  @Query(value = "SELECT `p_greitis1`,`p_greitis2`,`p_laikas` FROM `horizontal` WHERE `p_laikas`>=?1 AND `p_eil_nr`='39'" , nativeQuery = true)
	  List findAllData(Date date);
}
