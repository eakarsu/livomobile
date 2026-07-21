package tr.com.eno.livo.cloud.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import tr.com.eno.livo.cloud.delegate.PlatformDelegate;
import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.Companies;
import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.entity.WebUser;
import tr.com.eno.livo.cloud.utility.AppConstants;
import tr.com.eno.livo.cloud.viewBean.LoginBean;
import tr.com.eno.livo.cloud.viewBean.SignUpBean;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsItemType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

@Controller
public class LoginController {
	
	public static String forgotToken = "token";
	
	@Autowired
	private UserDelegate userDelegate;
	
	@Autowired
	private PlatformDelegate platformDelegate;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView displayLogin(HttpServletRequest request, HttpServletResponse response, LoginBean loginBean) {

		request.getSession().setAttribute("userMail", loginBean.getUserMail());
		System.out.println("login: " + loginBean.getUserMail());
		ModelAndView model = new ModelAndView("login");
		// LoginBean loginBean = new LoginBean();
		model.addObject("loginBean", loginBean);

		return model;
	}
	
	@RequestMapping(value = "/termsofuse", method = RequestMethod.GET)
	public ModelAndView displayTermsofUse(HttpServletRequest request, HttpServletResponse response, @RequestParam("email") String email) {

		System.out.println("displayTermsofUse: " + email);
		SignUpBean signUpBean = new SignUpBean();
		signUpBean.setEmail(email);
		ModelAndView model = new ModelAndView("termsofuse");
		model.addObject("email", email);
		model.addObject("signUpBean", signUpBean);

		return model;
	}
	
