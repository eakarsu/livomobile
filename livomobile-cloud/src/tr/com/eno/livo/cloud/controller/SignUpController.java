package tr.com.eno.livo.cloud.controller;

import java.net.InetAddress;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import tr.com.eno.livo.cloud.delegate.PendingRequestDelegate;
import tr.com.eno.livo.cloud.delegate.PlatformDelegate;
import tr.com.eno.livo.cloud.delegate.TransactionHistoryDelegate;
import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.Companies;
import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.TransactionHistory;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.entity.WebUser;
import tr.com.eno.livo.cloud.utility.AppConstants;
import tr.com.eno.livo.cloud.viewBean.LoginBean;
import tr.com.eno.livo.cloud.viewBean.SendEmailBean;
import tr.com.eno.livo.cloud.viewBean.SignUpBean;
import tr.com.eno.livo.cloud.viewBean.UserBean;
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
import botdetect.web.Captcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;

@Controller
public class SignUpController {

	@Autowired
	private UserDelegate userDelegate;

	@Autowired
	private PendingRequestDelegate pendingRequestDelegate;
	
	@Autowired
	private PlatformDelegate platformDelegate;
	
	@Autowired
	private TransactionHistoryDelegate transactionHistoryDelegate;

	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public ModelAndView executeSignUp(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("signUpBean") SignUpBean signUpBean) {
		ModelAndView model = null;
		String email = signUpBean.getEmail();
		String name = signUpBean.getName();

		try {
			// User Control from db
			User user = userDelegate.getUserByEmail(email);
			if (user.getUserId() != 0) {

				System.out.println("This email address has registered. : " + email);

				LoginBean loginBean = new LoginBean();
				loginBean.setUserMail(email);
				model = new ModelAndView("login");
				model.addObject("loginBean", loginBean);
				model.addObject("isRegisterForm", true);
				model.addObject("isRegisteredEmail", true);

				model.addObject("userControlResponse", "This email address has registered. : " + email);

				return model;
			}

			// insert new user for signup
			boolean isCreateUser = userDelegate.insertUser(name, email);
			if (isCreateUser) {
				System.out.println("User Sign Up Successful");
				// User user = userDelegate.getUserByEmail(email);
				// Added user to session.
				request.getSession().setAttribute("loggedInUser", user);
				request.getSession().setAttribute("userMail", email);

				System.out.println("User set Session : " + email);

				model = new ModelAndView("userInfo");
				model.addObject("name", name);
				model.addObject("email", email);

			} else {
				System.out.println("User cannot be created.");
				model = new ModelAndView("index");
				request.setAttribute("message", "Invalid credentials!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/viewSignUp", method = RequestMethod.GET)
	public ModelAndView viewSignUp(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;

		LoginBean loginBean = new LoginBean();
		model = new ModelAndView("login");
		model.addObject("loginBean", loginBean);
		model.addObject("isRegisterForm", true);

		return model;
	}

	@RequestMapping(value = "/updateUserInfos", method = RequestMethod.POST)
	public ModelAndView updateUserInfos(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("userBean") UserBean userBean) {
		ModelAndView model = null;
		Map<String, String> messages = new HashMap<String, String>();
		request.setAttribute("messages", messages);

		String name = userBean.getName();
		String email = userBean.getEmail();
		String companyName = userBean.getCompanyName();
		String userPassword = userBean.getUserPassword();
		System.out.println("updateUserInfos params : " + name + "  -  " + email + "  -  " + companyName + "  -  ");
		Captcha captcha = Captcha.load(request, "formCaptcha");

		HttpSession session = request.getSession(true);

		// validate the Captcha to check we're not dealing with a bot
		boolean isHuman = captcha.validate(request, request.getParameter("captchaCodeTextBox"));
		if (isHuman) {
			// Captcha validation passed, perform protected action
			session.setAttribute("isCaptchaValid", true);
		} else {
			// Captcha validation failed, show error message
			model = new ModelAndView("userInfo");
			model.addObject("email", email);
			model.addObject("name", name);
			model.addObject("isUpdated", false);
			model.addObject("responseText", "Your account infos can not be updated.");
			model.addObject("captchaValidateError", true);
			model.addObject("inCorrectCaptcha", "Captcha could not be verified.");
			return model;
		}

		try {
			// User Control from db
			System.out.println("getUserByEmail() is called with usermail : " + email);
			User user = userDelegate.getUserByEmail(email);
			// update user infos
			System.out.println("getUserByEmail() is complated with userId : " + user.getUserId());

			user.setName(name);
			user.setCompanyName(companyName);
			user.setUserPassword(userPassword);

			boolean isUpdatedUser = userDelegate.updateUser(user);
			if (isUpdatedUser) {
				System.out.println("User is updated, Successfull");

				SendEmailBean sendEmailBean = new SendEmailBean();
				sendEmailBean.setEmail(email);

				// create token for email verified.
				String token = AppConstants.createToken();

				// email and token must be inserted to pending_requests table --status 0 pasive ,1 active, 2 used
				boolean isInsertToken = pendingRequestDelegate.insertToken(email, token);
				if (isInsertToken) {
					System.out.println("Token is inserted to pending_requests table.");

				} else {
					System.out.println("Token cannot be inserted to pending_requests table.");
				}
				// send email content must be prepare.
				InetAddress IP = InetAddress.getLocalHost();
				System.out.println("IP of my system is --> " + IP.getHostAddress() + " -- " + IP.getHostName() + " -- " + IP.getCanonicalHostName());
				String remoteAddress = AppConstants.CLOUD_SERVER_ADDRESS.substring(7);
				String emailContent = PrepareEmailContent(email, token, remoteAddress);

				// send email to usermail
				ClientResponse sendMailResponse = AppConstants.SendSimpleMessage(email, emailContent, AppConstants.MAIL_SUBJECT);

				model = new ModelAndView("verifyEmail");
				model.addObject("email", email);
				model.addObject("sendEmailBean", sendEmailBean);

				// SignUpBean signUpBean = new SignUpBean();
				// signUpBean.setEmail(email);

				// model = new ModelAndView("termsofuse");
				// model.addObject("email", email);
				// model.addObject("signUpBean", signUpBean);
				// model.addObject("isUpdated", true);
				// model.addObject("responseText", "Your account infos were updated.");

			} else {
				System.out.println("User cannot be updated.");
				model = new ModelAndView("userInfo");
				model.addObject("email", email);
				model.addObject("name", name);
				model.addObject("isUpdated", false);
				model.addObject("responseText", "Your account infos can not be updated.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/updateUserProfile", method = RequestMethod.POST)
	public ModelAndView updateUserProfile(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("userBean") UserBean userBean) {
		ModelAndView model = null;
		Map<String, String> messages = new HashMap<String, String>();
		request.setAttribute("messages", messages);

		String name = userBean.getName();
		String email = userBean.getEmail();
		String companyName = userBean.getCompanyName();
		String userPassword = userBean.getUserPassword();
		System.out.println("updateUserInfos params : " + name + "  -  " + email + "  -  " + companyName + "  -  ");
		Captcha captcha = Captcha.load(request, "formCaptcha");

		HttpSession session = request.getSession(true);

		// validate the Captcha to check we're not dealing with a bot
		boolean isHuman = captcha.validate(request, request.getParameter("captchaCodeTextBox"));
		if (isHuman) {
			// Captcha validation passed, perform protected action
			session.setAttribute("isCaptchaValid", true);
		} else {
			// Captcha validation failed, show error message
			model = new ModelAndView("userInfo");
			model.addObject("email", email);
			model.addObject("name", name);
			model.addObject("isUpdated", false);
			model.addObject("responseText", "Your account infos can not be updated.");
			model.addObject("captchaValidateError", true);
			model.addObject("inCorrectCaptcha", "Captcha could not be verified.");
			return model;
		}

		try {
			// User Control from db
			System.out.println("getUserByEmail() is called with usermail : " + email);
			User user = userDelegate.getUserByEmail(email);
			// update user infos
			System.out.println("getUserByEmail() is complated with userId : " + user.getUserId());

			user.setName(name);
			user.setCompanyName(companyName);
			user.setUserPassword(userPassword);

			boolean isUpdatedUser = userDelegate.updateUser(user);
			if (isUpdatedUser) {
				System.out.println("User is updated, Successfull");

				model = new ModelAndView("myprofile");
				request.getSession().setAttribute("loggedInUser", user);
				
			} else {
				System.out.println("User cannot be updated.");
				model = new ModelAndView("userInfo");
				model.addObject("email", email);
				model.addObject("name", name);
				model.addObject("isUpdated", false);
				model.addObject("responseText", "Your account infos can not be updated.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	@RequestMapping(value = "/verifyEmail", method = RequestMethod.GET)
	public ModelAndView verifyEmail(HttpServletRequest request, HttpServletResponse response, @RequestParam("email") String email,
			@RequestParam("token") String token) {
		ModelAndView model = null;
		System.out.println("Email : " + email + " token : " + token);
		SignUpBean signUpBean = new SignUpBean();
		signUpBean.setEmail(email);

		try {
			// Token must be control.
			boolean isValidToken = pendingRequestDelegate.isValidToken(email, token);
			if (isValidToken) {

				// loginDelegate.updateUser(user);
				System.out.println("Token is valid.");
				// User state verified-inactive
				userDelegate.updateUserStateByEmail(email, AppConstants.UserState.VERIFIED_INACTIVE.value);
				request.getSession().setAttribute("loggedInUser", userDelegate.getUserByEmail(email));
				request.getSession().setAttribute("userMail", email);

				model = new ModelAndView("termsofuse");
				model.addObject("email", email);
				model.addObject("signUpBean", signUpBean);

			} else {
				System.out.println("Token is not valid.");
				model = new ModelAndView("error");
				model.addObject("errorText", "Your email link has been expired.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	@RequestMapping(value = "/termsVerified", method = RequestMethod.POST)
	public ModelAndView termsVerified(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("signUpBean") SignUpBean signUpBean) {
		ModelAndView model = null;

		// Token must be control
		try {
			System.out.println("termsVerified started with email : " + signUpBean.getEmail());

			// user update state set 2 where email address
			userDelegate.updateUserStateByEmail(signUpBean.getEmail(), AppConstants.UserState.VERIFIED_ACTIVE.value);
			User user = userDelegate.getUserByEmail(signUpBean.getEmail());
			request.getSession().setAttribute("loggedInUser", user);

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
			
			System.out.println("termsVerified: " + annualItems.size());
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

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 30);
			String email = (String) request.getSession().getAttribute("userMail");
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
			
			Platform platform = platformDelegate.getPlatformListByUser(user);
			
			ObjectMapper objectMapper = new ObjectMapper();
			System.out.println("displayPricing: " + objectMapper.writeValueAsString(currCompany));

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

	@RequestMapping(value = "/resendEmail", method = RequestMethod.GET)
	public ModelAndView resendEmail(HttpServletRequest request, HttpServletResponse response, @RequestParam("resendEmail") String resendEmail,
			@ModelAttribute("sendEmailBean") SendEmailBean sendEmailBean) {
		ModelAndView model = null;
		// String email = sendEmailBean.getEmail();
		// String name = signUpBean.getName();
		System.out.println("resendEmail : " + resendEmail);
		try {
			String token = AppConstants.createToken();

			// email and token must be inserted to pending_requests table --status 0 pasive ,1 active, 2 used
			boolean isInsertToken = pendingRequestDelegate.insertToken(resendEmail, token);
			if (isInsertToken) {
				System.out.println("Token is inserted to pending_requests table.");

			} else {
				System.out.println("Token cannot be inserted to pending_requests table.");
			}
			// send email content must be prepare.
			//InetAddress IP = InetAddress.getLocalHost();
			String remoteAddress = AppConstants.CLOUD_SERVER_ADDRESS.substring(7);
			String emailContent = PrepareEmailContent(resendEmail, token, remoteAddress);

			// send email to usermail
			ClientResponse sendMailResponse = AppConstants.SendSimpleMessage(resendEmail, emailContent, AppConstants.MAIL_SUBJECT);
				
			model = new ModelAndView("verifyEmail");
			model.addObject("email", resendEmail);
			model.addObject("sendEmailBean", sendEmailBean);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/sendEmail4Complete", method = RequestMethod.POST)
	public ModelAndView sendEmail4Complete(HttpServletRequest request, HttpServletResponse response, @RequestParam("email") String email, @RequestParam("free") Boolean free) {
		ModelAndView model = null;
		System.out.println("sendEmail4Complete ---> " + email + " -- " + free);
		Platform platform = null;
		User user = (User) request.getSession().getAttribute("loggedInUser");
		if(free){

			platform = new Platform();
			platform.setPlatformName(UUID.randomUUID().toString());
			platform.setCloudProvider("Amazon");
			platform.setUserId(user.getUserId());
			platform.setPlatformType(AppConstants.PlatformType.STARTER.text);
			
			try {
				platformDelegate.insertPlatform(platform);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// email and token must be inserted to pending_requests table --status 0 pasive ,1 active, 2 used
			boolean updatedStatus = false;
			try {
				System.out.println("updatedStatus ---> " + email);
				updatedStatus = pendingRequestDelegate.updateStatus(email, 2);
				if (updatedStatus) {
					System.out.println("status is updated to pending_requests table.");

				} else {
					System.out.println("status cannot be updated to pending_requests table.");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// transactionHistory insert
			// Seats and infrastructure

			TransactionHistory transactionHistory = new TransactionHistory();
			transactionHistory.setAmount(String.valueOf(0.00));
			transactionHistory.setInvoiceDate(new Timestamp(System.currentTimeMillis()));
			transactionHistory.setPayerId(null);
			transactionHistory.setPaymentMethod("PayPal");
			transactionHistory.setToken(null);
			transactionHistory.setUserId(user.getUserId());
			
			try {
				transactionHistoryDelegate.insertTransactionHistory(transactionHistory);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		else{
			
			try {
				platform = platformDelegate.getPlatformListByUser(user);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		System.out.println("sendEmail4Complete : " + email);
		try {

			// send email content must be prepare.
			String emailContent = AppConstants.COMPLETE_MAIL_CONTENT.replace(":email", email);

			// send email to usermail
			AppConstants.SendSimpleMessage(email, emailContent, AppConstants.MAIL_SUBJECT);
			
			ObjectMapper objectMapper = new ObjectMapper();
			model = new ModelAndView("data");
			model.addObject("platform", platform != null ? platform : null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	private String PrepareEmailContent(String email, String token, String remoteAddress) {
		String VERIFY_MAIL_CONTENT = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n<meta name=\"viewport\" content=\"width=device-width\" />\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n<title>Verify Livo Account</title>\n\n\n<style type=\"text/css\">\nimg {\nmax-width: 100%;\n}\n@media only screen and (max-width: 640px) {\n h1 {\n font-weight: 600 !important; margin: 20px 0 5px !important;\n }\n h2 {\n font-weight: 600 !important; margin: 20px 0 5px !important;\n }\n h3 {\n font-weight: 600 !important; margin: 20px 0 5px !important;\n }\n h4 {\n font-weight: 600 !important; margin: 20px 0 5px !important;\n }\n h1 {\n font-size: 22px !important;\n }\n h2 {\n font-size: 18px !important;\n }\n h3 {\n font-size: 16px !important;\n }\n .container {\n width: 100% !important;\n }\n .content {\n padding: 10px !important;\n }\n .content-wrapper {\n padding: 10px !important;\n }\n .invoice {\n width: 100% !important;\n }\n}\n</style>\n</head>\n\n<body style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none; width: 100% !important; height: 100%; line-height: 1.6; background: #fafafa; margin: 0; padding: 0;\">\n\n<table class=\"body-wrap\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; width: 100%; background: #fafafa; margin: 0; padding: 0;\">\n\t<tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\n\t\t<td style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\n\t\t<td class=\"container\" width=\"600\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; display: block !important; max-width: 520px !important; clear: both !important; margin: 0 auto; padding: 0;\" valign=\"top\">\n\t\t\t<div class=\"content\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; max-width: 540px; display: block; margin: 0 auto; padding: 10px;\">\n <table class=\"main\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; border-radius: 3px; background: #fff; margin: 0; padding: 0; border: 1px solid #e8e8e8; border-radius: 2px; box-shadow: 0 2px 4px #e5e5e5;\">\n <tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\n <td class=\"aligncenter\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; vertical-align: top; text-align: center; margin: 0; \" align=\"center\" valign=\"top\">\n <table style=\"width: 100%; text-align: center; margin: 0;\">\n <tr>\n <td><img style=\"padding-top: 0px;\" src=\"https://codenvy.com/site/images/mail/header.png\" alt=\"Codenvy\" /></td>\n </tr>\n </table>\n <table class=\"content-wrap\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; padding: 15px 10px 10px 10px; margin: 0;\">\n <tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\n <td class=\"content-block\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\n <h1 style=\"font-family: \'Helvetica Neue\', Helvetica, Arial, \'Lucida Grande\', sans-serif; box-sizing: border-box; font-size: 14px; color: #0d4269; line-height: 1.2; font-weight: 300; margin: 0 0 10px 0; padding: 0;\">Welcome to Codenvy!</h1>\n </td>\n </tr>\n <tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\n <td class=\"content-block\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\n <table style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; text-align: left; margin:0; padding: 0; width:100%;\">\n <tbody style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\n <tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\n <td style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\n <p style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 20px 0; color: #595959;\">\n Thank you for registering with Codenvy.\n </p>\n <p style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 20px 0; color: #595959;\">\n Please verify your email address to develop your mobile app:\n </p>\n </td>\n </tr>\n <tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; text-align: center;\">\n <td style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; text-align:center; padding-bottom: 30px;\">\n "
				+ "<a href=\":verifyUrl\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; text-decoration: none; text-transform: uppercase; line-height:1.5; font-weight: bold; display: inline-block; cursor: pointer; color: #fff; border-radius: 6px; background-color: #399076; border: 1px solid #646567; padding: 8px 20px;\">\n Let\'s Go!\n </a>\n </td>\n </tr>\n <tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\n <td style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\n <p style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 20px 0; color: #595959;\">\n We\'re glad to have you with us.\n <strong style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; font-weight: bold; color:#595959; margin: 0; display:block;\">The Codenvy Team</strong>\n </p>\n </td>\n </tr>\n </tbody>\n </table>\n </td>\n </tr>\n </table>\n </td>\n </tr>\n </table>\n\t\t\t\t<div class=\"footer\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; width: 100%; clear: both; color: #999; margin: 0; padding: 20px 0;\">\n\t\t\t\t\t<table width=\"100%\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\n\t\t\t\t\t\t<tr style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\n\t\t\t\t\t\t\t<td class=\"content-block\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; vertical-align: top; margin: 0; width: 50%; font-size: 10px; color: #8e8d8d; text-align: left;\" valign=\"top\">2015 - Codenvy SA</td>\n\t\t\t\t\t\t\t<td class=\"content-block\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; vertical-align: top; margin: 0; width: 50%; font-size: 10px; color: #8e8d8d; text-align: right;\" valign=\"top\">\n Follow\n <a href=\"#\" style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; width: 50%; font-size: 10px; color: #52ade1; text-align: right;\">@CodenvyHQ</a>\n on Twitter\n </td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t</table>\n\t\t\t\t</div></div>\n\t\t</td>\n\t\t<td style=\"font-family: \'Helvetica Neue\', \'Helvetica\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\n\t</tr>\n</table>\n\n</body>\n</html>";

		String verifyUrl = "http://" + remoteAddress + "/verifyEmail?email=" + email + "&token=" + token; // + ":8080/LivoCloud"

		String mailContent = AppConstants.VERIFY_MAIL_CONTENT.replace(":verifyUrl", verifyUrl);

		System.out.println("PrepareEmailContent is completed. verifyUrl : " + verifyUrl);
		return mailContent;
	}

}
