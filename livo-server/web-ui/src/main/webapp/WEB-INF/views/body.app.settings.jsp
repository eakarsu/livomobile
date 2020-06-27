<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script type="text/javascript" src="../js/jquery.multi-select.js"></script>

            <ul id="appSettingsNavs" class="nav nav-tabs">
                <li class="active"><a href="#pushNotifTab" id="tab1" data-toggle="tab">Push Notification</a></li>
                <li><a href="#iconSplashTab" id="tab2" data-toggle="tab" data-i18n="icons_splashes"></a></li>
                <li><a href="#deployListTab" id="tab3" data-toggle="tab" data-i18n="deploy_versions"></a></li>
                <li><a href="#authTab" id="tab4" data-toggle="tab" data-i18n="authorization"></a></li>
                <li><a href="#relationTab" id="tab5" data-toggle="tab" data-i18n="relations"></a></li>
            </ul>
          <div class="tab-content">
            <div class="tab-pane fade in active" id="pushNotifTab">
                <form id="androidPushNotificationForm" class="form-horizontal row-border"
                      action="/apps/notifications/pushNotification/android" method="post" enctype="multipart/form-data" accept-charset='ISO-8859-1' data-parsley-validate >
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel">ANDROID</h4>
                    </div>
                    <div class="modal-body">

                        <div class="form-group">
                            <div class="col-sm-12">
                                <input name="appName" type="hidden" class="form-control" value="${applicationId}"></input>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="code"></label>
                            <div class="col-sm-9">
                                <input name="apiKey" type="text" class="form-control"
                                       data-parsley-trigger="change"
                                       data-parsley-required-message="Please enter a api key for the application"
                                       data-parsley-errors-container="span#applicationApiKeyError"
                                       required> <span id="applicationApiKeyError"></span>
                            </div>
                        </div>
                        <div id="applicationUpload" class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="gcm_service_file"></label>
                            <div class="col-sm-9">

                                <input id="androidPushNotificationUploadFile" type="file" name="androidPushNotificationUploadFile" accept=".json"
                                       data-parsley-required-message="Please select an google-services.json file"
                                       data-parsley-errors-container="span#pushNotificationUploadError" style="width:250px;"><span id="pushNotificationUploadError"></span>

                                    <!--                                    <div class="progress progress-striped active" align="center">
                                                                            <div id="probar" style="width:0%" aria-valuemax="100" aria-valuemin="0" aria-valuenow="60" role="progressbar" class="progress-bar progress-bar-primary"> 
                                                                                <p id="progressState"></p> </div>
                                                                        </div>-->

                            </div>

                        </div>



                    </div>


                    <div class="modal-footer">
                        <!--<button id="CloseForm" type="button" class="btn btn-default" data-dismiss="modal">Close</button>-->
                        <button id="SubmitForm" type="submit" class="btn btn-primary" data-i18n="save"></button>
                    </div>
                </form>

                <form id="applePushNotificationForm" class="form-horizontal row-border"
                      action="/apps/notifications/pushNotification/ios" method="post" enctype="multipart/form-data" accept-charset='ISO-8859-1' data-parsley-validate >
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel">iOS</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <div class="col-sm-12">
                                <input name="appName" type="hidden" class="form-control" value="${applicationId}"></input>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="env"></label>
                            <div class="col-sm-9">
                                <select id="environmentSelect"  class="form-control">
                                    <option value="0" data-i18n="select_env"></option>
                                    <option value="sandbox">Sandbox</option>
                                    <option value="production" data-i18n="production"></option>
                                </select>
                            </div>
                        </div>

                        <div id="applicationCertificateUpload" class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="certificate"></label>
                            <div class="col-sm-9">

                                <input id="pushNotificationCertificateUploadFile" type="file" name="pushNotificationCertificateUploadFile" accept=".zip"
                                       data-parsley-required-message="Please select an certificate file"
                                       data-parsley-errors-container="span#pushNotificationUploadError" style="width:250px;"><span id="pushNotificationUploadError"></span>

                                    <!--                                    <div class="progress progress-striped active" align="center">
                                                                            <div id="probar" style="width:0%" aria-valuemax="100" aria-valuemin="0" aria-valuenow="60" role="progressbar" class="progress-bar progress-bar-primary"> 
                                                                                <p id="progressState"></p> </div>
                                                                        </div>-->

                            </div>

                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="pass"></label>
                            <div class="col-sm-9">
                                <input name="password" type="Password" class="form-control"
                                       data-parsley-trigger="change"
                                       data-parsley-required-message="Please enter a password for the application"
                                       data-parsley-errors-container="span#applicationPasswordError"
                                       required> <span id="applicationPasswordError"></span>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <!--<button id="CloseForm" type="button" class="btn btn-default" data-dismiss="modal">Close</button>-->
                        <button id="SubmitForm" type="submit" class="btn btn-primary" data-i18n="save"></button>
                    </div>
                </form>
            </div>
            <div class="tab-pane fade" id="iconSplashTab">
                
                <form id="androidIconSplashForm" class="form-horizontal row-border"
                      action="/apps/iconsplashes/assets/android" method="post" enctype="multipart/form-data" accept-charset='ISO-8859-1' data-parsley-validate >
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel">ANDROID</h4>
                    </div>
                    <div class="modal-body">

                        <div class="form-group">
                            <div class="col-sm-12">
                                <input name="appName" type="hidden" class="form-control" value="${applicationId}"></input>
                            </div>
                        </div>
                        <div id="appIcon32x32Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(32x32) </label>
                            <div class="col-sm-9">
                                <input id="androidAppIcon32x32" type="file" name="androidIcon32x32" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(32x32)"
                                        data-parsley-errors-container="span#androidIconError" style="width: 250px;"><span id="androidIconError"></span>
                                <span id="androidAppIcon32x32_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon48x48Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(48x48) </label>
                            <div class="col-sm-9">
                                <input id="androidAppIcon48x48" type="file" name="androidIcon48x48" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(48x48)"
                                       data-parsley-errors-container="span#androidIconError" style="width:250px;"><span id="androidIconError"></span>
                                <span id="androidAppIcon48x48_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon72x72Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(72x72) </label>
                            <div class="col-sm-9">
                                <input id="androidAppIcon72x72" type="file" name="androidIcon72x72" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(72x72)"
                                       data-parsley-errors-container="span#androidIconError" style="width:250px;"><span id="androidIconError"></span>
                                <span id="androidAppIcon72x72_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon96x96Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(96x96) </label>
                            <div class="col-sm-9">
                                <input id="androidAppIcon96x96" type="file" name="androidIcon96x96" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(96x96)"
                                       data-parsley-errors-container="span#androidIconError" style="width:250px;"><span id="androidIconError"></span>
                                <span id="androidAppIcon96x96_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash800x480Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(800x480) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash800x480" type="file" name="androidSplash800x480" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(800x480)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash800x480_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash320x200Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(320x200) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash320x200" type="file" name="androidSplash320x200" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(320x200)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash320x200_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash480x320Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(480x320) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash480x320" type="file" name="androidSplash480x320" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(480x320)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash480x320_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash1280x720Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(1280x720) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash1280x720" type="file" name="androidSplash1280x720" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(1280x720)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash1280x720_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash480x800Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(480x800) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash480x800" type="file" name="androidSplash480x800" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(480x800)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash480x800_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash200x320Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(200x320) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash200x320" type="file" name="androidSplash200x320" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(200x320)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash200x320_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash320x480Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(320x480) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash320x480" type="file" name="androidSplash320x480" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(320x480)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash320x480_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash720x1280Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(720x1280) </label>
                            <div class="col-sm-9">
                                <input id="androidSplash720x1280" type="file" name="androidSplash720x1280" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(720x1280)"
                                       data-parsley-errors-container="span#androidSplashError" style="width:250px;"><span id="androidSplashError"></span>
                                <span id="androidSplash720x1280_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>

                    </div>


                    <div class="modal-footer">
                        <!--<button id="CloseForm" type="button" class="btn btn-default" data-dismiss="modal">Close</button>-->
                        <button id="SubmitForm" type="submit" class="btn btn-primary" data-i18n="save"></button>
                    </div>
                </form>

                <form id="appleIconSplashForm" class="form-horizontal row-border"
                      action="/apps/iconsplashes/assets/ios" method="post" enctype="multipart/form-data" accept-charset='ISO-8859-1' data-parsley-validate >
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel">iOS</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <div class="col-sm-12">
                                <input name="appName" type="hidden" class="form-control" value="${applicationId}"></input>
                            </div>
                        </div>

                        <div id="appIcon29x29Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(29x29) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon29x29" type="file" name="iOSIcon29x29" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(29x29)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon29x29_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon29x29_2xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(29x29@2x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon29x29_2x" type="file" name="iOSIcon29x29_2x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(29x29@2x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon29x29_2x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon29x29_3xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(29x29@3x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon29x29_3x" type="file" name="iOSIcon29x29_3x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(29x29@3x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon29x29_3x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon40x40Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(40x40) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon40x40" type="file" name="iOSIcon40x40" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(40x40)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon40x40_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon40x40_2xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(40x40@2x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon40x40_2x" type="file" name="iOSIcon40x40_2x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(40x40@2x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon40x40_2x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon40x40_3xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(40x40@3x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon40x40_3x" type="file" name="iOSIcon40x40_3x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(40x40@3x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon40x40_3x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon60x60Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(60x60) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon60x60" type="file" name="iOSIcon60x60" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(60x60)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon60x60_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon60x60_2xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(60x60@2x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon60x60_2x" type="file" name="iOSIcon60x60_2x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(60x60@2x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon60x60_2x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon60x60_3xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(60x60@3x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon60x60_3x" type="file" name="iOSIcon60x60_3x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(60x60@3x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon60x60_3x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon76x76Upload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(76x76) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon76x76" type="file" name="iOSIcon76x76" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(76x76)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon76x76_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon76x76_2xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(76x76@2x) </label>
                            <div class="col-sm-9">
                                <input id="vAppIcon76x76_2x" type="file" name="iOSIcon76x76_2x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(76x76@2x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="vAppIcon76x76_2x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appIcon76x76_3xUpload" class="form-group">
                            <label class="col-sm-3 control-label">AppIcon(76x76@3x) </label>
                            <div class="col-sm-9">
                                <input id="iOSAppIcon76x76_3x" type="file" name="iOSIcon76x76_3x" accept="image/*"
                                        data-parsley-required-message="Please select an AppIcon(76x76@3x)"
                                        data-parsley-errors-container="span#iOSIconError" style="width:250px;"><span id="iOSIconError"></span>
                                <span id="iOSAppIcon76x76_3x_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash640x960Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(640x960) </label>
                            <div class="col-sm-9">
                                <input id="iOSSplash640x960" type="file" name="iOSSplash640x960" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(640x960)"
                                       data-parsley-errors-container="span#iOSSplashError" style="width:250px;"><span id="iOSSplashError"></span>
                                <span id="iOSSplash640x960_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash640x1136Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(640x1136) </label>
                            <div class="col-sm-9">
                                <input id="iOSSplash640x1136" type="file" name="iOSSplash640x1136" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(640x1136)"
                                       data-parsley-errors-container="span#iOSSplashError" style="width:250px;"><span id="iOSSplashError"></span>
                                <span id="iOSSplash640x1136_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash768x1024Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(768x1024) </label>
                            <div class="col-sm-9">
                                <input id="iOSSplash768x1024" type="file" name="iOSSplash768x1024" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(768x1024)"
                                       data-parsley-errors-container="span#iOSSplashError" style="width:250px;"><span id="iOSSplashError"></span>
                                <span id="iOSSplash768x1024_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash1024x768Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(1024x768) </label>
                            <div class="col-sm-9">
                                <input id="iOSSplash1024x768" type="file" name="iOSSplash1024x768" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(1024x768)"
                                       data-parsley-errors-container="span#iOSSplashError" style="width:250px;"><span id="iOSSplashError"></span>
                                <span id="iOSSplash1024x768_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash1536x2048Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(1536x2048) </label>
                            <div class="col-sm-9">
                                <input id="iOSSplash1536x2048" type="file" name="iOSSplash1536x2048" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(1536x2048)"
                                       data-parsley-errors-container="span#iOSSplashError" style="width:250px;"><span id="iOSSplashError"></span>
                                <span id="iOSSplash1536x2048_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>
                        <div id="appSplash2048x1536Upload" class="form-group">
                            <label class="col-sm-3 control-label">Splash(2048x1536) </label>
                            <div class="col-sm-9">
                                <input id="iOSSplash2048x1536" type="file" name="iOSSplash2048x1536" accept="image/*"
                                        data-parsley-required-message="Please select an Splash(2048x1536)"
                                       data-parsley-errors-container="span#iOSSplashError" style="width:250px;"><span id="iOSSplashError"></span>
                                <span id="iOSSplash2048x1536_text" style="position: absolute; color: #0063DC; top: 0px; left: 280px;"></span>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <!--<button id="CloseForm" type="button" class="btn btn-default" data-dismiss="modal">Close</button>-->
                        <button id="SubmitForm" type="submit" class="btn btn-primary" data-i18n="save"></button>
                    </div>
                </form>

            </div>
                            
            <div class="tab-pane fade" id="deployListTab">
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
                                                    <th class=" sorting" role="columnheader" tabindex="0"
                                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                                        aria-sort="descending"
                                                        aria-label="Activate to sort column ascending"
                                                        style="width: 179px;" data-i18n="deploy_id"></th>
                                                     <th class=" sorting" role="columnheader" tabindex="0"
                                                        aria-controls="dynamic-table" rowspan="1" colspan="1"
                                                        aria-sort="descending"
                                                        aria-label="Activate to sort column ascending"
                                                        style="width: 179px;" data-i18n="deliverer"></th>
                                                    <th class=" sorting" role="columnheader" tabindex="0"
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
                                                                <td><input style="width: 20px; height: 20px; border:1px red solid;" id="is_active${deploy.deployId}" name="is_active${deploy.deployId}" type="checkbox" ${deploy.isActive ? 'checked' : ''}></td>
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
            </div>
                            
            <div class="tab-pane fade" id="authTab">
                <form id="authorizationApplicationForm" class="form-horizontal row-border"
                      action="#" method="post" data-parsley-validate >
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel"><strong data-i18n="authorization"></strong></h4>
                    </div>
                    <div class="modal-body">

                        <div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="authorized_source"></label>
                            <div class="col-sm-9">
                                <select id="authorizedUserGroupSelect" class="form-control">
                                    <option value="0" data-i18n="select_source"></option>
                                    <option value="any"${selectedSource.equals("any") ? " selected" : ""} data-i18n="any"></option>
                                    <option value="company"${selectedSource.equals("company") ? " selected" : ""} data-i18n="only_company"></option>
                                    <option value="System"${selectedSource.equals("System") ? " selected" : ""} data-i18n="system_groups"></option>
                                    <option value="ldap"${selectedSource.equals("ldap") ? " selected" : ""}>LDAP</option>
                                    <option value="System_withcomp"${selectedSource.equals("System_withcomp") ? " selected" : ""} data-i18n="system_with_company"></option>
                                    <option value="ldap_withcomp"${selectedSource.equals("ldap_withcomp") ? " selected" : ""} data-i18n="ldap_with_company"></option>
                                </select>
                                <br/>
                                <select multiple="multiple" id="groupList" name="groupList" class="group_list">
                                    <c:forEach  var="group" items="${groups}">
                                        <option value="${group.domain}.${group.name}" ${group.selected}>${group.name}</option>
                                    </c:forEach>
                                </select>
                                <select multiple="multiple" id="ldapList" name="ldapList" class="ldap_list">
                                    <c:forEach  var="ldapConfiguration" items="${ldapList}">
                                        <option value="${ldapConfiguration.fullName}">${ldapConfiguration.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <!--<button id="CloseForm" type="button" class="btn btn-default" data-dismiss="modal">Close</button>-->
                        <button id="authApplicationSubmitForm" type="button" class="btn btn-primary" data-i18n="save"></button>
                    </div>
                </form>
                                
            </div>
                            
            <div class="tab-pane fade" id="relationTab">
                <form id="relationsForm" class="form-horizontal row-border"
                      action="#" method="post" data-parsley-validate >
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel"><strong data-i18n="relations"></strong></h4>
                    </div>
                    <div class="modal-body">
                                
                        <div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="app_owner"></label>
                            <div class="col-sm-9">
                                <select id="authorizedOwner" name="authorizedOwner" class="form-control">
                                    <c:forEach  var="developer" items="${developers}">
                                        <option value="${developer.userId}" <c:if test="${developer.userName == owner}">selected</c:if>>${developer.userName}</option>
                                    </c:forEach>
                                </select>
                                <br/>
                            </div>
                        </div>
                                
                        <div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="companies"></label>
                            <div class="col-sm-9">
                                <select multiple="multiple" id="companyList" name="companyList">
                                    <c:forEach  var="company" items="${companies}">
                                        <option value="${company.companyId}" ${company.selected}>${company.companyId}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                                
                        <!--<div class="form-group">
                            <label class="col-sm-3 control-label" data-i18n="max_device_count"></label>
                            <div class="col-sm-9">
                                <input name="deviceLimit" id="deviceLimit" type="number" class="form-control"
                                           data-parsley-trigger="change"
                                           data-parsley-required-message="Please enter max device count."
                                           data-parsley-errors-container="span#deviceLimitError"
                                           required value="${deviceLimit}"> </input><span id="deviceLimitError"></span>
                            </div>
                        </div>-->

                    </div>
                    <div class="modal-footer">
                        <!--<button id="CloseForm" type="button" class="btn btn-default" data-dismiss="modal">Close</button>-->
                        <button id="relationSubmitForm" type="button" class="btn btn-primary" data-i18n="save"></button>
                    </div>
                </form>
                                
            </div>

        </div>

<script type="text/javascript" src="../js/list_data.js"></script>
<script src="../js/pages/deployments.js"></script>
<script type="text/javascript">

    $('body').i18n();
    var selectedSource = "${selectedSource}";
    $('.activateDeployment').val($.i18n( 'activate_deployment' ));
    $('.deleteDeployment').val($.i18n( 'delete' ));
    if (selectedSource === "ldap" || selectedSource === "ldap_withcomp") {
        // Groups Combobox add ldap connections groups
        $('#ldapList').css("display", "inline-block");
        $('#ms-ldapList').css("display", "inline-block");
        $('#groupList').css("display", "none");
        $('#ms-groupList').css("display", "none");

    } else if (selectedSource === "System" || selectedSource === "System_withcomp") {
        // Groups Combobox add system  groups    
        $('#groupList').css("display", "inline-block");
        $('#ms-groupList').css("display", "inline-block");
        $('#ldapList').css("display", "none");
        $('#ms-ldapList').css("display", "none");

    }
    
    loadDeploymentTableDynamics();
        
    loadDeploymentActions();
       
    buttonGroupFadeOut(500);
    var main_list = $("#applicationSettings_link");
    
    <c:forEach items="${iconsSplashesList}" var="iconsSplashes">
        <c:choose>
            <c:when test="${iconsSplashes.imgId == 'androidIcon32x32'}">$("#androidAppIcon32x32_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidIcon48x48'}">$("#androidAppIcon48x48_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidIcon72x72'}">$("#androidAppIcon72x72_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidIcon96x96'}">$("#androidAppIcon96x96_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash800x480'}">$("#androidSplash800x480_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash320x200'}">$("#androidSplash320x200_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash480x320'}">$("#androidSplash480x320_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash1280x720'}">$("#androidSplash1280x720_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash480x800'}">$("#androidSplash480x800_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash200x320'}">$("#androidSplash200x320_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash320x480'}">$("#androidSplash320x480_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'androidSplash720x1280'}">$("#androidSplash720x1280_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon29x29'}">$("#iOSIcon29x29_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon29x29_2x'}">$("#iOSIcon29x29_2x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon29x29_3x'}">$("#iOSIcon29x29_3x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon40x40'}">$("#iOSIcon40x40_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon40x40_2x'}">$("#iOSIcon40x40_2x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon40x40_3x'}">$("#iOSIcon40x40_3x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon60x60'}">$("#iOSIcon60x60_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon60x60_2x'}">$("#iOSIcon60x60_2x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon60x60_3x'}">$("#iOSIcon60x60_3x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon76x76'}">$("#iOSIcon76x76_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon76x76_2x'}">$("#iOSIcon76x76_2x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSIcon76x76_3x'}">$("#iOSIcon76x76_3x_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSSplash640x960'}">$("#iOSSplash640x960_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSSplash640x1136'}">$("#iOSSplash640x1136_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSSplash768x1024'}">$("#iOSSplash768x1024_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSSplash1024x768'}">$("#iOSSplash1024x768_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSSplash1536x2048'}">$("#iOSSplash1536x2048_text").text("${iconsSplashes.fileName}");</c:when>
            <c:when test="${iconsSplashes.imgId == 'iOSSplash2048x1536'}">$("#iOSSplash2048x1536_text").text("${iconsSplashes.fileName}");</c:when>
            <c:otherwise>

            </c:otherwise>

        </c:choose>
    </c:forEach>

</script>