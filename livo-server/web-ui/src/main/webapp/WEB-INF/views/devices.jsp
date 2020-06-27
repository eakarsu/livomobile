<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>


<!-- Start of Devices Table. -->
<div class="row">
    <div class="col-md-12">
        <div class="block-web">
            <div class="header">
                <h3 class="content-header" data-i18n="mobile_devices"></h3>
            </div>
            <div class="porlets-content">
                <div class="table-responsive">
                    <div class="margin-top-10"></div>
                    <div id="dynamic-table_wrapper"
                         class="dataTables_wrapper form-inline" role="grid">
                        <table
                            class="display table table-bordered table-striped dataTable"
                            id="dynamic-table" aria-describedby="dynamic-table_info">
                            <thead>
                                <tr role="row">
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 379px;" data-i18n="company"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="username"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="device_id"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="record_date">Record Date</th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="delete">Delete</th>
                                </tr>
                            </thead>

                            <tfoot>
                                <tr>
                                    <th rowspan="1" colspan="1" data-i18n="company"></th>
                                    <th rowspan="1" colspan="1" data-i18n="username"></th>
                                    <th rowspan="1" colspan="1" data-i18n="device_id"></th>
                                    <th rowspan="1" colspan="1" data-i18n="record_date"></th>
                                    <th rowspan="1" colspan="1" data-i18n="delete"></th>
                                </tr>
                            </tfoot>
                            <tbody role="alert" aria-live="polite" aria-relevant="all">

                                <!-- Start of user list conditions -->
                                <c:choose>
                                    <c:when test="${devices.size() > 0}">
                                        <c:set var="i" scope="page" value="0" />
                                        <c:forEach var="device" items="${devices}">
                                            <c:choose>
                                                <c:when test="${i mod 2 eq 0}">
                                                    <tr class="gradeX odd">
                                                    </c:when>
                                                    <c:otherwise>
                                                    <tr class="gradeX even">
                                                    </c:otherwise>
                                                </c:choose>
                                                <td class=" ">${device.companyId}</td>
                                                <td class="left">${device.userPrincipal}</td>
                                                <td class="left">${device.deviceId}</td>
                                                <td class="left">${device.recordDate}</td>

                                        <td class="center ">
                                        <input type="button" class="btn btn-danger  deleteDevice"  data-companyId="${device.companyId}" data-userPrincipal="${device.userPrincipal}" data-deviceId="${device.deviceId}"/>
                                        </td>
                                        </tr>
                                        <c:set var="i" scope="page" value="${i+1}" />
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <span data-i18n="there_is_no_device_or_error"></span>
                                </c:otherwise>
                               
                            </c:choose>

                            <!-- End of user list conditions -->
                            </tbody>
                        </table>
                    </div>
                </div>
                <!--/table-responsive-->
            </div>
            <!--/porlets-content-->


        </div>
        <!--/block-web-->
    </div>
    <!--/col-md-12-->
</div>
<!-- End of Devices Table. -->
<script src="../js/pages/devices.js"></script>
<script type="text/javascript">

    $('body').i18n();	
    $('.deleteDevice').val($.i18n( 'delete' ));

</script>


































<!--   	<ul class="sub">
<c:choose>
    <c:when test="${application.screens.size() > 0}">
        <c:forEach items="${application.screens}" var="entry">

                <li class="screen" data-screen-id="${entry.key}"><a href="#"><i class="fa fa-angle-right"></i>
                                <i class="fa fa-code"></i> <c:out value="${entry.value.title}" /></a></li>
        </c:forEach>
        
    </c:when>
    <c:otherwise>
            <li><span
                    style="display: block; font-size: 10pt; margin: 10px; text-align: center;">You
                            don't have any screens.</span></li>
    </c:otherwise>
</c:choose>
</ul>
-->