<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>


<!-- Start of User Table. -->
<div class="row">
    <div class="col-md-12">
        <div class="block-web">
            <div class="header">
                <h3 class="content-header" data-i18n="groups"></h3>
            </div>
            <div class="porlets-content">
                <div class="table-responsive">
                    <div class="clearfix">
                        <div class="btn-group">
                            <button id="idCreateNewGroup" class="btn btn-primary">
                                <span data-i18n="add_new_group"></span> <i class="fa fa-plus"></i>
                            </button>

                        </div>
                    </div>
                    <div class="margin-top-10"></div>
                    <div id="dynamic-table_wrapper"
                         class="dataTables_wrapper form-inline" role="grid">
                        <table
                            class="display table table-bordered table-striped dataTable"
                            id="dynamic-table-groups" aria-describedby="dynamic-table_info">
                            <thead>
                                <tr role="row">
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 379px;" data-i18n="group_name"></th>
                                    <!--                                    <th class="sorting" role="columnheader" tabindex="0"
                                                                            aria-controls="dynamic-table" rowspan="1" colspan="1"
                                                                            aria-label="Activate to sort column ascending"
                                                                            style="width: 347px;">User Password</th>-->
                                    <!--                                    <th class=" sorting" role="columnheader" tabindex="0"
                                                                            aria-controls="dynamic-table" rowspan="1" colspan="1"
                                                                            aria-label="Activate to sort column ascending"
                                                                            style="width: 253px;">User Role</th>-->
                                    <th class=" sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-sort="descending"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="desc"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="domain"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="edit"></th>
                                    <th class="sorting" role="columnheader" tabindex="0"
                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                        aria-label="Activate to sort column ascending"
                                        style="width: 179px;" data-i18n="delete"></th>
                                </tr>
                            </thead>

                            <tfoot>
                                <tr>
                                    <th rowspan="1" colspan="1" data-i18n="group_name"></th>
                                    <!--<th rowspan="1" colspan="1">User Password</th>-->
                                    <!--<th rowspan="1" colspan="1">User Role</th>-->
                                    <th rowspan="1" colspan="1" data-i18n="desc"></th>
                                    <th rowspan="1" colspan="1" data-i18n="domain"></th>
                                    <th rowspan="1" colspan="1" data-i18n="edit"></th>
                                    <th rowspan="1" colspan="1" data-i18n="delete"></th>
                                </tr>
                            </tfoot>
                            <tbody role="alert" aria-live="polite" aria-relevant="all">

                                <!-- Start of user list conditions -->
                                <c:choose>
                                    <c:when test="${groups.size() > 0}">
                                        <c:set var="i" scope="page" value="0" />
                                        <c:forEach var="group" items="${groups}">
                                            <c:choose>
                                                <c:when test="${i mod 2 eq 0}">
                                                    <tr class="gradeX odd">
                                                    </c:when>
                                                    <c:otherwise>
                                                    <tr class="gradeX even">
                                                    </c:otherwise>
                                                </c:choose>
                                                <td class=" ">${group.name}</td>
                                                <!--<td class=" ">*********</td>-->
                                                <td class="left">${group.description}</td>
                                                <td class="left"> ${group.domain}</td>
                                                <td class="center">
                                        <input type="button" class="btn btn-success  editGroup" data-group="${group.name}"  data-domain="${group.domain}" data-description="${group.description}"/>
                                        <td class="center ">
                                        <input type="button" class="btn btn-danger  deleteGroup" data-group="${group.name}" data-domain="${group.domain}"/>
                                        </td>
                                        </tr>
                                        <c:set var="i" scope="page" value="${i+1}" />
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <span data-i18n="there_is_no_group_or_error"></span>
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
<!-- End of User Table. -->
<script type="text/javascript">

    $('body').i18n();
    $('.editGroup').val($.i18n( 'edit' ));
    $('.deleteGroup').val($.i18n( 'delete' ));
    var groupCompanies = {};
    var currGroups = '${groups}';
    <c:forEach items="${group_companies}" var="item">
      groupCompanies["${item.groupName}"]= "${item.group_companies}";
    </c:forEach>
    function setSelectedGroupComp(groupName){
        console.log('setSelectedGroupComp::groupName --> ' + groupName + ' -- groupCompanies ' + groupCompanies[groupName]);
        $('#groupCompanyList2').multiSelect('deselect_all');
        var options = $('#groupCompanyList2')[0].options;
        for(var i = 0; i < optLen; i++)
            options[i].selected = false;
        var companyArr = groupCompanies[groupName] ? groupCompanies[groupName].split(',') : '';
        var optLen = options.length, arrLen = companyArr.length;
        for(var i = 0; i < optLen; i++){
            
            for(var k = 0; k < arrLen; k++){
                
                //console.log('setSelectedComp: ' + options[i].value + ' == ' + companyArr[k] + ' --> ' + (options[i].value == companyArr[k]));
                if(options[i].value == companyArr[k]){
                    options[i].selected = true;
                    $('#groupCompanyList2').multiSelect('select',options[i].value);
                    break;
                }
                
            }
            
        }
        
    }

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