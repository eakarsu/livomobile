package tr.com.eno.livo.cloud.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

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
import tr.com.eno.livo.cloud.viewBean.SignUpBean;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsItemType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;

@Controller
public class PaymentController {

	@Autowired
	private UserDelegate userDelegate;

	@Autowired
	private PlatformDelegate platformDelegate;
	
	@Autowired
	private TransactionHistoryDelegate transactionHistoryDelegate;
	
	@Autowired
	private PendingRequestDelegate pendingRequestDelegate;

	@RequestMapping(value = "/pricing/paypalPayment", method = RequestMethod.POST)
	public ModelAndView paypalPayment(HttpServletRequest request, HttpServletResponse response) {

		String sandboxUrl = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		try {
			String paypalServiceToken = getPaypalServiceToken();
			sandboxUrl = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=" + paypalServiceToken;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Paypal Payment can not be created.");
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView("pricing");
		model.addObject("redirectPaypal", true);
		model.addObject("redirectPaypalUrl", sandboxUrl);
		return model;
	}

	private String getServerAddress() {
		// TODO Auto-generated method stub
		// Default server Address - localhost
		String serverIpAddress = "localhost";
		try {
			// Setting server address
			serverIpAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.println("Server address unknown host exception :");
			e1.printStackTrace();
		}
		return serverIpAddress;
	}
	
	double paymentAmount = 1;

	@RequestMapping(value = "/pricing/paypalPayment/returnSuccess", method = RequestMethod.GET)
	public ModelAndView payReturnSuccess(@RequestParam("token") String token, @RequestParam("PayerID") String payerID,
			HttpServletRequest request, HttpServletResponse response) {
		// paymentId=PAY-17G80672A5716960NKX4VJQY
		// &token=EC-3CW8637508486474B&PayerID=VK5WR54295SRL
		System.out.println("payReturnSuccess params : " + " token : " + token + " PayerID : " + payerID);
		System.out.println("payReturnSuccess params : " + request.getParameter("handlingTotal") + " -- " + request.getParameter("itemAmount"));

		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponseType = doExpressCheckoutPayment(token, payerID);
		
		String email = (String) request.getSession().getAttribute("userMail");
		User user = (User) request.getSession().getAttribute("loggedInUser");
		
		//  insert into Platforms table
//		User user = userDelegate.getUserByEmail(userMail);
		Platform platform = new Platform();
		platform.setPlatformName(UUID.randomUUID().toString());
		platform.setCloudProvider("Amazon");
		platform.setUserId(user.getUserId());
		platform.setPlatformType(paymentAmount == 90.00 ? AppConstants.PlatformType.MONTHLY.text : AppConstants.PlatformType.ANNUAL.text);
		
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
		transactionHistory.setAmount(String.valueOf(paymentAmount));
		transactionHistory.setInvoiceDate(new Timestamp(System.currentTimeMillis()));
		transactionHistory.setPayerId(payerID);
		transactionHistory.setPaymentMethod("PayPal");
		transactionHistory.setToken(token);
		transactionHistory.setUserId(user.getUserId());
		
		try {
			transactionHistoryDelegate.insertTransactionHistory(transactionHistory);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String homePath = System.getenv("AEON_HOME");

		File pltfmPropertiesFile = new File(homePath + File.separator + "conf", "pltfm.properties");
		if (pltfmPropertiesFile.exists()) {
			OutputStream out = null;
			try {
				Properties prop = new Properties();
				prop.load(new FileInputStream(pltfmPropertiesFile));
				prop.put("applicationLimit", "10");
				prop.put("clientLimit", "10");
				prop.put("developerLimit", "10");
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(pltfmPropertiesFile), "UTF-8");
				prop.store(osw, null);
				osw.close();
				System.out.println("Updated pltfrm.properties file for enterpricePlan.");
			} catch (IOException e) {
				System.out.println("Can not update pltfrm.properties file " + e.getMessage());
			}

		}
		
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
		
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

		ModelAndView model = new ModelAndView("pricing");

		model.addObject("payPaypal", true);
		model.addObject("user", "");
		model.addObject("payerID", payerID);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {

			model.addObject("currCompany", objectMapper.writeValueAsString(currCompany));
			model.addObject("wUser", objectMapper.writeValueAsString(wUser));
			model.addObject("platform", objectMapper.writeValueAsString(platform));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}
	@RequestMapping(value = "/pricing/paypalPayment/returnFail", method = RequestMethod.GET)
	public ModelAndView payReturnFail(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {

		ModelAndView model = new ModelAndView("pricing");

		model.addObject("payPaypal", false);

		return model;
	}

	// Map<String, String> sdkConfig = new HashMap<String, String>();
	// sdkConfig.put("mode", "sandbox");

	// String accessToken = token;
	// APIContext apiContext = new APIContext(accessToken);
	// apiContext.setConfigurationMap(sdkConfig);
	//
	// Payment payment = new Payment( );
	// PaymentExecution paymentExecute = new PaymentExecution();
	// paymentExecute.setPayerId("VK5WR54295SRL");
	// payment.execute(apiContext, paymentExecute);
	// Get the AEON home environment variable

	private DoExpressCheckoutPaymentResponseType doExpressCheckoutPayment(String token, String PayerID) {

		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = null;

		GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(token);
		getExpressCheckoutDetailsRequest.setVersion("104.0");

		GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
		getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "live");
		sdkConfig.put("acct1.UserName", "info_api1.livomobile.com");
		sdkConfig.put("acct1.Password", "HSDCU7JA2RC88NWU");
		sdkConfig.put("acct1.Signature", "AFcWxV21C7fd0v3bYYYRCpSSRl31A9ELejSe-uqHvUxa6nEsbAmPWUoQ");
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
		paymentAmount = 1;
		try {
			GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = service.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
			System.out.println("getExpressCheckoutDetailsRequest: " + getExpressCheckoutDetailsRequest + " -- " + getExpressCheckoutDetailsRequest);

			List<PaymentDetailsType> paymentDetailList = getExpressCheckoutDetailsResponse.getGetExpressCheckoutDetailsResponseDetails().getPaymentDetails();
			if(paymentDetailList.size() > 0){
				PaymentDetailsType paymentDetail = paymentDetailList.get(0);
				PaymentDetailsItemType detailsItem = paymentDetail.getPaymentDetailsItem().size() > 0 ? paymentDetail.getPaymentDetailsItem().get(0) : null;
				paymentAmount = Double.valueOf(paymentDetail.getOrderTotal().getValue());
				System.out.println("paymentDetail2: " + paymentDetail.getItemTotal().getValue() + " -- " + paymentDetail.getOrderTotal().getValue() + " -- " + (detailsItem != null ? detailsItem.getAmount().getValue() : null));
			}

			PaymentDetailsType paymentDetail = new PaymentDetailsType();
			paymentDetail.setNotifyURL("http://replaceIpnUrl.com");
			
			BasicAmountType orderTotal = new BasicAmountType();
			orderTotal.setValue(String.valueOf(paymentAmount));
			orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
			paymentDetail.setOrderTotal(orderTotal);
			paymentDetail.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));

			System.out.println("paymentDetail.getTransactionId()  : " + paymentDetail.getTransactionId());

			List<PaymentDetailsType> paymentDetails = new ArrayList<PaymentDetailsType>();
			paymentDetails.add(paymentDetail);

			DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();
			doExpressCheckoutPaymentRequestDetails.setToken(token);
			doExpressCheckoutPaymentRequestDetails.setPayerID(PayerID);
			doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetails);

			DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(
					doExpressCheckoutPaymentRequestDetails);
			doExpressCheckoutPaymentRequest.setVersion("104.0");

			DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
			doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

			return doExpressCheckoutPaymentResponse = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);

			// System.out.println( "Transaction ID :  "
			// +doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getTransactionID());
			// System.out.println("doExpressCheckoutPaymentResponse getEbayTransactionID  : "
			// +doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getEbayTransactionID());
			// System.out.println("doExpressCheckoutPaymentResponse.toString()  :  "+doExpressCheckoutPaymentResponse.toString());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return doExpressCheckoutPaymentResponse;

	}

	private String getPaypalServiceToken() throws SSLConfigurationException, InvalidCredentialException, HttpErrorException,
			InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, OAuthException, IOException,
			InterruptedException, ParserConfigurationException, SAXException {

		String serverIpAddress = getServerAddress();

		PaymentDetailsType paymentDetails = new PaymentDetailsType();
		paymentDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
		PaymentDetailsItemType item = new PaymentDetailsItemType();
		
		BasicAmountType amt = new BasicAmountType();
		amt.setCurrencyID(CurrencyCodeType.fromValue("USD"));
		amt.setValue(String.valueOf(90.00));
		item.setQuantity(1);
		item.setName("item");
		item.setAmount(amt);

		List<PaymentDetailsItemType> lineItems = new ArrayList<PaymentDetailsItemType>();
		lineItems.add(item);
		paymentDetails.setPaymentDetailsItem(lineItems);
		BasicAmountType orderTotal = new BasicAmountType();
		orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
		orderTotal.setValue(String.valueOf(90.00));
		paymentDetails.setOrderTotal(orderTotal);
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
		paymentDetailsList.add(paymentDetails);

		SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
		setExpressCheckoutRequestDetails.setReturnURL(AppConstants.CLOUD_SERVER_ADDRESS.replace("localhost", serverIpAddress)
				+ AppConstants.PAYPAL_PAYMENT_SUCCESS_METHOD);
		setExpressCheckoutRequestDetails.setCancelURL(AppConstants.CLOUD_SERVER_ADDRESS.replace("localhost", serverIpAddress)
				+ AppConstants.PAYPAL_PAYMENT_CANCEL_METHOD);

		setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);

		SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
		setExpressCheckoutRequest.setVersion("104.0");

		SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
		setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "live");
		sdkConfig.put("acct1.UserName", "info_api1.livomobile.com");
		sdkConfig.put("acct1.Password", "HSDCU7JA2RC88NWU");
		sdkConfig.put("acct1.Signature", "AFcWxV21C7fd0v3bYYYRCpSSRl31A9ELejSe-uqHvUxa6nEsbAmPWUoQ");
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);

		SetExpressCheckoutResponseType setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);
		return setExpressCheckoutResponse.getToken();
	}

}
