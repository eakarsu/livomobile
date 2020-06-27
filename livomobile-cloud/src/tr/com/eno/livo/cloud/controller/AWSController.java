package tr.com.eno.livo.cloud.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.com.eno.livo.cloud.viewBean.AWSBean;

@Controller
public class AWSController {


	@RequestMapping(value = "/createInstance", method = RequestMethod.POST)
	@ResponseBody
	public String createInstance(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("createInstances is called.");
		String ipAddress = "127.0.0.1";

		AWSBean awsBean = new AWSBean();
//		awsBean.init();

//		try {
//			ipAddress = awsBean.createInstance();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return ipAddress;
	}

}
