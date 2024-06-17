package praktikosDarbas.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import praktikosDarbas.DateConverter;
import praktikosDarbas.entity.Data;
import praktikosDarbas.entity.VelocityData;
import praktikosDarbas.repository.DataRepository;

@Service
public class DataService {

	@Autowired
	DataRepository rep;
	
   public DataService() {
	}
   public boolean checkSumOk(byte[] bytes) {
	   int l = bytes.length;
	   int sum = 0;
	   for (int i = 0; i < l - 2; i ++) sum += bytes[i] & 0xFF;
	   return (((byte) ((sum >> 8) & 0xFF) == bytes[l-1])
	   && ((byte) (sum & 0xFF)) == bytes[l-2]);
	 }
   private void writeToFile(ArrayList<Byte> list, String filepath) {
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String pref = Long.toString(timestamp.getTime());
		
		byte[] bytes = new byte[list.size()];
		for(int i = 0; i < list.size(); i++) {
			bytes[i] = list.get(i).byteValue();
		}
		
		try {
		   File file = new File(filepath + pref + "out.dat");
           OutputStream os = new FileOutputStream(file); 
           os.write(bytes); 
           os.close(); 
       } 
       catch (Exception e) {}
		
   }
   
   public void saveData(ArrayList<Byte> message) throws IOException, ParseException {
		ArrayList<Byte> allBytes=message;
		byte[] bytes = new byte[allBytes.size()];
		for(int i = 0; i < allBytes.size(); i++) {
			bytes[i] = allBytes.get(i).byteValue();
		}
		boolean checkSumCheck= checkSumOk(bytes); 
		if (checkSumCheck==true) {
		
			//writeToFile(message, "C:/Users/localadmin/Documents/out/");
		    //separating header bytes
		    ArrayList<Byte>header=new ArrayList<Byte>();
		    Byte currByte=null;
		    for (Byte b : allBytes) {
		    	if(b==0) {
		    		if(currByte==b) {
		    			header.remove(header.size()-1);
		    			break;
		    		}
		    	}
		    	header.add(b);
		    	currByte=b;
		    }
		    int assembly = ((header.get(3)& 0xFF)*256) + (header.get(2)& 0xFF);
		    int offset1 = ((header.get(7)& 0xFF)*256) + (header.get(6)& 0xFF);
		    int offset2 = ((header.get(9)& 0xFF)*256) + (header.get(8)& 0xFF);
		    int offset3 = ((header.get(11)& 0xFF)*256) + (header.get(10)& 0xFF);
		    int offset4 = ((header.get(13)& 0xFF)*256) + (header.get(12)& 0xFF);
		    int offset5 = ((header.get(15)& 0xFF)*256) + (header.get(14)& 0xFF);
		    int offset6 = ((header.get(17)& 0xFF)*256) + (header.get(16)& 0xFF);
		    // separating FIXED LEADER DATA
		    
		    ArrayList<Byte>fixedLaderData=new ArrayList<Byte>();
		    for (int i =offset1 ;i<offset2;i++) {	
		    	fixedLaderData.add(allBytes.get(i));
		    }	
		  
		    // separating VARIABLE LEADER DATA
		    
		    ArrayList<Byte>variableLaderData=new ArrayList<Byte>();
		    for (int i =offset2 ;i<offset3;i++) {	
		    	variableLaderData.add(allBytes.get(i));
		    }	
		    
		    //separating VELOCITY
		    
		    ArrayList<Byte>velocity=new ArrayList<Byte>();
		    for (int i =offset3 ;i<offset4;i++) {	
		    	velocity.add(allBytes.get(i));
		    }	
		    
		    // separating CORRELATION MAGNITUDE
	
		    ArrayList<Byte>correlationMagnitude=new ArrayList<Byte>();
		    for (int i =offset4 ;i<offset5;i++) {	
		    	correlationMagnitude.add(allBytes.get(i));
		    }	
		    
		    //separating ECHO INTENSITY
		    
		    ArrayList<Byte>echoIntensity=new ArrayList<Byte>();
		    for (int i =offset5 ;i<offset6;i++) {	
		    	echoIntensity.add(allBytes.get(i));
		    }	
		    
		    //separating PERCENT GOOD
		    
		    ArrayList<Byte>precentGood=new ArrayList<Byte>();
		    for (int i =offset6 ;i<allBytes.size();i++) {	
		    	precentGood.add(allBytes.get(i));
		    }		    
		    
		    //geting config
		    
		    FileReader reader = new FileReader("config");
			Properties properties = new Properties();
			properties.load(reader);
			String davID = properties.getProperty("P_DAV_ID");
			String tipas = properties.getProperty("D_TIPAS");
			String pavadinimas = properties.getProperty("D_PAVADINIMAS");
			String kordTipas = properties.getProperty("P_KOORD_TIPAS");
			String kordX = properties.getProperty("D_KOORDX");
			String kordY = properties.getProperty("D_KOORDY");
		      
		    	
		    	short ensembleNumber = (short) ((variableLaderData.get(3) << 8) | (variableLaderData.get(2) & 0xFF));
		    	
		    	DateConverter newDate = new DateConverter(variableLaderData.get(4), variableLaderData.get(5), variableLaderData.get(6), variableLaderData.get(7), variableLaderData.get(8), variableLaderData.get(9), variableLaderData.get(10));
		        
		        short temperature = (short) ((variableLaderData.get(27) << 8) | (variableLaderData.get(26) & 0xFF));
		        double temperatureFixed = temperature;
		        
		        double deapth = ((variableLaderData.get(17)& 0xFF)*256) + (variableLaderData.get(16)& 0xFF);
		        
		        double heading = (((variableLaderData.get(19)& 0xFF)*256) + (variableLaderData.get(18)& 0xFF));
		        
		        short pitch = (short) ((variableLaderData.get(21) << 8) | (variableLaderData.get(20) & 0xFF));
		        double pitchFixed = pitch ;
		        
		        short rool = (short) ((variableLaderData.get(23) << 8) | (variableLaderData.get(22) & 0xFF));
		        double roolFixed = rool ;
		        
		        short celiuKiekis=fixedLaderData.get(9);
		        
		        short blankAfterTransmit = (short) ((fixedLaderData.get(15) << 8) | (fixedLaderData.get(14) & 0xFF));
	
		        short deapthCellLenght = (short) ((fixedLaderData.get(13) << 8) | (fixedLaderData.get(12) & 0xFF));
		        
		        
		        int bit = 2;
		        ArrayList<Data> allData= new ArrayList<Data>();
		        for(int i =0; i <celiuKiekis;i++) {
		        	Data dataObject = new Data();
		        	dataObject.setDavID(Integer.parseInt(davID));
		        	dataObject.setPaketoNR(ensembleNumber);
		        	dataObject.setpLAIKAS(newDate.GetDate());
		        	dataObject.setTemparatura(temperature);
		        	dataObject.setGylis(deapth);
		        	dataObject.setKriptis(heading);
		        	dataObject.setNuolidis(pitchFixed);
		        	dataObject.setPasvirimas(roolFixed);
		        	dataObject.setCeliuKiekis(celiuKiekis);
		        	dataObject.setTipas(Integer.parseInt(tipas));
		        	dataObject.setPavadinimas(pavadinimas);
		        	dataObject.setAtstumas(blankAfterTransmit);
		        	dataObject.setAtstumasBin(deapthCellLenght);
		        	//data.setGedimas(gedimas);
		        	dataObject.setKordTipas(kordTipas);
		        	dataObject.setKordX((kordX));
		        	dataObject.setKordY((kordY));
		        	dataObject.setEilNr(i);
		        	dataObject.setGreitis1(((short) ((velocity.get(bit+1) << 8) | (velocity.get(bit) & 0xFF))));
			    	bit+=2;
			    	dataObject.setGreitis2(((short) ((velocity.get(bit+1) << 8) | (velocity.get(bit) & 0xFF))));
			    	bit+=2;
			    	dataObject.setGreitis3(((short) ((velocity.get(bit+1) << 8) | (velocity.get(bit) & 0xFF))));
			    	bit+=2;
			    	dataObject.setGreitis4(((short) ((velocity.get(bit+1) << 8) | (velocity.get(bit) & 0xFF))));
			    	bit+=2;
			    	
			    	allData.add(dataObject);
		        }
		        rep.saveAll(allData);
		}
		else {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.println(timestamp.getTime());
		}
	       

   }
	
