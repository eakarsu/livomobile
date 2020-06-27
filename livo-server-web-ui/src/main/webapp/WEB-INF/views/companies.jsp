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
                <h3 class="content-header" data-i18n="companies"></h3>
            </div>
            <div class="porlets-content">
                <div class="table-responsive">
                    <div class="clearfix">
                        <div class="btn-group">
                            <button id="idCreateNewCompany" class="btn btn-primary">
                                <span data-i18n="add_new_company"></span> <i class="fa fa-plus"></i>
                            </button>
                        </div>
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
                                        style="width: 379px;" data-i18n="company"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="company_title"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="create_user"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="create_date"></th>
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="login_perm"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="edit"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="delete">Delete</th>
                                </tr>
                            </thead>

                            <tfoot>
                                <tr>
                                    <th rowspan="1" colspan="1" data-i18n="company"></th>
                                    <th rowspan="1" colspan="1" data-i18n="company_title"></th>
                                    <th rowspan="1" colspan="1" data-i18n="create_user"></th>
                                    <th rowspan="1" colspan="1" data-i18n="create_date"></th>
                                    <th rowspan="1" colspan="1" data-i18n="login_perm"></th>
                                    <th rowspan="1" colspan="1" data-i18n="edit"></th>
                                    <th rowspan="1" colspan="1" data-i18n="delete"></th>
                                </tr>
                            </tfoot>
                            <tbody role="alert" aria-live="polite" aria-relevant="all">

                                <!-- Start of user list conditions -->
                                <c:choose>
                                    <c:when test="${companies.size() > 0}">
                                        <c:set var="i" scope="page" value="0" />
                                        <c:forEach var="company" items="${companies}">
                                            <c:choose>
                                                <c:when test="${i mod 2 eq 0}">
                                                    <tr class="gradeX odd">
                                                    </c:when>
                                                    <c:otherwise>
                                                    <tr class="gradeX even">
                                                    </c:otherwise>
                                                </c:choose>
                                                <td class=" ">${company.companyId}</td>
                                                <td class="left">${company.companyTitle}</td>
                                                <td class="left">${company.createUser}</td>
                                                <td class="left">${company.formatedDate}</td>
                                                <td class="left">${company.loginPerm}</td>
                                        <td class="center">
                                            <input type="button" class="btn btn-success  editCompany" data-recordid="${company.recordId}" data-companyid="${company.companyId}" data-ctitle="${company.companyTitle}" data-loginp="${company.loginPerm}"/>
                                        </td>
                                        <td class="center ">
                                            <input type="button" class="btn btn-danger deleteCompany" data-recordid="${company.recordId}" data-companyid="${company.companyId}"/>
                                        </td>
                                        </tr>
                                        <c:set var="i" scope="page" value="${i+1}" />
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <span data-i18n="there_is_no_company_or_error"></span>
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
<script src="../js/pages/companies.js"></script>
<script type="text/javascript">

    var currCompanies = '', companyId = '';
    <c:choose>
        <c:when test="${companies.size() > 0}">
            <c:forEach  var="company" items="${companies}">
                companyId = '${company.companyId}';
                if(companyId !== 'LivoUsers' && companyId !== 'LivoAny')
                    currCompanies += '${company.recordId};' + companyId + '::';
            </c:forEach>
        </c:when>
    </c:choose>
    currCompanies = currCompanies.length > 0 ? currCompanies.substring(0,currCompanies.length - 2) : '';
    $('body').i18n();
    $('.editCompany').val($.i18n( 'edit' ));
    $('.deleteCompany').val($.i18n( 'delete' ));

</script>