/*** Javascript codes of application settings page. ***/

//processing file creation

var pfBar = document.getElementById("probarfile");

var pfState = document.getElementById("progressStateFile");

$("form#fileUploading").ajaxForm({
    beforeSend: function (xhr, opts) {

        if (!$("form#fileUploading").find("input[name='applicationFile']").val())
            xhr.abort();

        else {

            $("#dontUpload").prop("disabled", true);

            $("#upLoadFile").prop("disabled", true);
        }

    },
    uploadProgress: function (event, position, total, percentComplete) {
        var percentVal = percentComplete + '%';
        var style = "width:" + percentVal + ";";
        pfBar.setAttribute("style", style);

        if (percentComplete == 100)
            pfState.innerHTML = $.i18n( 'saving_file' );

        else
            pfState.innerHTML = percentVal;

    },
    success: function (data) {

        $("#dontUpload").prop("disabled", false);

        $("#upLoadFile").prop("disabled", false);

        $("#sUploadFile").modal('hide');

        doTrick(targetElement);

        pfBar.setAttribute("style", "width: 0%;");

        pfState.innerHTML = "";
        $("form#fileUploading").find("input, textarea").val("");

    },
    error: function (response) {

        $("#dontUpload").prop("disabled", false);

        $("#upLoadFile").prop("disabled", false);

        pfBar.setAttribute("style", "width: 0%;");

        pfState.innerHTML = "";

        $("#fileUploadError").fadeIn(300, function () {

            document.getElementById("fileUploadError").innerHTML = response.responseText;

        });
    }

});

$("form#fileCreation").ajaxForm({
    beforeSend: function (xhr, opts) {

        if (!$("#inputFileName").val())
            xhr.abort();

        else {

            $("#closeFileCreate").prop("disabled", true);

            $("#saveFileCreate").prop("disabled", true);
        }

    },
    success: function (data) {

        $("#closeFileCreate").prop("disabled", false);

        $("#saveFileCreate").prop("disabled", false);

        $("#sCreateFile").modal('hide');

        doTrick(targetElement);
        
        $("form#fileCreation").find("input, textarea").val("");

    },
    error: function (response) {

        document.getElementById("fileNameError").innerHTML = response.responseText;

        $("#closeFileCreate").prop("disabled", false);

        $("#saveFileCreate").prop("disabled", false);
    }


});

$("form#folderCreation").ajaxForm({
    beforeSend: function (xhr, opts) {

        if (!$("#inputFolderName").val())
            xhr.abort();

        else {

            $("#closeFolderCreate").prop("disabled", true);

            $("#saveFolderCreate").prop("disabled", true);
        }

    },
    success: function (data) {

        $("#closeFolderCreate").prop("disabled", false);

        $("#saveFolderCreate").prop("disabled", false);

        $("#sCreateFolder").modal('hide');

        doTrick(targetElement);

        $("form#folderCreation").find("input, textarea").val("");

    },
    error: function (response) {

        document.getElementById("folderNameError").innerHTML = response.responseText;

        $("#closeFolderCreate").prop("disabled", false);

        $("#saveFolderCreate").prop("disabled", false);
    }


});

$("form#fileImportingForm").ajaxForm({
    uploadProgress: function (event, position, total, percentComplete) {
        var percentVal = percentComplete + '%';
        var style = "width:" + percentVal + ";";
        pBar.setAttribute("style", style);

        if (percentComplete == 100)
            pState.innerHTML = $.i18n( 'processing_file' );
        else
            pState.innerHTML = percentVal;

    },
    success: function (response) {
//          alert(response);
//          document.getElementById("importFromFileResponse").innerHTML = response;
        $("#sImportNewUserFromFile").modal("hide");

        $("#successModal").modal("show");
        $("#idUsersList").click();
        document.getElementById("alertText").innerHTML = response;
        $("form#fileImportingForm").find("input, textarea").val("");

    },
    error: function (response) {
        document.getElementById("importFromFileResponse").innerHTML = response.responseText;

        pBar.setAttribute("style", "width: 0%;");

        pState.innerHTML = "";

    }
});
// End of process code


/*** Changing the name of the application file ***/
$("#renameFileButton").click(function (event) {
    var filePath = $("#inputRenameFilePath").val();
    var newFileName = $("#newFileName").val();
    var fileType = $("#inputRenameFileType").val();
    var appId = $("#inputRenameFileAppId").val();

    $.ajax({
        url: "/rename/appFile",
        type: "POST",
        data: {filePath: filePath, fileType: fileType, appId: appId, newFileName: newFileName},
        success: function () {
            $("#sRenameFileModal").modal("hide");

            loadContent("/apps/", true, appId);

        },
        error: function (response) {

            $("#ErrorModal").modal("show");

            document.getElementById("errorText").innerHTML = response.responseText;

        }

    });

});


