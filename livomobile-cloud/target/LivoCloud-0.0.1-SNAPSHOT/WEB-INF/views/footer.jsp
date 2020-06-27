 
	<!-- BEGIN FOOTER -->
	<div class="page-footer">
		<div class="page-footer-inner">
			2016 &copy; LivoMobile - <a href="http://www.livomobile.com" title="LivoMobile" target="_blank">www.livomobile.com</a>
		</div>
		<div class="scroll-to-top">
			<i class="icon-arrow-up"></i>
		</div>
	</div>
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
		src="<c:url value="/resources/adminpanel/global/scripts/adminpanel.js"/>"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
	<script>
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