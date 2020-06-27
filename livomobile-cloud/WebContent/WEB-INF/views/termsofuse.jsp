<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@include file="header.jsp"%>

	<div class="clearfix"></div>
	<!-- BEGIN CONTAINER -->
	<div class="page-container">
		 <%@include file="sidebar.jsp"%>
		<!-- BEGIN CONTENT -->
		<div class="page-content-wrapper">
			<div class="page-content">
				<!-- BEGIN PAGE HEAD -->
				<div class="page-head">
					<!-- BEGIN PAGE TITLE -->
					<div class="page-title">
						<h1>
						</h1>
					</div>
					<!-- END PAGE TITLE -->
				</div>
				<!-- END PAGE HEAD -->
							<!-- BEGIN PAGE BREADCRUMB -->
				<div class="row">
					<div class="col-md-12">

						<div class="alert alert-success">

							<strong>Welcome to Livo Mobile!</strong> <br>Please accept
							the following "Terms of Use" to activate your account.

						</div>
					</div>

				</div>
				<div class="row">
					<div class="col-md-12">
						<!-- BEGIN PORTLET-->
						<div class="portlet light form-fit">
							<div class="portlet-title">
								<div class="caption font-blue">
									<i class="icon-speech font-blue"></i> <span
										class="caption-subject bold uppercase"> TERMS OF USE</span> <span class="caption-helper"></span>
								</div>
<!-- 								<div class="actions"> -->
<!-- 									<a href="javascript:;" -->
<!-- 										class="btn btn-circle btn-default btn-sm"> <i -->
<!-- 										class="fa fa-pencil"></i> Edit -->
<!-- 									</a> <a href="javascript:;" -->
<!-- 										class="btn btn-circle btn-default btn-sm"> <i -->
<!-- 										class="fa fa-plus"></i> Add -->
<!-- 									</a> <a class="btn btn-circle btn-icon-only btn-default" -->
<!-- 										href="javascript:;"> <i class="icon-wrench"></i> -->
<!-- 									</a> -->
<!-- 								</div> -->
							</div>
							<div class="portlet-body form">
								<form:form  action="termsVerified" method="post" id="form-termsOfUse"
									class="form-horizontal form-bordered"  modelAttribute="signUpBean">
									 	<div class="form-wrap-group">
										<form:input type="hidden" class="form-control" id="email"
											name="email" placeholder="Your Email"  value="${email}" path="" />
									</div>
									<div class="form-group last">
										<div class="col-md-12">
											<textarea class="form-control" rows="10"
												placeholder="Autosizeme..." disabled>
