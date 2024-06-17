package praktikosDarbas.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SerialReader implements Runnable 
{	
	@Autowired
	DataService serv;
	
	InputStream in;
	
	public SerialReader ()
	{	
	}
	public SerialReader ( InputStream in )
	{	
		System.out.println("in");
		this.in = in;
	}

	public void run ()
	{	
		byte[] buffer = new byte[1];
		int len = -1;
		try
		{	
			ArrayList<Byte> allBytes= new ArrayList<Byte>();
		while ( ( len = this.in.read(buffer)) > -1 )
		{	
			if(len>0) {
					allBytes.add(buffer[0]);
			}else
			{	
				if(allBytes.size()!=0) {
					serv.saveData(allBytes);
					allBytes=new ArrayList<Byte>();
				}
			}


		}
		}
		catch ( IOException | ParseException e )
		{
			e.printStackTrace();
		}            
	}
}
