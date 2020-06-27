<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>


<div class="row">
	<div class="col-md-12">
		<div class="block-web">
			<div class="block-web" id="repeater">
				<div class="header">
					<div class="actions">
						<a href="#" class="minimize"><i class="fa fa-chevron-down"></i></a>
						<a href="#" class="refresh"><i class="fa fa-repeat"></i></a> <a
							href="#" class="close-down"><i class="fa fa-times"></i></a>
					</div>
					<h3 class="content-header" data-i18n="download_app"></h3>
				</div>
				<div class="porlets-content">

					<c:set var="isDelete" value="${isDelete}" />
					<c:set var="error" value="${error}" />
					<c:set var="message" value="${message}" />


					<c:choose>
						<c:when test="${error != true}">
							<div class="alert alert-success">
								<strong data-i18n="well_done"></strong>${message} <br>
								<h2>
									<a href="/downloadApplicationToClient/${applicationId}/" data-i18n="click_here_download"></a>
								</h2>
							</div>

						</c:when>
						<c:otherwise>

							<div class="alert alert-danger">
								<strong data-i18n="warning"></strong>${message}
							</div>

						</c:otherwise>

					</c:choose>


				</div>
				<!--/porlets-cont
                       </div><!--/block-web-->
			</div>
			<!-- /repeater -->
		</div>
		<!--/row-->
		<!-- Modal for errors -->
<script type="text/javascript">

        $('body').i18n();
        console.log('body3 -- > i18n()');

</script>