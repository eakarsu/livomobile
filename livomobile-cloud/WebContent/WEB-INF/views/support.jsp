<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
	
<%@include file="header.jsp"%>

<div class="page-content-main" id="idPageContent">
	
	<!-- END HEADER -->
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
			<!-- BEGIN PAGE BREADCRUMB -->
			<div class="row">
				<div class="col-md-12">

					<div class="alert alert-success">
					<c:choose>
					    <c:when test="${supportMessage != null}">
					        <strong style="color: red">${supportMessage}</strong>
					    </c:when>    
					    <c:otherwise>
					        <strong>Welcome to Livo Mobile!</strong> <br>Please send your support request.
					    </c:otherwise>
					</c:choose>
						

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
								<i class="fa fa-gift"></i>Support Request
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

							<form:form action="process_support" method="post"
								modelAttribute="supportBean" id="form_sample_1"
								class="form-horizontal">
								<input type="hidden" name="userEmail" value="${user.email}" />
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
										<label class="control-label col-md-3">Title <span
											class="required"> * </span>
										</label>
										<div class="col-md-4">
											<input type="text" name="title" data-required="1" class="form-control" value="${title}" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-md-3">Description
										</label>
										<div class="col-md-4">
											<textarea cols="4" rows="4" name="description" class="form-control" value="${description}"></textarea>
										</div>
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
</div>
</div>

<%@include file="footer.jsp"%>