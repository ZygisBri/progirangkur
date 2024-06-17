package praktikosDarbas.service;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class PortRead{
	@Autowired
	SerialReader read;

	
	public PortRead()
	{	
		super();
	}
	public void connect ( String portName ) throws Exception
	{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if ( portIdentifier.isCurrentlyOwned() )
		{
			System.out.println("Error: Port is currently in use");
		}
		else
		{
			CommPort commPort = portIdentifier.open(this.getClass().getName(),9000);

			if ( commPort instanceof SerialPort )
			{
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();
				
				read.in = in;
				read.run();
				(new Thread(new SerialReader(in))).start();
				//(new Thread(new SerialWriter(out))).start();
			}
			else
			{
				System.out.println("Error");
			}
		}     
	}

	/** Reading Data */

	

	/** Write 
	    public static class SerialWriter implements Runnable 
	    {
	        OutputStream out;

	        public SerialWriter ( OutputStream out )
	        {
	            this.out = out;
	        }

	        public void run ()
	        {
	            try
	            {                
	                int c = 0;
	                while ( ( c = System.in.read()) > -1 )
	                {
	                    this.out.write(c);
	                }                
	            }
	            catch ( IOException e )
	            {
	                e.printStackTrace();
	            }            
	        }
	    }
	 */

}
