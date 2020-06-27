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
					<h1></h1>
				</div>
				<!-- END PAGE TITLE -->
			</div>
			<!-- END PAGE HEAD -->
			<c:choose>



				<c:when test="${payPaypal != null}">

					<!-- BEGIN PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT-->
					<div class="row">
						<div class="col-md-12">

							<c:if test="${payPaypal== true}">

								<div class="alert alert-success">
									<h3>
										<strong>Payment complete.</strong>
									</h3>
									<p>
										<strong>Thank you for your order. </strong>
									</p>
									<p>You will see the invoice in Billing menu. Your payment
										reference is ${payerID}</p>
									<p>It is now processing. We will shortly initialize your
										Livo platform and send you an email which includes details to
										access the platform.</p>
									<%-- 									Payment Completed for Payer ID : <strong>${payerID} </strong> --%>
									<!-- 									<p> -->
									<!-- 										Payment Status: <strong>Approved.</strong> -->
									<!-- 									<p> -->
									<!-- 									<h3> -->
									<!-- 										Platform Plan :<strong> Enterprise</strong> -->
									<!-- 									</h3> -->
									<!-- 									<p> -->
									<!-- 										Application Limit :<strong> 10</strong> -->
									<!-- 									<p> -->
									<!-- 										Developer Limit : <strong>10</strong> -->
									<!-- 									<p> -->
									<!-- 										Client Limit: <strong>10</strong> -->
									<!-- 									<p> -->
								</div>


								<!--<div class="alert alert-success">

									<%-- 									<form:form method="post" action="createInstance"> --%>

									 										<div class="form-actions">
									<a href="/LivoCloud/createInstance"><button type="button"
											id="createInstanceButton" name="createInstanceButton"
											class="btn blue pull-right">
											Create Instance Now <i class="m-icon-swapright m-icon-white"></i>
										</button></a>
																			</div> 
									 										<input type="button" name="createInstanceButton" 
									 											alt="Create Instance" value="Create Instance Now" />
									<%-- 									</form:form> --%>
									<h3>
										Instance Id :<strong> <span id="instanceIp">Creating
												instance..</span></strong>
									</h3> -->
									<p>
									<h3>
										<!-- 										<strong>We always are dreaming high for you. Lets -->
										<!-- 											create amazing apps together.</strong> -->
									</h3>
									<p>
								</div>



							</c:if>

							<c:if test="${payPaypal== false}">
								<div class="alert alert-danger">

									<strong>The payment failure.</strong>

								</div>
							</c:if>
						</div>

					</div>
					<!-- END PAGE CONTENT-->

					<!-- END PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT INNER -->
					<!-- END PAGE CONTENT INNER -->




				</c:when>
				<c:otherwise>
				
				<div id="starter-confirm" title="Starter Plan Activation?" style="display:none;">
				  <p><span class="ui-icon ui-icon-alert" style="float:left; margin:4px 12px 10px 0;"></span>You are activating the 'Starter Plan'. Are you sure?</p>
				</div>
				<div id="starter-message" title="Starter Plan" style="display:none;">
				  <p>
				    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
				    Congratulation! Your starter plan is started. Please login with the following username/password to 'developer.livomobile.com':
				  </p>
				  <p>
				    Your username: <b>${loggedInUser.email}</b><br>
				    Your password: <b>The entered pasword</b><br>
				  </p>
				</div>
				
					<!-- BEGIN PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT-->
					<div class="row">
						<div class="col-md-12">
							<!-- BEGIN INLINE NOTIFICATIONS PORTLET-->
							<div class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<i class="fa fa-cogs"></i>PRICING
									</div>
									<!-- 								<div class="tools"> -->
									<!-- 									<a href="javascript:;" class="collapse"> </a> <a -->
									<!-- 										href="#portlet-config" data-toggle="modal" class="config"> -->
									<!-- 									</a> <a href="javascript:;" class="reload"> </a> <a -->
									<!-- 										href="javascript:;" class="remove"> </a> -->
									<!-- 								</div> -->
								</div>
								<div class="portlet-body">
									<div class="row margin-bottom-40">
										<!-- Pricing -->
										<div class="col-md-4">
											<div class="pricing hover-effect">
												<div class="pricing-head pricing-head-active">
													<h3>Starter Plan</h3>
													<h4>
														<i>Free</i> <span> A Month </span>
													</h4>
												</div>
												<ul class="pricing-content list-unstyled">
													<li><i class="fa fa-tags"></i> IDE</li>
													<li><i class="fa fa-asterisk"></i>Mobile Container</li>
													<li><i class="fa fa-heart"></i> Up To 1 App</li>
													<li><i class="fa fa-star"></i> 1 Developer</li>

												</ul>
												<div class="pricing-footer">
													<p></p>

													<input type="button" class="btn btn-primary" id="devPlanButton" name="devPlanButton"
														alt="Developer Plan" value="Start Now">
												</div>
											</div>
										</div>
										<div class="col-md-4">
											<div class="pricing hover-effect">
												<div class="pricing-head">
													<h3>Monthly Plan</h3>
													<h4>
														<i>$</i>90<span> Only one Month </span>
													</h4>
												</div>
												<ul class="pricing-content list-unstyled">
													<li><i class="fa fa-tags"></i> Basic Auth</li>
													<li><i class="fa fa-asterisk"></i> Up To 3 App</li>
													<li><i class="fa fa-asterisk"></i> Free Generic Mobile Container</li>
													<li><i class="fa fa-heart"></i> 1000 MB Space</li>
													<li><i class="fa fa-star"></i> 1 Developer</li>
													<li><i class="fa fa-shopping-cart"></i> Standart Support</li>
													<li><i class="fa fa-shopping-cart"></i> Monthly Backup</li>
													<li><i class="fa fa-heart"></i> 50K API Call per Month</li>
												</ul>
												<div class="pricing-footer">
													<p></p>
													<!-- 													<form -->
													<!-- 														action="https://www.sandbox.paypal.com/cgi-bin/webscr" -->
													<!-- 														method="post" target="_blank"> -->
													<!-- 														<input type="hidden" name="cmd" value="_s-xclick"> -->
													<!-- 														<input type="hidden" name="encrypted" -->
													<!-- 															value="-----BEGIN PKCS7-----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-----END PKCS7-----"> -->
													<!-- 														<input type="image" -->
													<!-- 															src="https://www.sandbox.paypal.com/en_US/i/btn/btn_buynowCC_LG.gif" -->
													<!-- 															border="0" name="submit" -->
													<!-- 															alt="PayPal - The safer, easier way to pay online!"> -->
													<!-- 														<img alt="" border="0" -->
													<!-- 															src="https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif" -->
													<!-- 															width="1" height="1"> -->
													<!-- 													</form> -->
													<a href='${redirectPaypalUrlforMonthly}'> <input
														type="button" class="btn btn-primary" name="proPlanButton"
														alt="Monthly Plan" value="Buy Now">
													</a>


												</div>
											</div>
										</div>
										<div class="col-md-4">
											<div class="pricing hover-effect">
												<div class="pricing-head">
													<h3>Annual Plan</h3>
													<h4>
														<i>$</i>60<span> Per Month </span>
													</h4>
												</div>
												<ul class="pricing-content list-unstyled">
													<li><i class="fa fa-tags"></i> Basic Auth</li>
													<li><i class="fa fa-asterisk"></i> Ip to 10 App</li>
													<li><i class="fa fa-asterisk"></i> Free Generic Mobile Container</li>
													<li><i class="fa fa-heart"></i> 10.000 MB Space</li>
													<li><i class="fa fa-star"></i> Up to 10 Developer</li>
													<li><i class="fa fa-shopping-cart"></i> Standart Support</li>
													<li><i class="fa fa-heart"></i> Monthly Backup</li>
													<li><i class="fa fa-heart"></i> 50K API Call per Month</li>
												</ul>
												<div class="pricing-footer">
													<p></p>

													<a href='${redirectPaypalUrlforAnnual}'> <input
														type="button" class="btn btn-primary"
														name="enterprisePlanButton" alt="Annual Plan"
														value="Buy Now">

													</a>
												</div>
											</div>
										</div>
										<ul class="pricing-content list-unstyled">
											<li><i class="fa fa-asterisk"></i> Additional App after 3 Apps:		$5</li>
											<li><i class="fa fa-asterisk"></i> Additional Developer Monthly:	$40</li>
											<li><i class="fa fa-asterisk"></i> Add. Developer Yearly:	$360</li>
											<li><i class="fa fa-asterisk"></i> Add. 10K API Calls:	$1 </li>
										</ul>
										<a href="/dashboard" class="pull-right"
											style="margin-right: 25px;"> <i class="icon-home "></i> <span
											class="title">Go to Welcome</span>
										</a>
										<!--//End Pricing -->
									</div>
								</div>
							</div>
							<!-- END INLINE NOTIFICATIONS PORTLET-->
						</div>


					</div>
					<!-- END PAGE CONTENT-->

					<!-- END PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT INNER -->
					<!-- END PAGE CONTENT INNER -->

				</c:otherwise>

			</c:choose>

		</div>
		<!-- END CONTENT -->
		</div>
	</div>
	<!-- END CONTAINER -->


	<!-- BEGIN FOOTER -->
	<!-- 	<div class="page-footer-fixed"> -->
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
	<script
		src="<c:url value="/resources/adminpanel/admin/layout4/scripts/custom.js"/>"
		type="text/javascript"></script>

	<!-- END PAGE LEVEL SCRIPTS -->
	<script>
	
		var currCompany = '${currCompany}';
		var wUser = '${wUser}';
	
		jQuery(document).ready(function() {
			Metronic.init(); // init metronic core componets
			Layout.init(); // init layout
			Demo.init(); // init demo features 
			Index.init(); // init index page
			Tasks.initDashboardWidget(); // init tash dashboard widget 
		});
	<c:if test="${payPaypal == true}">
		setupOnComplete();
	</c:if>
		window['_fs_debug'] = false;
		window['_fs_host'] = 'fullstory.com';
		window['_fs_org'] = '7C9WZ';
		window['_fs_namespace'] = 'FS';
		(function(m,n,e,t,l,o,g,y){
		    if (e in m) {if(m.console && m.console.log) { m.console.log('FullStory namespace conflict. Please set window["_fs_namespace"].');} return;}
		    g=m[e]=function(a,b){g.q?g.q.push([a,b]):g._api(a,b);};g.q=[];
		    o=n.createElement(t);o.async=1;o.src='https://'+_fs_host+'/s/fs.js';
		    y=n.getElementsByTagName(t)[0];y.parentNode.insertBefore(o,y);
		    g.identify=function(i,v){g(l,{uid:i});if(v)g(l,v)};g.setUserVars=function(v){g(l,v)};
		    g.identifyAccount=function(i,v){o='account';v=v||{};v.acctId=i;g(o,v)};
		    g.clearUserCookie=function(c,d,i){if(!c || document.cookie.match('fs_uid=[`;`]*`[`;`]*`[`;`]*`')){
		    d=n.domain;while(1){n.cookie='fs_uid=;domain='+d+
		    ';path=/;expires='+new Date(0).toUTCString();i=d.indexOf('.');if(i<0)break;d=d.slice(i+1)}}};
		})(window,document,window['_fs_namespace'],'script','user');
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>


