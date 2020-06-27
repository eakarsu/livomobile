<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

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
<meta content="" name="description" />
<meta content="" name="author" />
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
				<a href="/LivoCloud/"> <img
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
					<div class="alert alert-danger">
						${errorText}  
					</div>
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