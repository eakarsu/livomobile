<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>

<div class="col-md-12">
    <div class="block-web">
        <div class="header">
            <h3 class="content-header" data-i18n="global_sett"></h3>
        </div>
        <div class="porlets-content">
            <div id="smtpSettingsProgressWizard" class="basic-wizard">
                <ul id="globalSettingsNavs" class="nav nav-pills nav-justified">
                    <li><a href="#smtpTab1" id="tab1" data-toggle="tab"><span data-i18n="smptp_server"></span></a></li>
                    <li><a href="#mailTab" id="tab2" data-toggle="tab"><span data-i18n="email_templates"></span></a></li>
                    <c:if test="${checkAdmin}"><li><a href="#SSTab" id="tab3" data-toggle="tab"><span data-i18n="sec_sett"></span></a></li></c:if>
                </ul>
                <div class="tab-content">
                    <div class="progress progress-striped active">
                        <div class="progress-bar" role="progressbar" aria-valuenow="45"
                             aria-valuemin="0" aria-valuemax="100"></div>
                    </div>

                    <div class="tab-pane" id="smtpTab1">
                        <form id="configureSmtpSettingsForm" class="form-horizontal row-border" autocomplete="off" action="/configureSmtpSettings" method="POST">
                            <div class="form-group">
                                <label class="col-sm-2 control-label"> <span data-i18n="email_from_opt"></span>
                                </label>
                                <div class="col-sm-4">
                                    <c:if test="${mailProps != null }">
                                        <input  id="displayMail" name="displayMail" type="text" value="${mailProps.from}" placeholder="jdoe@example.com" class="form-control" ></input>
                                    </c:if>
                                    <c:if test="${mailProps == null }">
                                        <input  id="displayMail" name="displayMail" type="text" value="" placeholder="jdoe@example.com" class="form-control" ></input>
                                    </c:if>
                                </div>
                                <label class="col-sm-2 control-label"> <span>Port</span>
                                </label>
                                <div class="col-sm-4">

                                    <c:if test="${mailProps.port != null }">
                                        <input  id="port" name="port" type="text"  value="${mailProps.port}"  placeholder="587" class="form-control" required></input>
                                    </c:if> 
                                    <c:if test="${mailProps.port == null }">
                                        <input  id="port" name="port" type="text"  value=""  placeholder="587" class="form-control" required></input>
                                    </c:if>
                                </div>


                            </div>
                            <!--/form-group-->
                            <!--/form-group-->
                            <div class="form-group">
                                <label class="col-sm-2 control-label"> <span data-i18n="smptp_server"></span>
                                </label>
                                <div class="col-sm-4">
                                    <c:if test="${mailProps.hostName != null }">
                                        <input id="smtpServer" name="smtpServer" type="text"  value="${mailProps.hostName}"  placeholder="smtp.example.com" class="form-control" required></input>
                                    </c:if>
                                    <c:if test="${mailProps == null }">
                                        <input id="smtpServer" name="smtpServer" type="text"  value=""  placeholder="smtp.example.com" class="form-control" required></input>

                                    </c:if>
                                </div>
                                <label class="col-sm-2 control-label" data-i18n="conn_type"></label>
                                <div class="col-sm-4">
                                    <select id="mailHostType" name="mailHostType" id="mailHostType" class="form-control" required>
                                        <c:set var="setHostType">${mailProps.type}</c:set>

                                        <c:if test="${mailProps.type != null }">
                                            <option value="${mailProps.type}"> ${mailProps.type} </option>
                                        </c:if>
                                        <c:if test="${setHostType != 'STARTTLS'}" >
                                            <option value="STARTTLS"> STARTTLS </option>
                                        </c:if>
                                        <c:if test="${setHostType != 'PLAIN' }">
                                            <option value="PLAIN"> PLAIN </option>
                                        </c:if>
                                        <c:if test="${setHostType != 'SSL'}">
                                            <option value="SSL"> SSL </option>
                                        </c:if>

                                    </select>
                                </div>
                            </div>
                            <!--/form-group-->


                            <div class="form-group">
                                <br>

                            </div> 



                            <div class="form-group">
                                <label class="col-sm-2 control-label"> <span data-i18n="require_auth"></span>
                                </label>
                                <div class="col-sm-4">

                                    <c:choose>
                                        <c:when test="${mailProps.authentication}">
                                            <input id="switch-authentication" name="switch-authentication" type="checkbox" checked data-size="small"></input>

                                        </c:when>
                                        <c:when test="${!mailProps.authentication}">
                                            <input id="switch-authentication" name="switch-authentication" type="checkbox"  data-size="small"></input>
                                        </c:when>
                                    </c:choose>

                                </div>
                            </div> 



                            <!--/form-group-->
                            <div class="form-group">
                                <label class="col-sm-2 control-label"> <span data-i18n="smptp_username"></span>
                                </label>
                                <div class="col-sm-4">
                                    <c:if test="${mailProps != null }">
                                        <input  id="smtpUserName" name="smtpUserName" type="text" value="${mailProps.userName}" placeholder="jdoe@example.com" class="form-control" required></input>
                                    </c:if>
                                    <c:if test="${mailProps == null }">
                                        <input  id="smtpUserName" name="smtpUserName" type="text" value="" placeholder="jdoe@example.com" class="form-control" required></input>
                                    </c:if>
                                </div>
                            </div>
                            <!--/form-group-->
                            <div class="form-group">
                                <label class="col-sm-2 control-label"> <span data-i18n="smptp_pass"></span>
                                </label>
                                <div class="col-sm-4">
                                    <c:if test="${mailProps != null }">
                                        <input id="smtpPassword" name="smtpPassword" type="password" value="${mailProps.password}" placeholder="******" class="form-control" required></input>
                                    </c:if>
                                    <c:if test="${mailProps == null }">
                                        <input id="smtpPassword" name="smtpPassword" type="password" value="" placeholder="******" class="form-control" required></input>

                                    </c:if>
                                </div>
                            </div>
                            <!--/form-group-->
                            <!--/form-group-->

                            <div class="bottom " style="background: none; border: none;">

                                <button id="smtpSettingsButtonSubmit" class="btn btn-primary"
                                        style="float: right; margin-right: 5px;"   type="button" data-i18n="save"></button>
                                <!--                                <button id="smtpSettingsButtonCancel" class="btn btn-default"
                                                                        style="float: right; margin-right: 5px;" type="button">Cancel</button>-->
                                <!--<button id="smtpSettingsTestButton" class="btn btn-success" style="float: right; margin-right: 5px;"  disabled  type="button">Test</button>-->


                                <c:choose>
                                    <c:when test="${mailProps.hostName != null }">
                                        <button id="smtpSettingsTestButton" class="btn btn-success" style="float: right; margin-right: 5px;"    type="button" data-i18n="test"></button>
                                    </c:when>
                                    <c:otherwise>
                                        <button id="smtpSettingsTestButton" class="btn btn-success" style="float: right; margin-right: 5px;"  disable  type="button" data-i18n="test"></button>
                                    </c:otherwise>
                                </c:choose>


                            </div>
                        </form>

                    </div>

                    <div class="tab-pane" id="mailTab">
                        <form id="configureMailContentForm" class="form-horizontal row-border"  accept-charset="utf-8"  autocomplete="off" action="/configureMailContent" method="POST">
                            <!--MAIL_SENDER_NAME-->
                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="sender_name"></label>
                                <div class="col-sm-9">
                                    <input name="senderName" id="senderName" type="text" class="form-control"
                                           data-parsley-trigger="change"
                                           data-parsley-required-message="Please enter a email header."
                                           data-parsley-errors-container="span#emailHeaderError"
                                           required value="${senderName}"> </input><span id="emailHeaderError"></span>
                                </div>

                            </div>
                            <!--/form-group-->
                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="email_body"></label>
                                <div class="col-sm-9">
                                    <input name="mailHeader" id="mailHeader" type="text" class="form-control"
                                           data-parsley-trigger="change"
                                           data-parsley-required-message="Please enter a email header."
                                           data-parsley-errors-container="span#emailHeaderError"
                                           required value="${mailHeader}"> </input><span id="emailHeaderError"></span>
                                </div>

                            </div>
                            <!--/form-group-->


                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="user_create_subj"></label>
                                <div class="col-sm-9">
                                    <input name="userCreateMailSubject" id="userCreateMailSubject" type="text" class="form-control"
                                           data-parsley-trigger="change"
                                           data-parsley-required-message="Please enter a email header."
                                           data-parsley-errors-container="span#emailHeaderError"
                                           required value="${userCreateMailSubject}"> </input><span id="emailHeaderError"></span>
                                </div>

                            </div>
                            <!--/form-group-->

                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="user_create_content"></label>
                                <div class="col-sm-9">
                                    <textarea name="userCreateMailContent" id="userCreateMailContent" style="height: 100px;" class="form-control"data-parsley-trigger="change"
                                              data-parsley-required-message="Please enter a email content."
                                              data-parsley-errors-container="span#emailContentError"
                                              required>${userCreateMailContent}</textarea>         

                                </div>
                            </div> 
                            <!--/form-group-->
                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="pass_change_subj"></label>
                                <div class="col-sm-9">
                                    <input name="passwordChangeMailSubject" id="passwordChangeMailSubject" type="text" class="form-control"
                                           data-parsley-trigger="change"
                                           data-parsley-required-message="Please enter a email header."
                                           data-parsley-errors-container="span#emailHeaderError"
                                           required value="${passwordChangeMailSubject}"> </input><span id="emailHeaderError"></span>
                                </div>

                            </div>
                            <!--/form-group-->
                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="pass_change_content"></label>
                                <div class="col-sm-9">
                                    <textarea name="passwordChangeMailContent" id="passwordChangeMailContent" style="height: 100px;" class="form-control"data-parsley-trigger="change"
                                              data-parsley-required-message="Please enter a email content for password change."
                                              data-parsley-errors-container="span#emailContentError"
                                              required >${passwordChangeMailContent}    
                                    </textarea> <span id="emailContentError"></span> <span id="importFromFileResponse"></span>
                                </div>
                            </div> 
                            <!--/form-group-->

                            <div class="bottom " style="background: none; border: none;">

                                <button id="mailConfigButtonSubmit" class="btn btn-primary"
                                        style="float: right; margin-right: 5px;"   type="button" data-i18n="save"></button>
                            </div>
                        </form>

                    </div>
                     
                    <c:if test="${checkAdmin}">
                    <div class="tab-pane" id="SSTab">
                        <form id="configureSecSettingsForm" class="form-horizontal row-border"  accept-charset="utf-8"  autocomplete="off" action="/configureSecSettings" method="POST">
                            <!--MAIL_SENDER_NAME-->
                            <div class="form-group">
                                <label class="col-sm-3 control-label" data-i18n="max_device_count"></label>
                                <div class="col-sm-9">
                                    <input name="deviceLimit" id="deviceLimit" type="number" class="form-control"
                                           data-parsley-trigger="change"
                                           data-parsley-required-message="Please enter max device count."
                                           data-parsley-errors-container="span#secSettHeaderError"
                                           required value="${deviceLimit}"> </input><span id="secSettHeaderError"></span>
                                </div>

                            </div>

                            <div class="bottom " style="background: none; border: none;">

                                <button id="secSettingsConfigButtonSubmit" class="btn btn-primary"
                                        style="float: right; margin-right: 5px;"   type="button" data-i18n="save"></button>
                            </div>
                        </form>

                    </div>
                    </c:if>

                </div>
                <!-- /tab-content -->

            </div>
            <!--/progressWizard-->
        </div>
        <!--/porlets-content-->
    </div>
    <!--/block-web-->
</div>
<!--/col-md-6-->
</div>
<!--/row-->

</div>
<!--/row-->
<script type="text/javascript">

    $('body').i18n();
    console.log('body6 -- > i18n()');

</script>