$("#downloadApplication").click(function (event) {

    var lastWordOfApp = "Are you sure to download the application with name: " + appIdHolder + " ?";

    document.getElementById("downloadApplicationText").innerHTML = lastWordOfApp;

    $("#downloadApplicationModal").modal('show');
});

$("#downloadAppButton").click(function (event) {

    if (appIdHolder != $("#downloadApplication.btn-group").data("application-id")) {

        alert("Why are you trying dirty things????");
    }
    else {
        $("#downloadApplicationModal").modal('hide');
        setTimeout(function () {
            $(".page-content").load("/apps/download", {applicationId: appIdHolder}, function (responseText, textStatus, jqXHR) {
            });
        }, 800);
    }
});

$("#deleteApplication.btn-group").click(function (event) {

    var lastWordOfApp = $.i18n( 'sure2delete_app' ) + appIdHolder + " ?";

    document.getElementById("deleteAppText").innerHTML = lastWordOfApp;

    $("#deleteAppModal").modal('show');

});
/*** Delete Application processes ***/
$("#deleteAppButton").click(function (event) {

    if (appIdHolder != $("#deployment.btn-group").data("application-id")) {

        alert("Why are you trying dirty things????");
    }

    else {

        $("#deleteAppModal").modal('hide');
        setTimeout(function () {
            $("#sidebarContainer").load("/apps/" + appIdHolder + "/delete", {"": ""}, function (responseText, textStatus, jqXHR) {

                $(".page-content").load("/bodyWelcome", {"": ""}, function () {

//					$(reloader());
                    location.reload();
                });

            });
        }, 800);


    }

});

//Processing application creation
var pBar = document.getElementById("probar"); //div element

var pState = document.getElementById("progressState");//<P> element

var loadMessage = $.i18n( 'loading' ), loadingInd = 1;
var loadingManager = function (loading){
  
    $("div.loading-overlay-content")[0].innerHTML = loadMessage.substr(0,loadingInd);
    loadingInd++;
    console.log('2 --> ' + loadingInd);
    setTimeout(function () {
        console.log('3 --> ' + loadMessage.length + ' -- ' + loadingInd);
        if(loadingInd !== -1 && loadMessage.length >= loadingInd){
            loadingManager(loading);
        }
        else
        if(loadingInd !== -1){
            loadingInd = loadMessage.length - 3;
            loadingManager(loading);
        }
    }, 1000);
    
};

