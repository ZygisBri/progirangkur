package praktikosDarbas.controller;

import java.text.ParseException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	@CrossOrigin(origins = "*")
	 @RequestMapping("/site/main")
		public String mainHtml() throws ParseException {
		return "main";
		}
	 
	 
}
