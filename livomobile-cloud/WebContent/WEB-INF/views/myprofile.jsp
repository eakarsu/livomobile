
<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib prefix="botDetect" uri="botDetect"%>

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
					<div class="page-title" style="width: 100%;">
						<div class="row">
							<div class="col-md-12">
		
								<div class="alert alert-success" style="padding-top: 0px; margin-top: 0px; padding-bottom: 0px;">
		
									<h1><strong>My Profile</strong></h1>
		
								</div>
							</div>
		
						</div>

						<div class="portlet-body form">
							<!-- BEGIN FORM-->

							<form:form action="updateUserProfile" method="post"
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
												  class="form-control" value="${loggedInUser.name}" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Email <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input name="email" type="text" class="form-control" value="${loggedInUser.email}" readonly="readonly" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Password <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input name="userPassword" type="password" class="form-control" value="${loggedInUser.userPassword}" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Company Name <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input name="companyName" type="text" class="form-control" value="${loggedInUser.companyName}" />
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