$("form#createApplication").ajaxForm({
    beforeSend: function (xhr, opts) {
        var currForm = $("form#createApplication");
        console.log('createApplication::ajaxForm --> ' + currForm.find("input[name='name']").val() + ' -- ' + 
              currForm.find("select[name='appCompanyList']").val() + ' -- ' + 
              currForm.find("textarea[name='description']").val() + ' -- ' + 
              currForm.find("select[name='templateName']").val());
        if (!(currForm.find("input[name='name']").val() && 
              currForm.find("select[name='appCompanyList']").val() &&
              currForm.find("textarea[name='description']").val() &&
              currForm.find("select[name='templateName']").val()))
            xhr.abort();
        else {

            $('#createApplicationModal').on('loading.start', function(event, loadingObj) {
                console.log(loadingObj);
                loadingManager(loadingObj);
            });
            
            $('#createApplicationModal').loading('start');

            if ($("div#applicationUpload").is(":visible")) {

                if ($("input#applicationUpload").val()) {

                    $("#CloseForm").prop("disabled", true);

                    $("#SubmitForm").prop("disabled", true);

                } else {

                    xhr.abort();
                }
            }
        }
        $("#applicationUploadError").fadeIn(200, function () {

            document.getElementById("applicationUploadError").innerHTML = "";

        });

    },
    uploadProgress: function (event, position, total, percentComplete) {
        var percentVal = percentComplete + '%';
        var style = "width:" + percentVal + ";";
        pBar.setAttribute("style", style);

        if (percentComplete == 100)
            pState.innerHTML = "Processing App...";

        else
            pState.innerHTML = percentVal;

    },
    success: function (data) {
        var appId = $("form#createApplication").find("input[name='name']").val();
        $("#createApplicationModal").modal('hide');
        console.log('createApplication:appId -- > ' + data + ' -- ' + appId);

        $("#CloseForm").prop("disabled", false);

        $("#SubmitForm").prop("disabled", false);
        
        $("form#createApplication").find("input, textarea").val("");
        
        loadingInd = -1;
        $('#createApplicationModal').loading('stop');

        setTimeout(function () {
            $("#sidebarContainer").load("/loadSideBar", {"": ""}, function (responseText, textStatus, jqXHR) {

                /*$(".page-content").load("/bodyWelcome", {"": ""}, function () {
                    //location.reload();

                });

                $(".page-content").load("/apps/" + appId, {"": ""}, function () {
                    appIdHolder = appId;
            });
                $(".dcjq-parent-li.app #app").click(function (event) {

                    event.preventDefault();
                    
                });*/
                console.log('createApplication::setTimeout:appId -- > ' + appId);
                buttonGroupFadeIn(500);
                //give id of the app to hold for later use
                //$("#savedStates.btn-group").data("application-id", appId);

                $("#deployment.btn-group").data("application-id", appId);

                $("#deleteApplication.btn-group").data("application-id", appId);

                $("#preview.btn-group").data("application-id", appId);

                $("#applicationSettings.btn-group").data("application-id", appId);

                $("#downloadApplication.btn-group").data("application-id", appId);

                //save current app id to global holder
                appIdHolder = appId;

                loadContent("/apps/", true, appId, true);
                
                $.getScript( "/js/pages/application.js" )
                    .done(function( script, textStatus ) {
                        console.log( "/js/pages/application.js --> " + textStatus );
                    })
                    .fail(function( jqxhr, settings, exception ) {
                      console.log( "/js/pages/application.js::exception --> " + exception );
                });
                //loadApplicationSavedStates(appId);
                //$("li[data-application-id='" + appId + "']").trigger('click');

            });
        }, 1800);
        //                             location.reload();

    },
    error: function (response) {

        $("#CloseForm").prop("disabled", false);

        $("#SubmitForm").prop("disabled", false);

        pBar.setAttribute("style", "width: 0%;");

        pState.innerHTML = "";
        $("#createApplicationModal").modal('hide');
        $("#ErrorModal").modal("show");

        document.getElementById("errorText").innerHTML = response.responseText;
        
        loadingInd = -1;
        $('#createApplicationModal').loading('stop');

        $("#applicationUploadError").fadeIn(300, function () {

            document.getElementById("applicationUploadError").innerHTML = response.responseText;

        });
    }
});
// End of process code

//Deploy application method
deployApplication = function (appId) {
    var p = document.getElementById("repeater");
    var loading = $('<div class="loading"><i class="fa fa-refresh fa-spin"></i></div>');
    $.ajax({
        url: "/deployApplication",
        type: "POST",
        data: {id: appId},
        beforeSend: function () {

            loading.appendTo(p);
            loading.fadeIn();
            $("div.sidebarContainer").css("display", "block");
            $("div.changeUserAttributesModal").css("display", "block");
        },
        success: function (response) {
            $("div.sidebarContainer").css("display", "none");
            $("div.changeUserAttributesModal").css("display", "none");

            $("#successModal").modal("show");
            document.getElementById("alertText").innerHTML = response;

            setTimeout(function () {
                loading.fadeOut();
            }, 1000);
        },
        error: function (response) {
            $("div.sidebarContainer").css("display", "none");
            $("div.changeUserAttributesModal").css("display", "none");
            loading.fadeOut();

            $("#ErrorModal").modal('show');

            document.getElementById("errorText").innerHTML = response.responseText;
        }

    });

};

//Save control actions by omur
$("#ignoreControlButton").click(function (event) { //1. ignore
    event.preventDefault();

    $("#saveControlModal").modal("hide");
    fileChanged = {'action':false,'path':''};
});
$("#saveControlButton").click(function (event) { //2. save
    event.preventDefault();

    $("#saveControlModal").modal("hide");
    $("#rawScreenEditor").submit();
    fileChanged = {'action':false,'path':''};
});
$("#saveDeployControlButton").click(function (event) { //3. save and deploy
    event.preventDefault();

    $("#saveControlModal").modal("hide");
    saveAndDeploy();
});

$("#editorSubmitAndDeploy").click(function () {

    saveAndDeploy();

    return false;
});

//source code saved in editor 
saveEditor = function () {
    var form = $("#rawScreenEditor");
    form.submit();
};