	@RequestMapping(value = "/myprofile", method = RequestMethod.GET)
	public ModelAndView displayMyProfile(HttpServletRequest request, HttpServletResponse response) {

		String email = (String) request.getSession().getAttribute("userMail");
		User user = null;
		try {
			user = userDelegate.getUserByEmail(email);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("displayMyProfile: " + email);
		
		ModelAndView model = new ModelAndView("myprofile");
		request.getSession().setAttribute("loggedInUser", user);

		return model;
	}
	
	@RequestMapping(value = "/pricing", method = RequestMethod.GET)
	public ModelAndView displayPricing(HttpServletRequest request, HttpServletResponse response, LoginBean loginBean) {

		ModelAndView model = null;

		try {

			PaymentDetailsType paymentDetails = new PaymentDetailsType();
			paymentDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
			
			BasicAmountType amt = new BasicAmountType();
			amt.setCurrencyID(CurrencyCodeType.fromValue("USD"));
			amt.setValue(String.valueOf(90.00));
			
			PaymentDetailsItemType item = new PaymentDetailsItemType();
			item.setQuantity(1);
			item.setName("monthly");
			item.setAmount(amt);
			
			List<PaymentDetailsItemType> monthlyItems = new ArrayList<PaymentDetailsItemType>();
			monthlyItems.add(item);
			paymentDetails.setPaymentDetailsItem(monthlyItems);
			
			BasicAmountType orderTotal = new BasicAmountType();
			orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
			orderTotal.setValue(String.valueOf(90.00));
			paymentDetails.setOrderTotal(orderTotal);
			List<PaymentDetailsType> monthlyPaymentDetailsList = new ArrayList<PaymentDetailsType>();
			monthlyPaymentDetailsList.add(paymentDetails);

			SetExpressCheckoutRequestDetailsType expressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
			expressCheckoutRequestDetails.setReturnURL(AppConstants.CLOUD_SERVER_ADDRESS + AppConstants.PAYPAL_PAYMENT_SUCCESS_METHOD);
			expressCheckoutRequestDetails.setCancelURL(AppConstants.CLOUD_SERVER_ADDRESS + AppConstants.PAYPAL_PAYMENT_CANCEL_METHOD);

			expressCheckoutRequestDetails.setPaymentDetails(monthlyPaymentDetailsList);

			SetExpressCheckoutRequestType expressCheckoutRequest = new SetExpressCheckoutRequestType();
			expressCheckoutRequest.setSetExpressCheckoutRequestDetails(expressCheckoutRequestDetails);
			expressCheckoutRequest.setVersion("104.0");

			SetExpressCheckoutReq monthlyExpressCheckoutRequest = new SetExpressCheckoutReq();
			monthlyExpressCheckoutRequest.setSetExpressCheckoutRequest(expressCheckoutRequest);
			
			paymentDetails = new PaymentDetailsType();
			paymentDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
			
			amt = new BasicAmountType();
			amt.setCurrencyID(CurrencyCodeType.fromValue("USD"));
			amt.setValue(String.valueOf(720.00));
			List<PaymentDetailsItemType> annualItems = new ArrayList<PaymentDetailsItemType>();
			item = new PaymentDetailsItemType();
			item.setQuantity(1);
			item.setName("annual");
			item.setAmount(amt);
			annualItems.add(item);
			
			paymentDetails.setPaymentDetailsItem(annualItems);
			orderTotal = new BasicAmountType();
			orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
			orderTotal.setValue(String.valueOf(720.00));
			paymentDetails.setOrderTotal(orderTotal);
			
			List<PaymentDetailsType> annualPaymentDetailsList = new ArrayList<PaymentDetailsType>();
			annualPaymentDetailsList.add(paymentDetails);
			
			expressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
			expressCheckoutRequestDetails.setReturnURL(AppConstants.CLOUD_SERVER_ADDRESS + AppConstants.PAYPAL_PAYMENT_SUCCESS_METHOD);
			expressCheckoutRequestDetails.setCancelURL(AppConstants.CLOUD_SERVER_ADDRESS + AppConstants.PAYPAL_PAYMENT_CANCEL_METHOD);
			expressCheckoutRequestDetails.setPaymentDetails(annualPaymentDetailsList);
			
			expressCheckoutRequest = new SetExpressCheckoutRequestType();
			expressCheckoutRequest.setSetExpressCheckoutRequestDetails(expressCheckoutRequestDetails);
			expressCheckoutRequest.setVersion("104.0");
			
			SetExpressCheckoutReq annualExpressCheckoutRequest = new SetExpressCheckoutReq();
			annualExpressCheckoutRequest.setSetExpressCheckoutRequest(expressCheckoutRequest);


			Map<String, String> sdkConfig = new HashMap<String, String>();
			sdkConfig.put("mode", "live");
			sdkConfig.put("acct1.UserName", System.getenv("LIVOMOBILE_PAYPAL_USERNAME"));
			sdkConfig.put("acct1.Password", System.getenv("LIVOMOBILE_PAYPAL_PASSWORD"));
			sdkConfig.put("acct1.Signature", System.getenv("LIVOMOBILE_PAYPAL_SIGNATURE"));
			PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
			String sandboxUrl = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=", monthlyURL = "", annualURL = "";
			try {

				monthlyURL = sandboxUrl + service.setExpressCheckout(monthlyExpressCheckoutRequest).getToken();
				annualURL = sandboxUrl + service.setExpressCheckout(annualExpressCheckoutRequest).getToken();
				System.out.println("monthlyURL: " + service.setExpressCheckout(monthlyExpressCheckoutRequest).getToken());
				System.out.println("annualURL: " + service.setExpressCheckout(annualExpressCheckoutRequest).getToken());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 30);
			String email = (String) request.getSession().getAttribute("userMail");
			User user = (User) request.getSession().getAttribute("loggedInUser");
			System.out.println("displayPricing: " + email);
			Companies currCompany = new Companies(null, user != null ? user.getCompanyName() : null, user != null ? user.getCompanyName() : null, email != null ? email.hashCode() : null,  new Date().getTime(), true);
			currCompany.setPassword( user != null ? user.getUserPassword() : null);
			WebUser wUser = new WebUser();
			wUser.setUserId( user != null ? user.getEmail().hashCode() : -1);
			wUser.setUserName( user != null ? user.getEmail() : null);
			wUser.setUserMail( user != null ? user.getEmail() : null);
			wUser.setCompanyList( user != null ? String.valueOf(user.getCompanyName().hashCode()) : "");
			wUser.setExpirationDate( calendar.getTime().getTime() );
			wUser.setUserPassword( user != null ? user.getUserPassword() : null);
			
			ObjectMapper objectMapper = new ObjectMapper();
			System.out.println("displayPricing: " + objectMapper.writeValueAsString(currCompany));
			
			Platform platform = platformDelegate.getPlatformListByUser(user);

			model = new ModelAndView("pricing");
			model.addObject("redirectPaypal", true);
			model.addObject("redirectPaypalUrlforMonthly", monthlyURL);
			model.addObject("redirectPaypalUrlforAnnual", annualURL);
			model.addObject("currCompany", objectMapper.writeValueAsString(currCompany));
			model.addObject("wUser", objectMapper.writeValueAsString(wUser));
			model.addObject("platform", platform != null ? objectMapper.writeValueAsString(platform) : null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView executeLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@ModelAttribute("loginBean") LoginBean loginBean) {
		ModelAndView model = null;

		try {
			User isValidUser = userDelegate.isValidUser(loginBean.getUserMail(), loginBean.getPassword(), 3);
			System.out.println("User Login --> " + isValidUser.getUserId() + " -- " + loginBean.getUserMail() + " -- " + loginBean.getPassword());
			if (isValidUser.getUserId() != 0) {
				System.out.println("User Login Successful");
				
				request.getSession().setAttribute("loggedInUser", isValidUser);
				request.getSession().setAttribute("userMail", loginBean.getUserMail());
				
				model = new ModelAndView("dashboard");
			 
				System.out.println("User set Session : " + loginBean.getUserMail());
			} else {
				model = new ModelAndView("login");
				request.setAttribute("message", "Invalid credentials!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public ModelAndView forgotPassword(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@ModelAttribute("email") String email) {
		ModelAndView model = null;

		try {
			
			String remoteAddress = AppConstants.CLOUD_SERVER_ADDRESS.substring(7);
			forgotToken = AppConstants.createToken();
			String activationURL = "http://" + remoteAddress + "/resetPassword?email=" + email + "&vcode=" + forgotToken;

			// send email content must be prepare.
			String emailContent = AppConstants.FORGOT_PASSWORD.replace(":activationURL", activationURL);

			// send email to usermail
			AppConstants.SendSimpleMessage(email, emailContent, "LivoMobile - Forgot Password");
			
			LoginBean loginBean = new LoginBean();
			loginBean.setUserMail(email);
			model = new ModelAndView("login");
			model.addObject("loginBean", loginBean);
			request.setAttribute("forgotPasswordMessage", "The email is sent to " + email + ". Please check your email and change your password by following the process.");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public ModelAndView resetPassword(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@ModelAttribute("email") String email, @ModelAttribute("vcode") String vcode) {
		ModelAndView model = null;

		try {
			
			LoginBean loginBean = new LoginBean();
			loginBean.setUserMail(email);
			model = new ModelAndView("login");
			model.addObject("loginBean", loginBean);
			model.addObject("email", email);
			model.addObject("vcode", vcode);
			
			if(vcode.equals(forgotToken)){
				request.setAttribute("resetMessage", "valid");
			}
			else request.setAttribute("resetMessage", "Verification code is invalid");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public ModelAndView resetPasswordProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@ModelAttribute("email") String email, @ModelAttribute("vcode") String vcode, @ModelAttribute("newPassword") String newPassword) {
		ModelAndView model = null;

		try {
			
			LoginBean loginBean = new LoginBean();
			loginBean.setUserMail(email);
			model = new ModelAndView("login");
			model.addObject("loginBean", loginBean);
			
			if(vcode.equals(forgotToken)){
				User user = userDelegate.getUserByEmail(email);
				user.setUserPassword(newPassword);
				userDelegate.updateUser(user);
				request.setAttribute("resetMessage", "Your password is successfuly changed!");
			}
			else request.setAttribute("resetMessage", "Verification code is invalid");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
}
