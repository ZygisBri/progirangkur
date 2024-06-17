package praktikosDarbas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import praktikosDarbas.service.MeteoConection;
import praktikosDarbas.service.PortRead;


@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
public class PraktikosDarbasApplication {
	@Autowired
	PortRead port;
	@Autowired
	MeteoConection meteo;
	public static void main(String[] args) throws Exception {
		SpringApplication.run(PraktikosDarbasApplication.class, args);
		
		
	}
	@EventListener(ApplicationReadyEvent.class)
	public void connect() throws Exception {
		port.connect("COM2");
	}
	
	
}
