<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>


<!-- Start of Companies Table. -->
<div class="row">
    <div class="col-md-12">
        <div class="block-web">
            <div class="header">
                <h3 class="content-header" data-i18n="deploy_versions"></h3>
            </div>
            <div class="porlets-content">
                <div class="table-responsive">
                    <div class="clearfix">
                        
                    </div>
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
                                        style="width: 379px;" data-i18n="app_name"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="deploy_id"></th>
                                     <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="deliverer"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="record_date"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="is_active"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="deploy_app"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="delete">Delete</th>
                                </tr>
                            </thead>

                            <tfoot>
                                <tr>
                                    <th rowspan="1" colspan="1" data-i18n="app_name"></th>
                                    <th rowspan="1" colspan="1" data-i18n="deploy_id"></th>
                                    <th rowspan="1" colspan="1" data-i18n="deliverer"></th>
                                    <th rowspan="1" colspan="1" data-i18n="record_date"></th>
                                    <th rowspan="1" colspan="1" data-i18n="is_active"></th>
                                    <th rowspan="1" colspan="1" data-i18n="deploy_app"></th>
                                    <th rowspan="1" colspan="1" data-i18n="delete"></th>
                                </tr>
                            </tfoot>
                            <tbody role="alert" aria-live="polite" aria-relevant="all">

                                <!-- Start of user list conditions -->
                                <c:choose>
                                    <c:when test="${deploy_versions.size() > 0}">
                                        <c:set var="i" scope="page" value="0" />
                                        <c:forEach var="deploy" items="${deploy_versions}">
                                            <c:choose>
                                                <c:when test="${i mod 2 eq 0}">
                                                    <tr class="gradeX odd">
                                                    </c:when>
                                                    <c:otherwise>
                                                    <tr class="gradeX even">
                                                    </c:otherwise>
                                                </c:choose>
                                                <td class=" ">${deploy.applicationName}</td>
                                                <td class="left">${deploy.deployId}</td>
                                                <td class="left">${deploy.userName}</td>
                                                <td class="left">${deploy.formatedDate}</td>
                                                <td><input style="border:1px red solid;" id="is_active${deploy.deployId}" name="is_active${deploy.deployId}" type="checkbox" ${deploy.isActive ? 'checked' : ''}></td>
                                        <td class="center">
                                            <input type="button" class="btn btn-success activateDeployment" data-deployid="${deploy.deployId}" data-appname="${deploy.applicationName}" data-timeuuid="${deploy.timeUUID}"/>
                                        </td>
                                        <td class="center ">
                                            <input type="button" class="btn btn-danger deleteDeployment" data-deployid="${deploy.deployId}" data-appname="${deploy.applicationName}" data-timeuuid="${deploy.timeUUID}"/>
                                        </td>
                                        </tr>
                                        <c:set var="i" scope="page" value="${i+1}" />
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <span data-i18n="there_is_no_deployment_or_error"></span>
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
<!-- End of Companies Table. -->
<script src="../js/pages/deployments.js"></script>
<script type="text/javascript">
    
    $('body').i18n();
    $('.activateDeployment').val($.i18n( 'activate_deployment' ));
    $('.deleteDeployment').val($.i18n( 'delete' ));
    var main_list = $("#idDeployVersions");

</script>