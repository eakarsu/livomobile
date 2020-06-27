<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
<meta charset="utf-8"/>
<title>LivoMobile Cloud | Login Options</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<meta name="description" content="Flexible mobile app platform helps midsize companies to roll out enterprise grade, rich mobile applications with only web development skills without complex iOS or Android code development">
<meta name="author" content="Livo Corporate - Omur">
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<!-- <link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css"/> -->
<link href="<c:url value="/resources/adminpanel/global/plugins/font-awesome/css/font-awesome.min.css"/>" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/global/plugins/simple-line-icons/simple-line-icons.min.css"/>" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/global/plugins/bootstrap/css/bootstrap.min.css"/>" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/global/plugins/uniform/css/uniform.default.css"/>" rel="stylesheet" type="text/css"/>
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL STYLES -->
<link href="<c:url value="/resources/adminpanel/global/plugins/select2/select2.css"/>" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/admin/pages/css/login-soft.css"/>" rel="stylesheet" type="text/css"/>
<!-- END PAGE LEVEL SCRIPTS -->
<!-- BEGIN THEME STYLES -->
<link href="<c:url value="/resources/adminpanel/global/css/components-rounded.css"/>" id="style_components" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/global/css/plugins.css"/>" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/admin/layout/css/layout.css"/>" rel="stylesheet" type="text/css"/>
<link id="style_color" href="<c:url value="/resources/adminpanel/admin/layout/css/themes/default.css"/>" rel="stylesheet" type="text/css"/>
<link href="<c:url value="/resources/adminpanel/admin/layout/css/custom.css"/>" rel="stylesheet" type="text/css"/>
<!-- END THEME STYLES -->
<link rel="shortcut icon" href="favicon.ico"/>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="login">
<!-- BEGIN LOGO -->
<div class="logo">
	<a href="index.html">
	<img src="<c:url value="/resources/adminpanel/admin/layout4/img/LivoLogo.png"/>" 
	alt=""/>
	</a>
</div>
<!-- END LOGO -->
<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
<div class="menu-toggler sidebar-toggler">
</div>
<!-- END SIDEBAR TOGGLER BUTTON -->
<!-- BEGIN LOGIN -->
<div class="content">
	<!-- BEGIN LOGIN FORM -->
	<form:form class="login-form" method="post" action="login" modelAttribute="loginBean">
		<h3 class="form-title">Login to your account</h3>
		<div class="alert alert-danger display-hide">
			<button class="close" data-close="alert"></button>
			<span>Enter any email and password. </span>
		</div>
		<div class="form-group">
			<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
			<label class="control-label visible-ie8 visible-ie9">User eMail</label>
			<div class="input-icon">
				<i class="fa fa-user"></i>
				<form:input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="User eMail" name="userMail" path=""/>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label visible-ie8 visible-ie9">Password</label>
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<form:input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="Password" name="password" path=""/>
			</div>
		</div>
		<div class="form-actions">
