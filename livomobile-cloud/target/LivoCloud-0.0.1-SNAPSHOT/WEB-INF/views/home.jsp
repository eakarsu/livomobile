<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html lang="en">

<head>

<title>Livo Mobile Cloud</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">

<!-- BEGIN GLOBAL MANDATORY STYLES -->
<!-- <link href='http://fonts.googleapis.com/css?family=Hind:400,500,300,600,700' rel='stylesheet' type='text/css'> -->
<link
	href="<c:url value="/resources/onepage/css/font-awesome.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/onepage/css/simple-line-icons.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/onepage/bootstrap/css/bootstrap.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
<link href="<c:url value="/resources/onepage/css/owl.carousel.css"/>"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/onepage/css/settings.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/onepage/css/cubeportfolio.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE LEVEL PLUGIN STYLES -->
<!-- BEGIN THEME STYLES -->
<link href="<c:url value="/resources/onepage/css/layout.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END THEME STYLES -->
<!-- <link rel="shortcut icon" href="favicon.ico" /> -->
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<!-- DOC: Apply "page-on-scroll" class to the body element to set fixed header layout -->
<body class="page-header-fixed">

	<!-- BEGIN MAIN LAYOUT -->
	<!-- Header BEGIN -->

	<header class="page-header">
		<div id="megamenu">
			<div class="toptopbar">
				<ul>
					<li><a href="/viewSignUp" class="signup">SIGN UP</a></li>
					<li><a href="/login" class="login">Login</a></li>
					<!-- <li><a href="#" class="toplink">Company</a></li>
					<li><a href="#" class="toplink">Dev Center</a></li>
					<li><a href="#" class="toplink">Blog</a></li> -->
				</ul>
			</div>
		</div>
		<nav class="navbar navbar-fixed-top" style="top: 40px;"
			role="navigation">
			<div class="container">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header page-scroll">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target=".navbar-responsive-collapse">
						<span class="sr-only">Toggle navigation</span> <span
							class="toggle-icon"> <span class="icon-bar"></span> <span
							class="icon-bar"></span> <span class="icon-bar"></span>
						</span>
					</button>
					<a class="navbar-brand" href="#intro"> <img
						class="logo-default"
						src="<c:url value="/resources/onepage/img/logo_default_livo.png"/>"
						alt="Logo"> <img class="logo-scroll"
						src="<c:url value="/resources/onepage/img/logo_scroll_livo.png"/>"
						alt="Logo">
					</a>
				</div>

				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse navbar-responsive-collapse">
					<ul class="nav navbar-nav">
						<li class="page-scroll active"><a href="#intro">HOME</a></li>
						<li class="page-scroll"><a href="#about">WHAT IS LIVO</a></li>
						<li class="page-scroll"><a href="#features">FEATURES</a></li>
						<li class="page-scroll"><a href="#pricing">PRICING</a></li>
						<li class="page-scroll"><a href="#clients">PLATFORMS</a></li>
						<li class="page-scroll"><a href="#contact">CONTACT</a></li>
						<!-- <li class="page-scroll"><a href="#team">Team</a></li> -->
						<!-- <li class="page-scroll"><a href="#portfolio">Portfolio</a></li> -->
					</ul>
				</div>
				<!-- End Navbar Collapse -->
			</div>
			<!--/container-->
		</nav>
	</header>
	<!-- Header END -->

	<!-- BEGIN INTRO SECTION -->
	<section id="intro">
		<!-- Slider BEGIN -->
		<div class="page-slider">
			<div class="fullwidthbanner-container revolution-slider">
				<div class="banner">
					<ul id="revolutionul">

						<li data-transition="fade" data-slotamount="8"
							data-masterspeed="700" data-delay="6000" data-thumb="">
							<!-- THE MAIN IMAGE IN THE FIRST SLIDE --> <img
							src="<c:url value="/resources/onepage/img/bg/bg_slider2.jpg"/>"
							alt="">

							<div class="caption lft tp-resizeme" data-x="center"
								data-y="center" data-hoffset="-322" data-voffset="-30"
								data-speed="900" data-start="1000" data-easing="easeOutExpo">
								<h3 class="title-v2">
									MOBILIZE YOUR <br> BUSINESS DATA
								</h3>
							</div>
							<div class="caption lft tp-resizeme" data-x="center"
								data-y="center" data-hoffset="-490" data-voffset="110"
								data-speed="900" data-start="1500" data-easing="easeOutExpo">
								<p class="subtitle-v2">Available in:</p>
							</div> <a href="resources/ccs/livo-android-client-release.apk"
							class="caption lft tp-resizeme slide_thumb_img slide_border"
							data-x="center" data-y="center" data-hoffset="-370"
							data-voffset="102" data-speed="900" data-start="1500"
							data-easing="easeOutExpo"><img
								src="<c:url value="./resources/onepage/img/widgets/android_icon.png"/>"
								alt="Livo Android Container"></a> |
						   <a href="itms-services://?action=download-manifest&url=https://cloud.livomobile.com/resources/ccs/livo.plist" class="caption lft tp-resizeme slide_thumb_img"
							data-x="center" data-y="center" data-hoffset="-248"
							data-voffset="102" data-speed="900" data-start="1500"
							data-easing="easeOutExpo"><img
								src="<c:url value="./resources/onepage/img/widgets/ios_icon.png"/>"
								alt="Livo iOS Container"></a>
								
							<div class="caption lfb tp-resizeme" data-x="right"
								data-y="bottom" data-hoffset="100" data-speed="900"
								data-start="2000" data-easing="easeOutExpo">
								<img
									src="<c:url value="./resources/onepage/img/widgets/device.png"/>"
									alt="Image 3">
							</div>
							<!--<div class="caption lft tp-resizeme" data-x="center"
								data-y="center" data-hoffset="-280" data-voffset="250"
								data-speed="900" data-start="1500" data-easing="easeOutExpo">
								
								<form:form id="signUp-form" class="form-wrap input-field"
									action="signUp" method="post" modelAttribute="signUpBean">
									<div class="form-wrap-group">
										<form:input type="text" class="form-control" id="name" name="name" placeholder="Name Surname" path="" />
									</div>
									<div class="form-wrap-group">
										<form:input type="email" class="form-control" id="email" name="email" placeholder="Your Email" path="" />
									</div>
									 									<div class="form-wrap-group border-left-transparent">
									 										<form:input type="password" class="form-control" id="password"
									 											name="password" placeholder="Password" path="" />
									 									</div>
									<div class="form-wrap-group">
										<button id="signUpButton" type="submit"
											class="btn-danger btn-md btn-block">Signup</button>

									</div>

								</form:form>

								<div class="col-md-12">
									<c:if test="${isRegisteredEmail}">
										<div id="signUpResponse" class="alert alert-warning">${userControlResponse}</div>
									</c:if>
								</div>

							</div>-->


						</li>

					</ul>
				</div>
			</div>
		</div>
		<!-- Slider END -->
	</section>
	<!-- END INTRO SECTION -->

	<!-- BEGIN MAIN LAYOUT -->
	<div class="page-content">
		<!-- 		<!-- SUBSCRIBE BEGIN -->
		<!-- 		<div class="subscribe"> -->
		<!-- 			<div class="container"> -->
		<!-- 				<div class="subscribe-wrap"> -->
		<!-- 					<div class="subscribe-body subscribe-desc md-margin-bottom-30"> -->
		<!-- 						<h1>Signup for free</h1> -->
		<!-- 						<p>To try the most advanced business platform for mobile and -->
		<!-- 							desktop</p> -->
		<!-- 					</div> -->
		<!-- 					<div class="subscribe-body"> -->
		<!-- 						<form id="signUpForm" class="form-wrap input-field"> -->
		<!-- 							<div class="form-wrap-group"> -->
		<!-- 								<input type="name" class="form-control" id="name" -->
		<!-- 									placeholder="Name"> -->
		<!-- 							</div> -->
		<!-- 							<div class="form-wrap-group border-left-transparent"> -->
		<!-- 								<input type="email" class="form-control" id="email" -->
		<!-- 									placeholder="Your Email"> -->
		<!-- 							</div> -->
		<!-- 							<div class="form-wrap-group"> -->
		<!-- 								<button type="submit" class="btn-danger btn-md btn-block">Signup</button> -->
		<!-- 							</div> -->
		<!-- 						</form> -->
		<!-- 					</div> -->
		<!-- 				</div> -->
		<!-- 			</div> -->
		<!-- 		</div> -->
		<!-- 		<!-- SUBSCRIBE END -->

		<!-- BEGIN ABOUT SECTION -->
		<section id="about">
			<!-- Services BEGIN -->
			<div class="container service-bg">
				<div class="row">
					<div class="col-sm-4">
						<div class="services sm-margin-bottom-100">
							<div class="services-wrap">
								<div class="service-body">
									<img
										src="<c:url value="/resources/onepage/img/widgets/icon1.png"/>"
										alt="">
								</div>
							</div>
							<h2>Rapid App Development</h2>
							<p>
								Build your mobile app using <br> the HTML5 in just a day
							</p>
						</div>
					</div>
					<div class="col-sm-4">
						<div class="services sm-margin-bottom-100">
							<div class="services-wrap">
								<div class="service-body">
									<img
										src="<c:url value="/resources/onepage/img/widgets/icon2.png"/>"
										alt="">
								</div>
							</div>
							<h2>Enterprise Middleware on Cloud</h2>
							<p>
								Mobilize your business data <br> with service integration
							</p>
						</div>
					</div>
					<div class="col-sm-4">
						<div class="services">
							<div class="services-wrap">
								<div class="service-body">
									<img
										src="<c:url value="/resources/onepage/img/widgets/icon3.png"/>"
										alt="">
								</div>
							</div>
							<h2>Deploy and Manage</h2>
							<p>
								Deploy and update app from the dashboard, <br> autherize
								your clients
							</p>
						</div>
					</div>
				</div>
			</div>
			<!-- Services END -->
		</section>
		<!-- END ABOUT SECTION -->

		<!-- BEGIN FEATURES SECTION -->
		<section id="features">
			<!-- Features BEGIN -->
			<div class="features-bg">
				<div class="container">
					<!-- 					<div class="heading"> -->
					<!-- 						<h2> -->
					<!-- 							<strong>Livo Mobile</strong> Main Features -->
					<!-- 						</h2> -->

					<!-- 					</div> -->
					<!-- //end heading -->

					<!-- Features -->
					<div class="row margin-bottom-70">
						<div class="col-md-6 md-margin-bottom-70">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen1.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">MOBILE CONTAINER</a>
									</h3>

									<p>
										<strong>CROSS-PLATFORM (Android-iOS)</strong><br> NATIVE
										DEVICE FUNCTIONS<br>-Camera<br>-Geolocation<br>-Vibration<br>-Contacts<br>more..
									</p>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen2.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">IDE</a>
									</h3>
									<p>
										Jquery Toolbar <br>Pre-built templates<br> Free
										style coding <br> Live Preview and Test <br>
										Collaboration with Github <br>
									</p>
								</div>
							</div>
						</div>
					</div>
					<!-- //end row -->
					<div class="row margin-bottom-80">
						<div class="col-md-6 md-margin-bottom-70">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen3.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">PLATFORM</a>
									</h3>
									<p>
										AD Auth <br> Push Notification<br> REST/SOAP
										Integration Wizard <br> SAP Connector and JavaScript
										Plugin <br> Central Deployment <br> Easy app
										distribution and update
									</p>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen4.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">AngularJS support</a>
									</h3>
									<p>Lorem niam ipsum dolor sit ammet adipiscing et suitem
										elit et nonuy nibh elit niam dolor</p>
								</div>
							</div>
						</div>
					</div>
					<!-- //end row -->
					<!-- End Features -->
				</div>
			</div>
			<!-- Features END -->
		</section>
		<!-- END FEATURES SECTION -->

		<!-- BEGIN PRICING SECTION -->
		<section id="pricing">
			<div class="pricing-bg">
				<div class="container">
					<!-- Pricing -->
					<div class="row no-space-row">
						<div class="col-md-4">
							<div class="pricing no-right-brd">
								<img
									src="<c:url value="/resources/onepage/img/widgets/icon4.png"/>"
									alt="">
								<h4>Starter Plan</h4>
								<span>Free</span>
								<ul class="pricing-features">
									<li>IDE</li>
									<li>Free Generic Mobile Container</li>
									<li>Up to 1 App</li>
									<li>100 MB Space</li>
								</ul>
								<!-- <button type="button" class="btn-brd-primary">Purchase</button> -->
							</div>
						</div>
						<div class="col-md-4">
							<div class="pricing pricing-red">
								<img
									src="<c:url value="/resources/onepage/img/widgets/icon5.png"/>"
									alt="">
								<h4>Monthly Plan</h4>
								<span>$90 / Month</span>
								<ul class="pricing-features">
									<li>Basic Auth</li>
									<li>Up to 3 App</li>
									<li>Free Generic Mobile Container</li>
									<li>1000 MB Space</li>
									<li>1 Developer</li>
									<li>Standart Support</li>
									<li>Monthly Backup</li>
									<li>50K API Call per Month</li>
								</ul>
								<!-- <button type="button" id="professionalPlanButton" class="btn-brd-white">Purchase</button> -->
							</div>
						</div>
						<div class="col-md-4">
							<div class="pricing no-left-brd">
								<img
									src="<c:url value="/resources/onepage/img/widgets/icon6.png"/>"
									alt="">
								<h4>Annual Plan</h4>
								<span>$60 / Month</span>
								<ul class="pricing-features">
									<li>Basic Auth</li>
									<li>Up to 10 App</li>
									<li>Free Generic Mobile Container</li>
									<li>10.000 MB Space</li>
									<li>Up to 10 Developer</li>
									<li>Standart Support</li>
									<li>Monthly Backup</li>
									<li>50K API Call per Month</li>
								</ul>
								<!-- <button type="button" class="btn-brd-primary">Purchase</button> -->
							</div>
						</div>
					</div>
					<!-- //end row -->
					<!-- End Pricing -->
				</div>
			</div>
		</section>

		<!-- BEGIN CLIENTS SECTION -->
		<section id="clients">
			<div class="clients">
				<div class="clients-bg">
					<div class="container">
						<div class="heading-blue">
							<h2>
								Several Platforms and HTML5-Compliant Javascript APIs
							</h2>
							<p>and let's see what is Gartner 2016 Reports saying</p>
						</div>
						<!-- //end heading -->

						<!-- Owl Carousel -->
						<div class="owl-carousel">
							<div class="item" data-quote="#client-quote-1">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo1.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-2">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo2.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-3">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo3.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-4">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo4.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-5">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo5.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-6">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo6.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-7">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo7.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-8">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo8.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-9">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo9.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-10">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo10.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-11">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo11.png"/>"
									alt="">
							</div>
						</div>
						<!-- End Owl Carousel -->
					</div>
				</div>

				<!-- Clients Quotes -->
				<div class="clients-quotes">
					<div class="container">
						<div class="client-quote" id="client-quote-1">
							<p>By 2020, more than 75% of enterprises will have adopted 
							   at least one mobile app development platform to accelerate 
							   their digital business transformation strategy, up from approximately 33% in 2015.</p>
							<h4>Strategic Planning Assumption</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Reports</span>
						</div>
						<div class="client-quote" id="client-quote-2">
							<p>In a 2015 global Gartner survey on enterprise mobile app development, 
							   34% of respondents indicated that they use a <strong>MADP</strong>(Mobile App Development Platform), 
							   while 11% use MBaaS(Mobile Back end as a Service) products and 9% use one or more RMAD(Rapid Mobile App Development) tools.</h4>
							<h4>Context - 1</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-3">
							<p>90% of respondents indicated that native SDKs are employed to build apps, 
							   while 44% also make use of open-source tools such as Apache Cordova, JQuery Mobile, React Native and other frameworks.</p>
							<h4>Context - 2</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-4">
							<p>While a single <strong>MADP</strong> may not provide everything an enterprise needs, 
							   it can be used to bridge systems of record to systems of innovation and 
							   enables a broader digital transformation platform that includes cloud, the IoT and analytics. </p>
							<h4>Context - 3</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-5">
							<p>Once an organization starts building and delivering mobile apps, 
							   demand across the enterprise inevitably grows. 
							   This potentially disruptive situation typically leads application and IT leaders to evaluate and adopt MADPs 
							   for the purpose of unifying, governing and scaling mobile app development activities.</p>
							<h4>Market Overview - 1</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-6">
							<p>As mobile development converges with traditional web and desktop development, 
							   <strong>MADP</strong>s are continuing to evolve rapidly to be not just silos for mobile app development. 
							   Indeed, nearly all of the <strong>MADP</strong> vendors listed this year can build apps for desktop and web, as well as mobile, in the same tool.</p>
							<h4>Market Overview - 2</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-7">
							<p>These three important principles must be addressed in the <strong>MADP</strong>:
							   1- Decoupling back-end and front-end development
							   2- Embracing open source and standards
							   3- Enabling self-service development</h4>
							<h4>Market Overview - 3</h4>
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-8">
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-9">
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-10">
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						<div class="client-quote" id="client-quote-11">
							<span>Mobile App Development Management Platform - Gartner 2016 Report</span>
						</div>
						</div>
					</div>
				</div>
				<!-- End Clients Quotes -->
			</div>
		</section>
		<!-- END CLIENTS SECTION -->


		<!-- BEGIN CONTACT SECTION -->
		<section id="contact">
			<!-- Footer -->
			<div class="footer">
				<div class="container">
					<div class="row">
						<div class="col-sm-6">
							<div class="heading-left-light">
								<h2>LIVO Corporation</h2>
								<p>
									1261 Locust Street #96 Walnut Creek, CA 94596<br/>
									Mobile: +1 (925) 718-6266<br/>
									www.livomobile.com<br/>
								</p>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form">
								<div class="form-wrap">
									<div class="form-wrap-group">
										<input type="text" placeholder="Your Name"
											class="form-control"> <input type="text"
											placeholder="Subject"
											class="border-top-transparent form-control">
									</div>
									<div class="form-wrap-group border-left-transparent">
										<input type="text" placeholder="Your Email"
											class="form-control"> <input type="text"
											placeholder="Contact Phone"
											class="border-top-transparent form-control">
									</div>
								</div>
							</div>
							<textarea rows="8" name="message"
								placeholder="Write comment here ..."
								class="border-top-transparent form-control"></textarea>
							<button type="submit" class="btn-danger btn-md btn-block">Send
								it</button>
						</div>
					</div>
					<!-- //end row -->
				</div>
			</div>
			<!-- End Footer -->

			<!-- Footer Coypright -->
			<div class="footer-copyright">
				<div class="container">
					<h3>LIVO</h3>
					<ul class="copyright-socials">
						<li><a href="#"><i class="fa fa-twitter"></i></a></li>
						<li><a href="#"><i class="fa fa-facebook"></i></a></li>
						<li><a href="#"><i class="fa fa-dribbble"></i></a></li>
						<li><a href="#"><i class="fa fa-pinterest"></i></a></li>
						<li><a href="#"><i class="fa fa-linkedin"></i></a></li>
					</ul>
				</div>
			</div>
			<!-- End Footer Coypright -->
		</section>
		<!-- END CONTACT SECTION -->
	</div>
	<!-- END MAIN LAYOUT -->
	<a href="#intro" class="go2top"><i class="fa fa-arrow-up"></i></a>

	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<!-- BEGIN CORE PLUGINS -->

	<script src="<c:url value="/resources/onepage/js/jquery.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/js/jquery-migrate.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/bootstrap/js/bootstrap.min.js"/>"
		type="text/javascript"></script>
	<!-- END CORE PLUGINS -->

	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script src="<c:url value="/resources/onepage/js/jquery.easing.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/jquery.parallax.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/smooth-scroll.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/js/owl.carousel.min.js"/>"
		type="text/javascript"></script>

	<!-- BEGIN Cubeportfolio -->
	<script
		src="<c:url value="/resources/onepage/js/jquery.cubeportfolio.min.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/portfolio.js"/>"
		type="text/javascript"></script>
	<!-- END Cubeportfolio -->

	<!-- BEGIN RevolutionSlider -->
	<script
		src="<c:url value="/resources/onepage/js/jquery.themepunch.revolution.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/js/jquery.themepunch.tools.min.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/revo-ini.js"/>"
		type="text/javascript"></script>
	<!-- END RevolutionSlider -->
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="<c:url value="/resources/onepage/js/layout.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/custom.js"/>"
		type="text/javascript"></script>


	<!-- END PAGE LEVEL SCRIPTS -->

	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>