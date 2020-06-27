package tr.com.eno.livo.cloud.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tr.com.eno.livo.cloud.delegate.SupportDelegate;
import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.viewBean.SignUpBean;
import tr.com.eno.livo.cloud.viewBean.SupportBean;

@Controller
public class ApplicationController {
	
	@Autowired
	private UserDelegate userDelegate;
	
	@Autowired
	private SupportDelegate supportDelegate;
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public ModelAndView displayDashboard(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {
		 
		ModelAndView model = new ModelAndView("dashboard");
		
		return model;
	}

	@RequestMapping(value = "/licensing", method = RequestMethod.GET)
	public ModelAndView displayLicensing(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {

		User loggedInUser = (User) request.getSession().getAttribute("loggedInUser");
		ModelAndView model = new ModelAndView("licensing");
		model.addObject("user", loggedInUser);
		return model;
	}
	
	@RequestMapping(value = "/support_request", method = RequestMethod.GET)
	public ModelAndView supportRequest(HttpServletRequest request, HttpServletResponse response, SupportBean supportBean) {

		User loggedInUser = (User) request.getSession().getAttribute("loggedInUser");
		ModelAndView model = new ModelAndView("support");
		model.addObject("user", loggedInUser);
		return model;
		
	}
	
	@RequestMapping(value = "/process_support", method = RequestMethod.POST)
	public ModelAndView processSupportRequest(HttpServletRequest request, HttpServletResponse response, SupportBean supportBean) {

		User loggedInUser = (User) request.getSession().getAttribute("loggedInUser");
		
		try {
			supportDelegate.insertSupport(supportBean.getUserEmail(), supportBean.getTitle(), supportBean.getDescription(), loggedInUser.getUserId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView("support");
		model.addObject("user", loggedInUser);
		model.addObject("supportMessage", "Your Support Message is sended!");
		return model;
	}
 
}