<!-- 			<label class="checkbox"> -->
<!-- 			<input type="checkbox" name="remember" value="1"/> Remember me </label> -->
			<button type="submit" class="btn blue pull-right">
			Login <i class="m-icon-swapright m-icon-white"></i>
			</button>
		</div>
		<div class="login-options">

		</div>
		<div class="forget-password">
			<h4>Forgot your password ?</h4>
			<p>
				 no worries, click <a href="javascript:;" id="forget-password">
				here </a>
				to reset your password.
			</p>
		</div>
		<div class="create-account">
			<p>
				 Don't have an account yet ?&nbsp; <a href="javascript:;" id="register-btn">
				Create an account </a>
			</p>
		</div>
	</form:form>
	<!-- END LOGIN FORM -->
	<!-- BEGIN FORGOT PASSWORD FORM -->
	<form class="forget-form" action="forgotPassword" method="post">
		<h3>Forget Password ?</h3>
		<p>
			 Enter your e-mail address below to reset your password.
		</p>
		<div class="form-group">
			<div class="input-icon">
				<i class="fa fa-envelope"></i>
				<input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="Email" name="email"/>
			</div>
			<div id="forgot_message" style="color: #ffcc33;"></div>
		</div>
		<div class="form-actions">
			<button type="button" id="back-btn" class="btn">
			<i class="m-icon-swapleft"></i> Back </button>
			<button type="submit" class="btn blue pull-right">
			Submit <i class="m-icon-swapright m-icon-white"></i>
			</button>
		</div>
	</form>
	<!-- END FORGOT PASSWORD FORM -->
	<!-- BEGIN RESET PASSWORD FORM -->
	<form class="reset-form" action="resetPassword" method="post">
		<h3>Forget Password ?</h3>
		<p>
			 Enter your e-mail address below to reset your password.
		</p>
		<input type="hidden" class="form-control" id="email" name="email" value="${email}" />
		<input type="hidden" class="form-control" id="vcode" name="vcode" value="${vcode}" />
		<div class="form-group">
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="New Password" id="newPassword" name="newPassword"/>
			</div>
		</div>
		<div class="form-group">
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="New Password Again" id="newPasswordAgain" name="newPasswordAgain"/>
			</div>
		</div>
		<div id="reset_message" style="color: #ffcc33;"></div>
		<div class="form-actions">
			<button id="back-btn" type="button" class="btn" onclick="document.location.href='/login';">
			<i class="m-icon-swapleft"></i> Login </button>
			<button type="submit" class="btn blue pull-right">
			Submit <i class="m-icon-swapright m-icon-white"></i>
			</button>
		</div>
	</form>
	<!-- END RESET PASSWORD FORM -->
	<!-- BEGIN REGISTRATION FORM -->
	<form class="register-form" action="signUp" method="post" modelAttribute="signUpBean">
		<h3>Sign Up</h3>
		<p>
			 Enter your personal details below:
		</p>
		<div class="form-group">
			<label class="control-label visible-ie8 visible-ie9">Full Name</label>
			<div class="input-icon">
				<i class="fa fa-font"></i>
				<input class="form-control placeholder-no-fix" type="text" placeholder="Full Name" id="name" name="name"/>
			</div>
		</div>
		<div class="form-group">
			<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
			<label class="control-label visible-ie8 visible-ie9">Email</label>
			<div class="input-icon">
				<i class="fa fa-envelope"></i>
				<input class="form-control placeholder-no-fix" type="text" placeholder="Email as an Username" id="email" name="email"/>
			</div>
		</div>
		
		<div class="form-group">
			<c:if test="${isRegisteredEmail}">
				<div id="signUpResponse" class="alert alert-warning">${userControlResponse}</div>
			</c:if>
		</div>
		
		<div class="form-actions">
			<button id="register-back-btn" type="button" class="btn" onclick="document.location.href='/login';">
			<i class="m-icon-swapleft"></i> Login </button>
			<button type="submit" id="register-submit-btn" class="btn blue pull-right">
			Sign Up <i class="m-icon-swapright m-icon-white"></i>
			</button>
		</div>
	</form>
	<!-- END REGISTRATION FORM -->
</div>
<!-- END LOGIN -->
<!-- BEGIN COPYRIGHT -->
<div class="copyright">
	 2016 &copy; LivoMobile - <a href="http://www.livomobile.com" title="LivoMobile" target="_blank">www.livomobile.com</a>
</div>
<!-- END COPYRIGHT -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<!--[if lt IE 9]>
<script src="<c:url value="/resources/adminpanel/global/plugins/respond.min.js"/>" ></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/excanvas.min.js"/>" ></script> 
<![endif]-->
<script src="<c:url value="/resources/adminpanel/global/plugins/jquery.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/jquery-migrate.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/bootstrap/js/bootstrap.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/jquery.blockui.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/uniform/jquery.uniform.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/jquery.cokie.min.js"/>" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="<c:url value="/resources/adminpanel/global/plugins/jquery-validation/js/jquery.validate.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/backstretch/jquery.backstretch.min.js"/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value="/resources/adminpanel/global/plugins/select2/select2.min.js"/>" ></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="<c:url value="/resources/adminpanel/global/scripts/metronic.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/admin/layout/scripts/layout.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/admin/layout/scripts/demo.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/adminpanel/admin/pages/scripts/login-soft.js"/>" type="text/javascript"></script>
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
</script>

<script>

	var forgotPasswordMessage = '${forgotPasswordMessage}', resetMessage = '${resetMessage}', isRegisterForm = '${isRegisterForm}';
	if(forgotPasswordMessage && forgotPasswordMessage.length){
		jQuery('.login-form').hide();
        jQuery('.forget-form').show();
        jQuery('#forgot_message').html('<br>' + forgotPasswordMessage);
	}
	if(resetMessage && resetMessage.length){
		jQuery('.login-form').hide();
        jQuery('.reset-form').show();
        if(resetMessage !== 'valid')
        	jQuery('#reset_message').html(resetMessage + '<br><br>');
	}
	
	if(isRegisterForm){
		jQuery('.login-form').hide();
        jQuery('.register-form').show();
	}

	jQuery(document).ready(function() {     
	  	Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
	  	Login.init();
	  	Demo.init();
        // init background slide images
        $.backstretch([
        "<c:url value='/resources/adminpanel/admin/pages/media/bg/1.jpg'/>",
        "<c:url value='/resources/adminpanel/admin/pages/media/bg/2.jpg'/>",
        "<c:url value='/resources/adminpanel/admin/pages/media/bg/3.jpg'/>",
        "<c:url value='/resources/adminpanel/admin/pages/media/bg/4.jpg'/>"
        ], {
          fade: 1000,
          duration: 8000
    	}
    	);
	});
	
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