//source code saved in editor and deploy application
saveAndDeploy = function () {
    saveEditor();
    deployApplication($("#deployment.btn-group").data("application-id"));
    fileChanged = {'action':false,'path':''};
};

//application show preview actions
showPreview = function (arg) {

    $("div#fileContent").load("/show/preview/application", {"appId": $("#deployment.btn-group").data("application-id"), "currentFile": arg}, function () {
        console.log('arg ---> ' + arg);
        if (arg === "") {

            document.getElementById("displayFileName").innerHTML = $.i18n( 'application_x_preview' , $("#deployment.btn-group").data("application-id"));

        } else {

            var fileName = currentFile.split("/");
            document.getElementById("displayFileName").innerHTML = $.i18n( 'server_address' ) + " - " + fileName[fileName.length - 1];

        }


        $("#changeOrientation").click(function (e) {
            if ($("#previewDevice").hasClass("landscape")) {

                $("#previewDevice").removeClass("landscape");

            }
            else
                $("#previewDevice").addClass("landscape");



        });

        $("#nexusPre").click(function () {

            if ($("#previewDevice").hasClass("landscape"))
                document.getElementById("previewDevice").setAttribute("class", "marvel-device nexus5 landscape");
            else
                document.getElementById("previewDevice").setAttribute("class", "marvel-device nexus5");


        });

        $("#iphonePre").click(function () {

            if ($("#previewDevice").hasClass("landscape"))
                document.getElementById("previewDevice").setAttribute("class", "marvel-device iphone5s black landscape");
            else
                document.getElementById("previewDevice").setAttribute("class", "marvel-device iphone5s black");


        });

        $("#tabletPre").click(function () {

            if ($("#previewDevice").hasClass("landscape"))
                document.getElementById("previewDevice").setAttribute("class", "marvel-device ipad landscape");
            else
                document.getElementById("previewDevice").setAttribute("class", "marvel-device ipad");


        });


    });

};
// load application saved states -- Disable now 
loadApplicationSavedStates = function (applicationId) {

    $.ajax({
        url: "/apps/" + applicationId + "/savedStates",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            console.log('dataaaaaaaaaa');
            console.log(data);
            if (data.length == 0) {

                /*$("#savedStates #count.label").text("");
                $("#savedStates .title.alert").removeClass("alert-warning").addClass("alert-success");
                $("#savedStates #count:not(.label)").text("0");

                $("#savedStates ul li:not(.title):not(.prototype)").remove();*/

            } else {

                /*$("#savedStates #count.label").text("" + data.length);
                $("#savedStates .title.alert").removeClass("alert-success").addClass("alert-warning");

                $("#savedStates ul li:not(.title):not(.prototype)").remove();

                for (var state in data) {

                    var clonedItem = $("#savedStates li.prototype").clone();

                    clonedItem.children(".alert-content").children("#time").text(state.time);
                    clonedItem.children(".alert-content").children("#date").text(state.date);

                    clonedItem.children(".alert-time").text(state.summary);

                    clonedItem.removeClass("prototype");
                    clonedItem.removeAttr("style");

                    $("#savedStates ul").append(clonedItem);
                }

                $("#savedStates ul").append("<li></li>");*/
            }


        }
    });
};

deleteFile = function (el, app) {

    $.ajax({
        url: "/delete/appfile",
        type: "POST",
        data: {fileUrl: $(el).attr("rel"), appId: app},
        success: function () {

            $(el).parent().remove();
        },
        error: function (response) {

            $("#ErrorModal").modal("show");

            document.getElementById("errorText").innerHTML = response.responseText;

        }

    });

};

duplicateFile = function (el, app) {

    $.ajax({
        url: "/duplicate/appfile",
        type: "POST",
        data: {fileUrl: $(el).attr("rel"), appId: app},
        success: function () {

            loadContent("/apps/", true, app);

        },
        error: function (response) {

            $("#ErrorModal").modal("show");

            document.getElementById("errorText").innerHTML = response.responseText;

        }

    });

};

renameFile = function (el, app) {

    newFileName = "";
    $.ajax({
        url: "/rename/appFile",
        type: "POST",
        data: {fileUrl: $(el).attr("rel"), appId: app, newFileName: newFileName},
        success: function () {

            $(el).parent().remove();
        },
        error: function (response) {

            $("#ErrorModal").modal("show");

            document.getElementById("errorText").innerHTML = response.responseText;

        }

    });

};