SUBSCRIPTION LICENSE AGREEMENT - LIVO CORPORATION
TERMS OF SERVICE
BY COMPLETING ONLINE REGISTRATION FOR THE LIVO CORPORATION SERVICE ("SERVICE"), PAYING ANY SUBSCRIPTION FEE, USING THE SERVICE, AND/OR INSTALLING ANY RELATED SOFTWARE, YOU IRREVOCABLY AGREE TO ALL OF THE TERMS AND CONDITIONS SET FORTH IN THIS SUBSCRIPTION LICENSE AGREEMENT, WHICH CONSTITUTES THE TERMS OF SERVICE ("TERMS OF SERVICE") BETWEEN YOU AND LIVO CORPORATION, LLC., A CALIFORNIA CORPORATION ("LIVO CORPORATION") AND GOVERNS YOUR USE OF THE SERVICE, AS SUCH SERVICE MAY BE MODIFIED BY LIVO CORPORATION FROM TIME TO TIME.
IF YOU DO NOT AGREE TO ANY OF THESE TERMS OF SERVICE, DO NOT REGISTER FOR OR USE THE SERVICE. IN PROVIDING THE SERVICE, LIVO CORPORATION IS RELYING ON YOUR AGREEMENT TO THESE TERMS OF SERVICE.
Terms of Service
Livo Corporation reserves the right to update and change the Terms of Service from time to time without notice. Any new features that augment or enhance the current Service, including the release of new tools and resources, shall be subject to the Terms of Service.
Continued use of the Service after any such changes shall constitute your consent to such changes. You can review the most current version of the Terms of Service at any time at:
https://livemobile.com/terms-of-use
Violation of any these Terms of Service is grounds for immediate termination of your Account. You understand and agree that Livo Corporation is not responsible for any content posted on the Service ("Content") and that in using the Service you may be exposed to materials that are prohibited by these Terms of Service. You agree that any and all use of the Service is at your own risk.
Account Terms
You must be 13 years or older to use this Service.
You must be a human. Accounts registered by bots or other automated methods are not permitted.
You must provide your legal full name, a valid email address, and any other information requested in order to complete the signup process. You represent and warrant that any and all information you provide to Livo Corporation is accurate and complete in all respects.
You are responsible for maintaining the security of your account and password. Livo Corporation cannot and will not be liable for any loss or damage from your failure to comply with this security obligation.
You are responsible for all Content posted and activity that occurs under your account (even when Content is posted by others, including without limitation those who have accounts under your account).
You may not use the Service for any illegal or unauthorized purpose. You must not, in the use of the Service, violate any applicable laws (including but not limited to copyright laws). You may not sublicense or otherwise transfer your Account or use of the Service. You are solely responsible for all use of the Service through your Account.
Payment, Refunds, Upgrading and Downgrading Terms
Unless otherwise agreed by Livo Corporation, a valid credit card is required for paid subscription plans.
The Service is billed in advance on a monthly, annually, or other periodic basis as may be agreed by Livo Corporation and in any case is non-refundable. There will be no refunds or credits for partial periods of service, upgrade/downgrade refunds, or refunds for periods unused with an open account. In order to treat everyone equally, no exceptions will be made.
All fees are exclusive of all taxes, levies, or duties imposed by taxing authorities, and you shall be responsible for payment of all such taxes, levies, or duties, excluding only Livo Corporation's income taxes.
For any upgrade or downgrade in plan level, you will automatically be charged the new rate on your next billing cycle.
Downgrading your Service may cause the loss of Content, features, or capacity of your Account. Livo Corporation is not and shall not be liable for any such loss.
Cancellation and Termination
You are solely responsible for properly canceling your account. An email or phone request to cancel your account is not considered as cancellation. You can cancel your account at any time by clicking on the Account link in the global navigation bar at the top of the screen and following the simple cancellation link.
All of your Content will be immediately deleted from the Service upon cancellation. This information cannot be recovered once your account is cancelled. Livo Corporation is not and shall not be liable for the loss of any Content.
If you cancel the Service before the end of your current paid up period, your cancellation will take effect immediately and you will not be charged again. There will be no proration of unused subscription or refund of unused fees.
Livo Corporation, in its sole discretion, has the right to suspend or terminate your account and refuse any and all current or future use of the Service, or any other Livo Corporation service, for any reason at any time. Such termination of the Service will result in the deactivation or deletion of your Account or your access to your Account, and the forfeiture and relinquishment of all Content in your Account. Livo Corporation reserves the right to refuse service to anyone for any reason at any time. Livo Corporation is not and shall not be liable for any related losses. Without limiting the foregoing, Livo Corporation may, at any time and for any reason or no reason, suspend, terminate, or delete any free Accounts (and all related Content) that are inactive for a period of two months or more.
Modifications to the Service and Prices
Livo Corporation reserves the right at any time and from time to time to modify, supplement, expand, or discontinue, temporarily or permanently, the Service (or any part thereof) with or without notice.
Prices of all Services, including but not limited to periodic subscription plan fees to the Service, are subject to change upon 30 days notice from Livo Corporation.
Livo Corporation shall not be liable to you or to any third party for any modification, price change, suspension or discontinuance of the Service.
Copyright, Trademark, and Content Ownership
All content posted on the Service must comply with U.S. copyright law.
Livo Corporation claims no intellectual property rights over the material you provide to the Service. As between you and Livo Corporation, your profile and materials uploaded remain yours. However, by sharing your pages, apps, databases, scripts or other resources you agree to allow others to view and share your Content. Livo Corporation is not responsible for the use of your Content by others.
Livo Corporation does not pre-screen Content and is not responsible for Content, whether provided by you or any other user of the Service. However, Livo Corporation and its designees have the right (but not the obligation) in their sole discretion to refuse or remove any Content that is available via the Service.
The look and feel of the Service is copyright©2017 Livo Corporation. All rights reserved. You may not duplicate, copy, or reuse any portion of the HTML/CSS or visual design elements without express written permission from Livo Corporation, which permission may be withheld, conditioned, or delayed in Livo Corporation's sole discretion.
As between you and Livo Corporation, Livo Corporation is the sole and exclusive owner of the LIVO CORPORATION name and logo. Your use of the Service confers no ownership or other rights to the LIVO CORPORATION name or logo, or any other name or logo used by Livo Corporation, or the goodwill associated therewith, all of which shall inure to the benefit of Livo Corporation. Livo Corporation makes no representation or warranty regarding non-infringement.
General Conditions
Your use of the Service is at your sole risk. The service is provided on an "as is" and "as available" basis. Without limiting the foregoing, Livo Corporation is not responsible for any losses or corruption of Content.
Technical support is only provided to paying account holders and is available via email.
You understand that Livo Corporation uses third party vendors and hosting partners to provide the necessary hardware, software networking, storage, and related technology required to run the Service.
You agree that you will not modify, adapt or hack the Service or modify another website so as to falsely imply that it is associated with the Service, Livo Corporation, or any other Livo Corporation service.
You agree not to reproduce, duplicate, copy, sell, reverse engineer, decompile, resell or exploit the name of the Service, any portion of the Service, use of the Service, or access to the Service without the express written permission by Livo Corporation, which permission may be withheld, conditioned, or delayed in Livo Corporation's sole discretion.
We may, but have no obligation to, remove Content and Accounts containing Content that we determine in our sole discretion are unlawful, offensive, threatening, libelous, defamatory, pornographic, obscene or otherwise objectionable or violates any party's intellectual property or these Terms of Service.
Verbal, physical, written or other abuse (including threats of abuse or retribution) of any Livo Corporation customer, employee, member, or officer will result in immediate account termination.
You understand that the technical processing and transmission of the Service, including your Content, may be transferred unencrypted and involve (a) transmissions over various networks; and (b) changes to conform and adapt to technical requirements of connecting networks or devices.
Livo Corporation has established relationships with certain third parties who provide services or products (such as web APIs and services, developer tools and operating systems) you may find useful in using the Service, and Livo Corporation may establish additional, similar relationships in the future. Such third party products and services may be made available through Livo Corporation's website, through links provided by Livo Corporation, or directly from the third party provider. In order to use such third party products or services or increase the functionality of the Service, you may be asked to provide certain personal information, including without limitation passwords, certificates, authorizations, and other information. You acknowledge and agree that any and all personal information provided to Livo Corporation may be: (i) used by Livo Corporation in connection with the Service and such third party relationships; (ii) shared with such third parties in connection with the Service and the products and services provided by such third parties; (iii) transferred across national boundaries and stored and processed in any of the countries in which Livo Corporation maintains an office; and (iv) supplemented by Livo Corporation with additional information you provide to Livo Corporation in connection with the Service. Livo Corporation shall not be responsible for any decreased functionality of the Service or products provided by Livo Corporation resulting from your failure to provide requested information, or from the inaccuracy or incompleteness of any such information. Livo Corporation shall not be responsible for any information provided directly by you to any third party, whether in connection with the Service or otherwise.
You must not upload, post, host, or transmit unsolicited email, SMSs, or spam messages.
You must not transmit any worms or viruses or any code of a destructive nature.
BY USING THE SERVICE, YOU VOLUNTARILY AND IRREVOCABLY ASSUME ALL RELATED RISKS. LIVO CORPORATION MAKES NO REPRESENTATION, WARRANTY, OR COVENANT REGARDING THE SERVICE OR ANY CONTENT, INCLUDING WITHOUT LIMITATION ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, OR NON-INFRINGEMENT, AND ALL SUCH REPRESENTATIONS, WARRANTIES, AND COVENANTS ARE HEREBY FULLY DISCLAIMED. Without limiting the foregoing, Livo Corporation does not warrant that (i) the service will meet your specific requirements, (ii) the service will be uninterrupted, timely, secure, or error-free, (iii) the results that may be obtained from the use of the service will be accurate or reliable, (iv) the quality of any products, services, information, or other material purchased or obtained by you through the service will meet your expectations, (v) any errors in the Service will be corrected; (vi) Content will be free from defects; (vii) Content will be free from claims as to infringement of third party rights; (viii) Content will be secure or not subject to partial or total loss.
LIVO CORPORATION SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL OR EXEMPLARY DAMAGES, INCLUDING BUT NOT LIMITED TO DAMAGES FOR LOSS OF PROFITS, GOODWILL, USE, DATA OR OTHER INTANGIBLE LOSSES (EVEN IF LIVO CORPORATION HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES), RESULTING FROM: (i) THE USE OR THE INABILITY TO USE THE SERVICE; (ii) THE COST OF PROCUREMENT OF SUBSTITUTE GOODS AND SERVICES RESULTING FROM ANY GOODS, DATA, INFORMATION OR SERVICES PURCHASED OR OBTAINED OR MESSAGES RECEIVED OR TRANSACTIONS ENTERED INTO THROUGH OR FROM THE SERVICE; (iii) UNAUTHORIZED ACCESS TO OR ALTERATION OF YOUR TRANSMISSIONS OR DATA; (iv) CONTENT ON THE SERVICE; (v) STATEMENTS OR CONDUCT OF ANY THIRD PARTY ON THE SERVICE; (vi) OR ANY OTHER MATTER RELATING TO THE SERVICE.
IN NO EVENT WILL LIVO CORPORATION'S LIABILITY TO YOU OR ANYONE CLAIMING THROUGH YOU FOR ACTUAL DAMAGES EXCEED THE AGGREGATE AMOUNT OF SUBSCRIPTION FEES PAID BY YOU IN THE SIX (6) MONTHS PRIOR TO THE EVENT OR OCCURRENCE GIVING RISE TO THE CLAIM.
YOU AGREE TO INDEMNIFY, DEFEND, AND HOLD LIVO CORPORATION AND ITS SHAREHOLDERS, OFFICERS, DIRECTORS, AGENTS AND EMPLOYEES HARMLESS FROM AND AGAINST ANY AND ALL CLAIMS, COSTS, LOSSES, AND CHARGES (INCLUDING WITHOUT LIMITATION ATTORNEYS' FEES AND COSTS) ARISING FROM YOUR REGISTRATION TO USE THE SERVICE, ACTUAL USE OF THE SERVICE, OR BREACH OF THESE TERMS OF SERVICE.
The failure of Livo Corporation to exercise or enforce any right or provision of the Terms of Service shall not constitute a waiver of such right or provision. These Terms of Service constitutes the entire agreement between you and Livo Corporation and govern your use of the Service, superseding any prior agreements between you and Livo Corporation (including, but not limited to, any prior versions of the Terms of Service). No additional documents provided by you will alter these Terms of Service or otherwise be binding on Livo Corporation unless such alteration or binding effect is specifically acknowledged by Livo Corporation in writing.
These Terms of Service and your use of the Service shall be governed by and interpreted in accordance with the internal laws of the State of California as if you were a resident of California and without application of any rules favoring the non-drafting party. Jurisdiction and venue for any and all actions arising from these Terms of Service or the Service itself shall be in the California Superior Court sitting in Contra Costa County, California. If any such action is removed to federal court for any reason, jurisdiction and venue shall be in the United States District Court for the Ninth Circuit (Northern District of California). You hereby submit to the jurisdiction of the foregoing courts over your person and property, waive all objections to venue therein, and agree that service of process by mail will be effective when mailed to an address provided by you in your registration/subscription material.
Livo Corporation may assign or otherwise transfer without further liability these Terms of Service and/or the Service in whole or in part at any time without notice. You may not assign or otherwise transfer your Account or any rights related to the Service without Livo Corporation's prior written consent. You retain all liability hereunder regardless of any assignment or other transfer, and regardless of Livo Corporation's consent. A sale, merger, or other reorganization of you which results in any change of control shall constitute an assignment requiring Livo Corporation's consent hereunder.
Weather API, and such other plug-ins as may be made available by Livo Corporation in connection with the Service from time-to-time (each a "Plug-In" and together, the "Plug-Ins")
PLUG-INS ARE PROVIDED "AS-IS" WITH ANY AND ALL FAULTS AND LIVO CORPORATION MAKES NO REPRESENTATION, WARRANTY OR COVENANT WITH RESPECT TO THE PLUG-INS OF ANY KIND WHATSOEVER. TO THE MAXIMUM EXTENT OF APPLICABLE LAW LIVO CORPORATION HEREBY DISCLAIMS ANY AND ALL REPRESENTATIONS, WARRANTIES AND COVENANTS (EXPRESS OR IMPLIED, ORAL OR WRITTEN), WITH RESPECT TO THE PLUG-INS (AND ALL PARTS THEREOF), INCLUDING WITHOUT LIMITATION ANY AND ALL IMPLIED WARRANTIES AND CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY, AND FITNESS OR SUITABILITY FOR ANY PURPOSE (WHETHER LIVO CORPORATION KNOWS, HAS REASON TO KNOW, HAS BEEN ADVISED, OR IS OTHERWISE IN FACT AWARE OF ANY SUCH PURPOSE), WHETHER ALLEGED TO ARISE BY LAW, BY REASON OR CUSTOM OR USAGE IN THE TRADE, OR BY COURSE OF DEALING. WITHOUT LIMITING THE FOREGOING, LIVO CORPORATION MAKES NO REPRESENTATION OR WARRANTY THAT THE PLUG-INS OR THE OPERATION THEREOF WILL BE UNINTERRUPTED, ERROR-FREE, "BUG FREE", OR FREE FROM CLAIMS OF INFRINGEMENT.
YOU ASSUME ALL RISKS ARISING FROM THE RECEIPT, COPYING, USE, MODIFICATION, OR DISSEMINATION OF THE PLUG-INS. UNDER NO CIRCUMSTANCES WILL LIVO CORPORATION BE LIABLE OR OBLIGATED TO YOU OR ANY OTHER PARTY FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES ARISING DIRECTLY OR INDIRECTLY FROM THE PLUG-INS, REGARDLESS OF THEORY OF LIABILITY, WHETHER IN TORT, CONTRACT, NEGLIGENCE, STRICT LIABILITY OR OTHERWISE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. Livo Corporation shall have no obligation to correct, update or modify any Plug-Ins (even if Livo Corporation is advised of bugs or errors).
You acknowledge that the Plug-Ins are being made available gratuitously in reliance on the foregoing disclaimer of warranties, assumption of risk, and limitation of liability, and that such disclaimers, assumption of risk, and limitation of liability are each a strict condition to Livo Corporation's willingness to provide the Plug-Ins. You hereby waive any and all claims against Livo Corporation with respect to the Plug-Ins, and you shall defend, indemnify, and hold Livo Corporation harmless, from and against any and all claims, damages, costs, and losses arising from the receipt or use of any Plug-Ins by you or any third party which you supply with any Plug-Ins.
Questions about the Terms of Service should be sent to support at support@livemobile.com
© Copyright Livo Corporation 2017
												</textarea>
											 
										</div>
									</div>
									<div class="form-actions">
										<div class="row">
											<div class="col-md-12">
											<button type="button" class="btn default pull-left" style="${loggedInUser.accountState == 3 ? 'display:none' : 'display:inline'}">I Do NOT Accept</button>
												<button id="termsofUseVerifiedButton" type="submit" class="btn red pull-right" style="${loggedInUser.accountState == 3 ? 'display:none' : 'display:inline'}">
													<i class="fa fa-check"></i>I Accept
												</button>
												
											</div>
										</div>
									</div>
								</form:form>
							</div>
						</div>
						<!-- END PORTLET-->
					</div>
				</div>

				<!-- END PAGE BREADCRUMB -->
				<!-- BEGIN PAGE CONTENT INNER -->
				<!-- END PAGE CONTENT INNER -->
			</div>
			<!-- END CONTENT -->
			</div>
	</div>
	<!-- END CONTAINER -->
	<!-- BEGIN FOOTER -->
<!-- 	<div class="page-footer-fixed" > -->
<!-- 		<div class="page-footer-inner"> -->
<!-- 			2014 &copy; Metronic by keenthemes. <a -->
<!-- 				href="http://themeforest.net/item/metronic-responsive-admin-dashboard-template/4021469?ref=keenthemes" -->
<!-- 				title="Purchase Metronic just for 27$ and get lifetime updates for free" -->
<!-- 				target="_blank">Purchase Metronic!</a> -->
<!-- 		</div> -->
<!-- 		<div class="scroll-to-top"> -->
<!-- 			<i class="icon-arrow-up"></i> -->
<!-- 		</div> -->
<!-- 	</div> -->
	<!-- END FOOTER -->
	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<!-- BEGIN CORE PLUGINS -->
	<!--[if lt IE 9]>
<script src="<c:url value="/resources/adminpanel/global/plugins/respond.min.js"/>" ></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/excanvas.min.js"/>" ></script> 
<![endif]-->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-migrate.min.js"/>"
		type="text/javascript"></script>
	<!-- IMPORTANT! Load jquery-ui.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-ui/jquery-ui.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap/js/bootstrap.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.blockui.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.cokie.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/uniform/jquery.uniform.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js"/>"
		type="text/javascript"></script>
	<!-- END CORE PLUGINS -->
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/jquery.vmap.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.russia.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.world.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.europe.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.germany.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.usa.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/data/jquery.vmap.sampledata.js"/>"
		type="text/javascript"></script>
	<!-- IMPORTANT! fullcalendar depends on jquery-ui.min.js for drag & drop support -->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/morris/morris.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/morris/raphael-min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.sparkline.min.js"/>"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script
		src="<c:url value="/resources/adminpanel/global/scripts/metronic.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/layout4/scripts/layout.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/layout4/scripts/demo.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/pages/scripts/index3.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/pages/scripts/tasks.js"/>"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
	
	<!-- Global site tag (gtag.js) - Google Analytics -->
	<script async src="https://www.googletagmanager.com/gtag/js?id=UA-108371219-1"></script>
	<script>
	  window.dataLayer = window.dataLayer || [];
	  function gtag(){dataLayer.push(arguments);}
	  gtag('js', new Date());
	
	  gtag('config', 'UA-108371219-1');
	</script>
	<!-- Global site tag (gtag.js) - Google AdWords: 965723771 -->
	<script async src="https://www.googletagmanager.com/gtag/js?id=AW-965723771"></script>
	<script> 
		window.dataLayer = window.dataLayer || []; 
		function gtag(){dataLayer.push(arguments);} 
		gtag('js', new Date()); 
		gtag('config', 'AW-965723771');
		<c:if test="${loggedInUser.accountState != 3}">
			gtag('event', 'conversion', {'send_to': 'AW-965723771/9ayNCO_A6ncQ-4y_zAM'});
		</c:if>
	</script>
	
	<script>
		jQuery(document).ready(function() {
			Metronic.init(); // init metronic core componets
			Layout.init(); // init layout
			Demo.init(); // init demo features 
			Index.init(); // init index page
			Tasks.initDashboardWidget(); // init tash dashboard widget  
		});
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>