package tr.com.eno.livo.cloud.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tr.com.eno.livo.cloud.viewBean.SignUpBean;

@Controller
public class HomeController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView displayHomePage(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {
		
		ModelAndView model = new ModelAndView("home");
		model.addObject("homeBean", signUpBean);

		return model;
	}

}