cutFile = function (el, app) {
// Cut file status is true for file paste.
    cutFilePath = $(el).attr("rel");
    cutFileStatus = true;

};

pasteFile = function (el, app) {

    var targetFilePath = $(el).attr("rel");
    if (cutFilePath == "") {

        $("#ErrorModal").modal("show");
        document.getElementById("errorText").innerHTML = $.i18n( 'cut_file_path_empty' );
        return false;
    }
    console.log("filePath : " + cutFilePath + " targetFilePath  : " + targetFilePath + "    appId : " + app);

    $.ajax({
        url: "/move/appfile",
        type: "POST",
        data: {filePath: cutFilePath, targetFilePath: targetFilePath, appId: app},
        success: function () {
            loadContent("/apps/", true, app);
        },
        error: function (response) {

            $("#ErrorModal").modal("show");
            document.getElementById("errorText").innerHTML = response.responseText;
            cutFileStatus = false;
        }

    });

};


$("#sendPushNotificationButton").click(function () {

    var application = $("#applicationSelect option:selected").text();

    var title = $("#pushNotificationTitle").val();

    var message = $("#pushNotificationMessage").val();

    var messageType = $("#messageTypeSelect option:selected").text();


    if (application === "") {

        document.getElementById("errorText").innerHTML = $.i18n( 'all_fields_req' );
    } else if (title === "") {
        document.getElementById("pushTittleError").innerHTML = $.i18n( 'please_enter_title' );
    } else if (message === "") {
        document.getElementById("pushMessageContentError").innerHTML = $.i18n( 'please_enter_message' );
    } else if (messageType === "") {
        document.getElementById("pushTittleError").innerHTML = $.i18n( 'select_message_type' );
    }

    if (application === "" || title === "" || message === "" || messageType === "") {
        $("#sendPushNotificationModal").modal("hide");
        $("#ErrorModal").modal("show");
        document.getElementById("errorText").innerHTML = $.i18n( 'all_fields_req' );
        return false;
    }

    sendPushNotification(application, title, message);

    clearNotificationFormInputs();
});


function sendPushNotification(application, title, message, messageType) {
    event.preventDefault();
    var p = document.getElementById("repeater");
    var loading = $('<div class="loading"><i class="fa fa-refresh fa-spin"></i></div>');

    $.ajax({
        url: "/notification/sendNotification",
        type: "POST",
        data: {appName: application, pushNotificationTitle: title, pushNotificationMessage: message, pushNotificationMessageType: messageType},
        beforeSend: function () {
            loading.appendTo(p);
            loading.fadeIn();

        },
        success: function (response) {
            loading.fadeOut();
            $("#sendPushNotificationModal").modal("hide");
            $("#successModal").modal("show");
            document.getElementById("alertText").innerHTML = response;
        },
        error: function (response) {
            loading.fadeOut();
            $("#sendPushNotificationModal").modal("hide");
            $("#ErrorModal").modal("show");
            document.getElementById("errorText").innerHTML = response.responseText;
        }

    });
}


function clearNotificationFormInputs() {
    $("#applicationSelect").val(0)
    $("#messageTypeSelect").val(0)
    $("#pushNotificationTitle").val('');
    $("#pushNotificationMessage").val('');
}



$(document).on('click', '#test_preview', function (event) {
    saveEditor();

    previewCount++;
    previewSourceArray[previewCount] = editor.doc.getValue();

    showPreview(currentFile);
});

$(document).on('click', '#code_preview', function (event) {

    console.log('document.onclick ---> fileChanged: ' + fileChanged.action);

    loadFileContent("/file/content", currentFile);

});


doTrick = function (target) {

    var tf = "a";

    if ($(target).data("type") == "directory") {

        if ($(target).attr("id") == "treeRoot")
        {
            $('#testtree').fileTree({root: $(target).attr("rel"), script: '../tree/jqueryFileTree.jsp', multiFolder: false, expandSpeed: 750, collapseSpeed: 750}, function (file) {

                currentFile = file;
                loadFileContent("/file/content", file);
            });
        }
        else {

            var list = $(target).parent().attr("class").split(/\s+/);

            for (var i = 0; i < list.length; i++) {

                if (list[i] == "expanded")
                {
                    tf = "aa";
                }
            }

            if (tf === "aa") {

                $(target).click();

                setTimeout(function () {

                    $(target).click();

                }, 500);
            }

            else
                $(target).click();


        }
    }
};


function showShortcuts() {

    $("#Shortcuts").modal("show");

}