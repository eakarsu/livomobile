						
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
<link
	href="<c:url value="/resources/adminpanel/global/plugins/jquery-ui/jquery-ui.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE LEVEL PLUGIN STYLES -->
<!-- BEGIN PAGE STYLES -->
<link
	href="<c:url value="/resources/adminpanel/admin/pages/css/tasks.css"/>"
	rel="stylesheet" type="text/css" />

<link
	href="<c:url value="/resources/adminpanel/admin/pages/css/pricing-table.css"/>"
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
			<!-- BEGIN PAGE TOP -->
			<div class="page-top">
				<!-- BEGIN TOP NAVIGATION MENU -->
				<div class="top-menu">
					<ul class="nav navbar-nav pull-right">
						<li class="separator hide"></li>
						<li class="separator hide"></li>
						<li class="separator hide"></li>
						<!-- BEGIN USER LOGIN DROPDOWN -->
						<!-- DOC: Apply "dropdown-dark" class after below "dropdown-extended" to change the dropdown styte -->
						<li class="dropdown dropdown-user dropdown-dark"><a
							href="javascript:;" class="dropdown-toggle"
							data-toggle="dropdown" data-hover="dropdown"
							data-close-others="true"> <span
								class="username username-hide-on-mobile"> ${loggedInUser.name} </span> <!-- DOC: Do not remove below empty space(&nbsp;) as its purposely used -->
								<img class="img-circle"
								src="<c:url value="/resources/adminpanel/admin/layout4/img/avatar13.png"/>"
								alt="" />
						</a>
							<ul class="dropdown-menu dropdown-menu-default">
								<li><a href="/myprofile"> <i class="icon-user"></i>
										My Profile
								</a></li>
								<li><a href="/support_request"> <i class="icon-user"></i>
										Support
								</a></li>
								<li class="divider"></li>
								<li><a href="/login"> <i class="icon-key"></i> Log
										Out
								</a></li>
							</ul></li>
						<!-- END USER LOGIN DROPDOWN -->
					</ul>
				</div>
				<!-- END TOP NAVIGATION MENU -->
			</div>
			<!-- END PAGE TOP -->
		</div>
		<!-- END HEADER INNER -->
	</div>
	<!-- END HEADER -->