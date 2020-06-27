
<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@include file="header.jsp"%>

<div class="page-content-main" id="idPageContent">

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
						<div class="row">
							<div class="col-md-12">
		
								<div class="alert alert-success">
		
									<strong>Welcome to LivoMobile Cloud Admin Console!</strong> 
									<br>
									Hi ${loggedInUser.name},
									<br>
									You can view the price list and buy a package. Addition to this, you can edit your personal data from the menu in right-top side.
		
								</div>
							</div>
		
						</div>

					</div>
					<!-- END PAGE TITLE -->
					
				</div>
				<!-- END PAGE HEAD -->
				<!-- BEGIN PAGE BREADCRUMB -->

				<!-- END PAGE BREADCRUMB -->
				<!-- BEGIN PAGE CONTENT INNER -->
				<!-- END PAGE CONTENT INNER -->
			</div>
			<!-- END CONTENT -->
		</div>
	</div>
	<!-- END CONTAINER -->
</div>

<%@include file="footer.jsp"%>