   public void postHidroData(Data data) {
		rep.save(data);
	}
	public List<Data> getAllHidroData(){
		return rep.findAll();
	}
	public Optional<Data> getOneHidroData(Integer id){
		return rep.findById(id);
	}
	public void deleteHidroData(Integer id) {
		rep.deleteById(id);
	}
	public List<VelocityData> getAllVelocityData() throws ParseException{ 
		Date currentDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12));
		List<Object[]> allData = rep.findAllData(currentDate);
		ArrayList<VelocityData> velocity=new ArrayList<VelocityData>();
		for(Object[] object : allData) {
			 double rytai = new Double((int) object[0]);
			 double siaure = new Double((int) object[1]);
			 double greitis = Math.sqrt((Math.pow(rytai*0.0019438, 2)+Math.pow(siaure*0.0019438, 2)));

			 double kampas =Math.toDegrees(Math.atan(Math.abs(rytai/siaure)));

		        if(siaure>=0 && rytai<0) {
		        	kampas = 180-kampas;
		        }
		        else if(siaure<=0 && rytai<0) {
		        	kampas = 180+kampas;
		        }
		        else {
		        	kampas =270+kampas;
		        }
			     Calendar cal = Calendar.getInstance();
			     // remove next line if you're always using the current time.
			     cal.setTime((Date) object[2]);
			     cal.add(Calendar.HOUR, -1);
			     Date oneHourBack = cal.getTime();
			     velocity.add(new VelocityData(DoubleRounder.round(greitis, 2), kampas, oneHourBack));
		    // System.out.println("Greitis mazgais:"+DoubleRounder.round(greitis,3)+"   vandens kritptis laipsniais:"+kampas+"     Data:"+object[2]);
		}
		return  velocity;
	}
   
}
