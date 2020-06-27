<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib prefix="botDetect" uri="botDetect"%>

<!DOCTYPE html>
<!-- 
Template Name: Metronic - Responsive Admin Dashboard Template build with Twitter Bootstrap 3.3.4
Version: 3.8.1
Author: KeenThemes
Website: http://www.keenthemes.com/
Contact: support@keenthemes.com
Follow: www.twitter.com/keenthemes
Like: www.facebook.com/keenthemes
Purchase: http://themeforest.net/item/metronic-responsive-admin-dashboard-template/4021469?ref=keenthemes
License: You must have a valid license purchased only from themeforest(the above link) in order to legally use the theme for your project.
-->
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
<meta charset="utf-8" />
<title>Livo Mobile | Admin Dashboard</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1" name="viewport" />
<meta name="description" content="Flexible mobile app platform helps midsize companies to roll out enterprise grade, rich mobile applications with only web development skills without complex iOS or Android code development">
<meta name="author" content="Livo Corporate - Omur">
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/font-awesome/css/font-awesome.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/simple-line-icons/simple-line-icons.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/bootstrap/css/bootstrap.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/uniform/css/uniform.default.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
<link
	href="<c:url value="/resources/adminpanel/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/fullcalendar/fullcalendar.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/jqvmap.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/morris/morris.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE LEVEL PLUGIN STYLES -->
<!-- BEGIN PAGE STYLES -->
<link
	href="<c:url value="/resources/adminpanel/admin/pages/css/tasks.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE STYLES -->
<!-- BEGIN THEME STYLES -->
<!-- DOC: To use 'rounded corners' style just load 'components-rounded.css' stylesheet instead of 'components.css' in the below style tag -->
<link
	href="<c:url value="/resources/adminpanel/global/css/components-rounded.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/css/plugins.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/admin/layout4/css/layout.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/admin/layout4/css/themes/light.css"/>"
	rel="stylesheet" type="text/css" id="style_color" />
<link
	href="<c:url value="/resources/adminpanel/admin/layout4/css/custom.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END THEME STYLES -->


</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<!-- DOC: Apply "page-header-fixed-mobile" and "page-footer-fixed-mobile" class to body element to force fixed header or footer in mobile devices  -->
<!-- DOC: Apply "page-sidebar-closed" class to the body and "page-sidebar-menu-closed" class to the sidebar menu element to hide the sidebar by default -->
<!-- DOC: Apply "page-sidebar-hide" class to the body to make the sidebar completely hidden on toggle -->
<!-- DOC: Apply "page-sidebar-closed-hide-logo" class to the body element to make the logo hidden on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-hide" class to body element to completely hide the sidebar on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-fixed" class to have fixed sidebar -->
<!-- DOC: Apply "page-footer-fixed" class to the body element to have fixed footer -->
<!-- DOC: Apply "page-sidebar-reversed" class to put the sidebar on the right side -->
<!-- DOC: Apply "page-full-width" class to the body element to have full width page without the sidebar menu -->



<body class="page-header-fixed">
	<!-- BEGIN HEADER -->
	<div class="page-header navbar navbar-fixed-top">
		<!-- BEGIN HEADER INNER -->
		<div class="page-header-inner">
			<!-- BEGIN LOGO -->
			<div class="page-logo">
				<a href="/"> <img
					src="<c:url value="/resources/adminpanel/admin/layout4/img/LivoLogo.png"/>"
					alt="logo" class="logo-default" />
				</a>

			</div>
			<!-- END LOGO -->

		</div>
		<!-- END HEADER INNER -->
	</div>
	<!-- END HEADER -->
	<div class="clearfix"></div>
	<!-- BEGIN CONTAINER -->
	<div class="page-container">

		<!-- BEGIN CONTENT -->
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
			<!-- BEGIN PAGE BREADCRUMB -->
			<div class="row">
				<div class="col-md-12">

					<div class="alert alert-success">

						<strong>Welcome to Livo Mobile!</strong> <br>Please update
						your account infos.

					</div>
				</div>

			</div>
			<!-- BEGIN PAGE CONTENT-->
			<div class="row">
				<div class="col-md-12">
					<!-- BEGIN VALIDATION STATES-->
					<div class="portlet box purple">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-gift"></i>Account Infos
							</div>
							<div class="tools">
								<a href="javascript:;" class="collapse"> </a> <a
									href="#portlet-config" data-toggle="modal" class="config">
								</a> <a href="javascript:;" class="reload"> </a> <a
									href="javascript:;" class="remove"> </a>
							</div>
						</div>
						<div class="portlet-body form">
							<!-- BEGIN FORM-->

							<form:form action="updateUserInfos" method="post"
								modelAttribute="userBean" id="form_sample_1"
								class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-danger display-hide">
										<button class="close" data-close="alert"></button>
										You have some form errors. Please check below.
									</div>
									<div class="alert alert-success display-hide">
										<button class="close" data-close="alert"></button>
										Your form validation is successful!
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Name <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input type="text" name="name" data-required="1"
												  class="form-control" value="${name}" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Email <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input name="email" type="text" class="form-control"
												  value="${email}" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Password <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input name="userPassword" type="password" class="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Company Name <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input name="companyName" type="text" class="form-control" />
										</div>
									</div>
									<div class="form-group">
					 
											<label for="captchaCodeTextBox" class="prompt col-md-3">
												Retype the code from the picture <span> * </span></label>
										<div class="col-md-4">
											<!-- Adding BotDetect Captcha to the page -->
											<botDetect:captcha id="formCaptcha" codeLength="4"
												imageWidth="150" imageStyles="graffiti, graffiti2" />
											<div class="validationDiv">
												<input id="captchaCodeTextBox" type="text"
													name="captchaCodeTextBox" /><br> 
											</div>

										</div>
							 
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-offset-3 col-md-9">
										<c:if test="${captchaValidateError}">
											<div id="validateResponse" class="alert alert-warning">${inCorrectCaptcha}</div>
										</c:if>
										<c:if test="${isUpdated==false}">
											<div id="updateFailResponse" class="alert alert-warning">${responseText}</div>
										</c:if>
									</div>
								</div>
								<div class="form-actions">
									<div class="row">
										<div class="col-md-offset-3 col-md-9">
											<button type="button" class="btn default">Cancel</button>
											<button type="submit" class="btn green">Save</button>
										</div>
									</div>




								</div>


							</form:form>
							<!-- END FORM-->
						</div>
					</div>
					<!-- END VALIDATION STATES-->
				</div>
			</div>

			<!-- END PAGE BREADCRUMB -->
			<!-- BEGIN PAGE CONTENT INNER -->
			<!-- END PAGE CONTENT INNER -->
		</div>
		<!-- END CONTENT -->
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
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-validation/js/jquery.validate.min.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-validation/js/additional-methods.min.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/select2/select2.min.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-wysihtml5/wysihtml5-0.3.0.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-wysihtml5/bootstrap-wysihtml5.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/ckeditor/ckeditor.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-markdown/js/bootstrap-markdown.js"/>"></script>
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-markdown/lib/markdown.js"/>"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL STYLES -->
	<script type="text/javascript"
		src="<c:url value="/resources/adminpanel/admin/pages/scripts/form-validation.js"/>"></script>
	<!-- END PAGE LEVEL STYLES -->

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
	</script>

	<script>
		jQuery(document).ready(function() {
			Metronic.init(); // init metronic core componets
			Layout.init(); // init layout
			Demo.init(); // init demo features 
			// 			Index.init(); // init index page
			// 			Tasks.initDashboardWidget(); // init tash dashboard widget  
			FormValidation.init();
		});
	</script>


	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>