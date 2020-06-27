var editor;
var targetElement;
var appIdHolder = "";
var appFramework = "";
var currentFile = "";
var jqueryComponentIdList = ["icons", "buttons", "listviews", "checkboxWidget", "collapsible", "grids", "formElement", "loader", "tables", "popups", "toolbars", "tabs"];
var previewSourceArray;
var previewCount;

var cutFileStatus = false;
var cutFilePath = "";
var fileChanged = {'action':false,'path':''};
var allApps = null;

$(document).ready(function () {

    //HeartBeater
    $.ajax({
        url: "/heartbeater",
        type: "POST",
        data: "",
        processData: false,
        contentType: false,
        success: function (suc) {
        },
        error: function (response) {
        }
    });

    setInterval(function () {
        $.ajax({
            url: "/heartbeater",
            type: "POST",
            data: "",
            processData: false,
            contentType: false,
            success: function (suc) {
            },
            error: function (response) {
            }
        });

    }, 500000);

    /*==Slim Scroll ==*/
    if ($.fn.slimScroll) {
        $('.event-list').slimscroll({
            height: '305px',
            wheelStep: 20

        });
        $('.conversation-list').slimscroll({
            height: '360px',
            wheelStep: 35
        });
        $('.to-do-list').slimscroll({
            height: '300px',
            wheelStep: 35
        });
    }

    /*==Collapsible==*/
    $('.widget-head').click(function (e) {
        var widgetElem = $(this).children('.widget-collapse').children('i');

        $(this)
                .next('.widget-container')
                .slideToggle('slow');
        if ($(widgetElem).hasClass('ico-minus')) {
            $(widgetElem).removeClass('ico-minus');
            $(widgetElem).addClass('ico-plus');
        } else {
            $(widgetElem).removeClass('ico-plus');
            $(widgetElem).addClass('ico-minus');
        }
        e.preventDefault();
    });

    $('.sidebar-toggle-box').click(function (e) {

        $(".leftside-navigation").niceScroll({
            cursorcolor: "#959595",
            cursorborder: "0px solid #fff",
            cursorborderradius: "0px",
            cursorwidth: "10px"
        });

        $('#sidebar').toggleClass('hide-left-bar');
        if ($(window).width() < 750)
        {
            $('#sidebar').toggleClass('show-left-bar');
            $('#main-content').toggleClass('merge-right');
            $('#sidebar').toggleClass('hidden-xs');

        }
        if ($('#sidebar').hasClass('hide-left-bar')) {
            $(".leftside-navigation").getNiceScroll().hide();
        }
        $(".leftside-navigation").getNiceScroll().show();
        $('#main-content').toggleClass('merge-left');
        e.stopPropagation();


    });

    /*== to do list ==*/
    $('.task-finish').click(function () {
        if ($(this).is(':checked')) {
            $(this).parent().parent().addClass('selected');
        }
        else {
            $(this).parent().parent().removeClass('selected');
        }
    });

    /*==Delete to do list==*/
    $('.task-del').click(function () {
        var activeList = $(this).parent().parent();

        activeList.addClass('removed');

        setTimeout(function () {
            activeList.remove();
        }, 1000);

        return false;
    });

    /*==Porlets Actions==*/
    $('.minimize').click(function (e) {
        var h = $(this).parents(".header");
        var c = h.next('.porlets-content');
        var p = h.parent();

        c.slideToggle();

        p.toggleClass('closed');

        e.preventDefault();
    });

    $('.refresh').click(function (e) {
        var h = $(this).parents(".header");
        var p = h.parent();
        var loading = $('<div class="loading"><i class="fa fa-refresh fa-spin"></i></div>');

        loading.appendTo(p);
        loading.fadeIn();
        setTimeout(function () {
            loading.fadeOut();
        }, 1000);

        e.preventDefault();
    });

    $('.close-down').click(function (e) {
        var h = $(this).parents(".header");
        var p = h.parent();

        p.fadeOut(function () {
            $(this).remove();
        });
        e.preventDefault();
    });

    // tool tips
    $('.tooltips').tooltip();

    // popovers
    $('.popovers').popover();

    // icon tab
    $('#myTab a').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
        calculateHeight();
    });

    // custom bar chart
    if ($(".custom-bar-chart")) {
        $(".bar").each(function () {
            var i = $(this).find(".value").html();
            $(this).find(".value").html("");
            $(this).find(".value").animate({
                height: i
            }, 2000);
        });
    }

    /*==mailbox jquerys ==*/

    //Check
    jQuery('.ckbox input').click(function () {
        var t = jQuery(this);
        if (t.is(':checked')) {
            t.closest('tr').addClass('selected');
        } else {
            t.closest('tr').removeClass('selected');
        }
    });

    // Star
    jQuery('.star').click(function () {
        if (!jQuery(this).hasClass('star-checked')) {
            jQuery(this).addClass('star-checked');
        }
        else
            jQuery(this).removeClass('star-checked');
        return false;
    });

    // Read mail
    jQuery('.table-email .media').click(function () {
        location.href = "read.html";
    });

    //custom scripts

    $(document).contextmenu({
        delegate: ".hasmenu",
        preventContextMenuForPopup: true,
        preventSelect: true,
        taphold: false,
        menu: [
            {title: $.i18n( 'new_folder' ), cmd: "createFolder", uiIcon: "ui-icon-folder-collapsed", disabled: true},
            {title: $.i18n( 'new_file' ), cmd: "createFile", uiIcon: "ui-icon-document", disabled: true},
            {title: $.i18n( 'delete' ), cmd: "deleteFile", uiIcon: "ui-icon-trash", disabled: true},
            {title: $.i18n( 'duplicate' ), cmd: "duplicateFile", uiIcon: "ui-icon-copy", disabled: true},
            {title: $.i18n( 'rename' ), cmd: "renameFile", uiIcon: "ui-icon-refresh", disabled: true},
            {title: $.i18n( 'upload_file' ), cmd: "uploadFile", uiIcon: "ui-icon-arrowthickstop-1-n", disabled: true},
            {title: $.i18n( 'cut_file' ), cmd: "cutFile", uiIcon: "ui-icon-scissors", disabled: true},
            {title: $.i18n( 'paste' ), cmd: "pasteFile", uiIcon: "ui-icon-paste", disabled: true}
        ],
        // Handle menu selection to implement a fake-clipboard
        select: function (event, ui) {
            switch (ui.cmd) {
                case "createFolder":
                    $("#sCreateFolder").modal({backdrop: 'static'});
                    $("#inputFolderPath").val($(ui.target).attr("rel"));
                    $("#inputFolderType").val("dir");
                    $("#inputFolderAppId").val(appIdHolder);
                    targetElement = ui.target;
                    break;
                case "createFile":
                    $("#sCreateFile").modal({backdrop: 'static'});
                    $("#inputFilePath").val($(ui.target).attr("rel"));
                    $("#inputFileType").val("file");
                    $("#inputAppId").val(appIdHolder);
                    targetElement = ui.target;
                    break;
                case "uploadFile":

                    $("#sUploadFile").modal({backdrop: 'static'});
                    $("#inputUpFilePath").val($(ui.target).attr("rel"));
                    $("#inputUpAppId").val(appIdHolder);
                    targetElement = ui.target;
                    break;
                case "deleteFile":

                    deleteFile(ui.target, appIdHolder);

                    break;
                case "duplicateFile":
                    console.log(ui.target);
                    targetElement = ui.target;
                    duplicateFile(ui.target, appIdHolder);

                    break;
                case "renameFile":
                    console.log(ui.target);
                    $("#sRenameFileModal").modal("show");
                    $("#inputRenameFilePath").val($(ui.target).attr("rel"));
                    $("#inputRenameFileType").val("file");
                    $("#inputRenameFileAppId").val(appIdHolder);
                    targetElement = ui.target;

                    break;
                case "cutFile":
                    console.log(ui.target);
                    targetElement = ui.target;
                    cutFile(ui.target, appIdHolder);

                    break;
                case "pasteFile":
                    console.log(ui.target);
                    targetElement = ui.target;
                    pasteFile(ui.target, appIdHolder);

                    break;
            }

        },
        // Implement the beforeOpen callback to dynamically change the entries
        beforeOpen: function (event, ui) {

            ui.menu.zIndex($(event.target).zIndex() + 1);
            
            console.log('beforeOpen ---> ' + ui.menu[0].childNodes[0].innerText);
            if(ui.menu[0].childNodes[0].innerText == 'new_folder'){
                $(document).contextmenu("setEntry", "createFolder", {title: $.i18n( 'new_folder' )});
                $(document).contextmenu("setEntry", "createFile", {title: $.i18n( 'new_file' )});
                $(document).contextmenu("setEntry", "deleteFile", {title: $.i18n( 'delete' )});
                $(document).contextmenu("setEntry", "duplicateFile", {title: $.i18n( 'duplicate' )});
                $(document).contextmenu("setEntry", "renameFile", {title: $.i18n( 'rename' )});
                $(document).contextmenu("setEntry", "uploadFile", {title: $.i18n( 'upload_file' )});
                $(document).contextmenu("setEntry", "cutFile", {title: $.i18n( 'cut_file' )});
                $(document).contextmenu("setEntry", "paste", {title: $.i18n( 'paste' )});
            }

            // Optionally return false, to prevent opening the menu now
            if ($(ui.target).data("type") == "directory") {
                $(document).contextmenu("enableEntry", "createFolder", true);
                $(document).contextmenu("enableEntry", "createFile", true);
                $(document).contextmenu("enableEntry", "uploadFile", true);
                $(document).contextmenu("enableEntry", "deleteFile", true);
                $(document).contextmenu("enableEntry", "duplicateFile", false);
                $(document).contextmenu("enableEntry", "renameFile", true);
                $(document).contextmenu("enableEntry", "cutFile", false);
                if (cutFileStatus) {
                    console.log("cutfilestatus : " + cutFileStatus + "  cutfilepath : " + cutFilePath);
                    $(document).contextmenu("enableEntry", "pasteFile", true);
                } else {
                    $(document).contextmenu("enableEntry", "pasteFile", false);
                }

            } else {
                $(document).contextmenu("enableEntry", "createFolder", false);
                $(document).contextmenu("enableEntry", "createFile", false);
                $(document).contextmenu("enableEntry", "uploadFile", false);
                $(document).contextmenu("enableEntry", "deleteFile", true);
                $(document).contextmenu("enableEntry", "duplicateFile", true);
                $(document).contextmenu("enableEntry", "renameFile", true);
                $(document).contextmenu("enableEntry", "cutFile", true);
                if (cutFileStatus) {
                    console.log("cutfilestatus : " + cutFileStatus + "  cutfilepath : " + cutFilePath);
                    $(document).contextmenu("enableEntry", "pasteFile", true);
                } else {
                    $(document).contextmenu("enableEntry", "pasteFile", false);
                }
            }

        }
    });

    // Hide the saved states & deployment &deleteApp button groups
    $("#preview.btn-group").hide();
    $("#savedStates.btn-group").hide();
    $("#deployment.btn-group").hide();
    $("#deleteApplication.btn-group").hide();
    $("#applicationSettings.btn-group").hide();
    $("#downloadApplication.btn-group").hide();

    $("#preview.btn-group").click(function () {

        showPreview("");
    });
    $("#deployment.btn-group").click(function () {
        //this function in application.js
        deployApplication($("#deployment.btn-group").data("application-id"));

    });

    $("#sendPushNotification").click(function (event) {
        event.preventDefault();

        $("#sendPushNotificationModal").modal("show");

    });

    $("#idInbox").click(function (event) {
        event.preventDefault();
        $("div#idPageContent").load("/inbox", {"": ""}, function () {

            buttonGroupFadeOut(500);

        });
    });

    $("#idDashboard").click(function (event) {
        event.preventDefault();

        $(".page-content").load("/getAnalyticReports", {"": ""}, function (responseText, textStatus, jqXHR) {
            //this function in dashboard.js 
            prepareDashboard();

        });

    });

    //$('.popovers').focusout(function () {

    //	$(this).popover("hide");
    //});

    $(document).on('click', '#jquery-btn-toolbar1', function (event) {
        var componentId = event.target.id;
        for (i = 0; i < jqueryComponentIdList.length; i++) {
            if (componentId == jqueryComponentIdList[i]) {
                return;
            }
        }
        if (componentId == "next-toolbar") {
            $("#jquery-btn-toolbar1").hide();
            $("#jquery-btn-toolbar2").show();
            return;
        } else if (componentId == "") {
            return;
        } else if (componentId == "jquery-btn-toolbar1") {
            return;
        }
        $.ajax({
            url: "/load/component",
            type: "POST",
            data: {componentName: componentId},
            success: function (response) {
                editor.replaceSelection(response, 'html');
//                $("#successModal").modal("show");
//                document.getElementById("alertText").innerHTML = "Component's example source code is added into the editor.";
                editor.markText({line: 1, ch: 1}, {line: 1, ch: 11}, {className: "styled-background"});
            },
            error: function (response) {

                $("#ErrorModal").modal("show");
                document.getElementById("errorText").innerHTML = response.responseText;
            }

        });

    });

    $(document).on('click', '#jquery-btn-toolbar2', function (event) {
        var componentId = event.target.id;
        for (i = 0; i < jqueryComponentIdList.length; i++) {
            if (componentId == jqueryComponentIdList[i]) {
                return;
            }
        }
        if (componentId == "back-toolbar") {
            $("#jquery-btn-toolbar1").show();
            $("#jquery-btn-toolbar2").hide();
            return;
        } else if (componentId == "") {
            return;
        } else if (componentId == "jquery-btn-toolbar2") {
            return;
        }
        $.ajax({
            url: "/load/component",
            type: "POST",
            data: {componentName: componentId},
            success: function (response) {
                editor.replaceSelection(response, 'html');
            },
            error: function (response) {

                $("#ErrorModal").modal("show");
                document.getElementById("errorText").innerHTML = response.responseText;
            }

        });

    });

                $("#idDashboard").click();

}); //document ready

loadContent = function (url, post, arg, fromApps) {

    console.log('loadContent ---> url: ' + url + ' -- ' + post + ' -- ' + arg + ' -- ' + appIdHolder + ' -- ' + fromApps);
    //console.log('loadContent ---> page-content: ' + $(".page-content").is(":visible") + ' -- ' + fileChanged.action);
    if ($(".page-content").is(":visible") && !fileChanged.action)
        $(".page-content").fadeOut(200, function () {

            if (post)
                $(".page-content").load(url, {applicationId: arg}, function () {
                    //console.log('loadContent ---> page-content: ' + $(".page-content").is(":visible") + ' -- ' + fileChanged.action + ' -- ' + fromApps);
                    if (!$(".page-content").is(":visible"))
                        $(".page-content").fadeIn(200, function () {
                            if(document.getElementById("displayAppName"))
                                document.getElementById("displayAppName").innerHTML = $.i18n( 'app_files' ) + " - " + appIdHolder;
                            else
                            if(!fromApps){
                                location.reload();
                            }
                            if(fromApps){
                                //alert($("li[data-application-id='" + arg + "']").attr('class') + ' -- ' + arg);
                                $('> a',$("li[data-application-id='" + arg + "']")).addClass('active');
                            }

                            //console.log('loadContent ---> treeRoot: ' + $("div#holder").data("holder-value") + ' -- ' + fileChanged.action);
                            $("#treeRoot").attr("rel", $("div#holder").data("holder-value") + "/assets/" + arg);

                            $('#testtree').fileTree({root: $("div#holder").data("holder-value") + "/assets/" + arg, script: '../tree/jqueryFileTree.jsp', multiFolder: false, expandSpeed: 750, collapseSpeed: 750}, function (file) {
                                //TODO: Control the other conditions later. By omur
                                //console.log('loadContent ---> testtree: ' + fileChanged.action);
                                console.log(file);
                                if(!fileChanged.action){
                                    currentFile = file;
                                    loadFileContent("/file/content", file);
                                }
                                else{
                                    $("#saveControlModal").modal("show");
                                    document.getElementById("saveWarningText").innerHTML = $.i18n( 'change_not_saved' );
                                }
                                console.log('loadContent1 ---> fileChanged: ' + fileChanged.action);
                            });


                        });

                });
            else
                $(".page-content").load(url, function () {
                    if (!$(".page-content").is(":visible"))
                        $(".page-content").fadeIn(200, function () {
                            document.getElementById("displayAppName").innerHTML = $.i18n( 'app_files' ) + " - " + appIdHolder;
                            $("#treeRoot").attr("rel", $("div#holder").data("holder-value") + "/assets/" + arg);

                            $('#testtree').fileTree({root: $("div#holder").data("holder-value") + "/assets/" + arg, script: '../tree/jqueryFileTree.jsp', multiFolder: false, expandSpeed: 750, collapseSpeed: 750}, function (file) {
                                if(!fileChanged.action){
                                    currentFile = file;
                                    loadFileContent("/file/content", file);
                                }
                                console.log('loadContent3 ---> fileChanged: ' + fileChanged.action);
                            });

                        });

                });

        });
    else if (post && !fileChanged.action)
        $(".page-content").load(url, {applicationId: arg}, function () {
            if (!$(".page-content").is(":visible"))
                $(".page-content").fadeIn(200, function () {

                    document.getElementById("displayAppName").innerHTML = $.i18n( 'app_files' ) + " - " + appIdHolder;
                    $("#treeRoot").attr("rel", $("div#holder").data("holder-value") + "/assets/" + arg);

                    $('#testtree').fileTree({root: $("div#holder").data("holder-value") + "/assets/" + arg, script: '../tree/jqueryFileTree.jsp', multiFolder: false, expandSpeed: 750, collapseSpeed: 750}, function (file) {
                        if(!fileChanged.action){
                            currentFile = file;
                            loadFileContent("/file/content", file);
                        }
                        console.log('loadContent2 ---> fileChanged: ' + fileChanged.action);
                    });

                });

        });
    else if (!fileChanged.action)
        $(".page-content").load(url, function () {
            if (!$(".page-content").is(":visible"))
                $(".page-content").fadeIn(200, function () {

                    document.getElementById("displayAppName").innerHTML = $.i18n( 'app_files' ) + " - " + appIdHolder;

                    $("#treeRoot").attr("rel", $("div#holder").data("holder-value") + "/assets/" + arg);

                    $('#testtree').fileTree({root: $("div#holder").data("holder-value") + "/assets/" + arg, script: '../tree/jqueryFileTree.jsp', multiFolder: false, expandSpeed: 750, collapseSpeed: 750}, function (file) {
                        if(!fileChanged.action){
                        currentFile = file;
                        loadFileContent("/file/content", file);
                        }
                        console.log('loadContent4 ---> fileChanged: ' + fileChanged.action);
                    });
                });
        });

    cutFileStatus = false;

};

loadFileContent = function (url, path) {

    $("div#fileContent").load(url, {"appname": $("#deployment.btn-group").data("application-id"), "fileurl": path}, function () {
        var appname = $("#deployment.btn-group").data("application-id");
        var fileName = path.split("/");
        var arrangedPath = '';
        $.each( fileName, function( i, val ) {
            if(val === appname)
                arrangedPath = '/';
            else
            if(arrangedPath !== '')
                arrangedPath += val + (i === fileName.length - 1 ? '' : '/');
        });
        //console.log('loadFileContent ---> arrangedPath: ' + arrangedPath);
        document.getElementById("displayFileName").innerHTML = $.i18n( 'file' ) + " - " + arrangedPath;

        if (document.getElementById("rawScreenEditor")) {
            //console.log('loadFileContent ---> currentEditor: ' + $("#typeHolder").data("type"));
            currentEditor($("#typeHolder").data("type"), path);

            if (previewCount == null) {
                previewCount = 0;
            }
            if (previewSourceArray == null) {
                previewSourceArray = [];
            }
            previewSourceArray[previewCount] = editor.doc.getValue();

        } else {
            $("#componentHeader").hide();
            $("#headerPreviewButtons").hide();
        }
    });

};

currentEditor = function (a, b) {
    console.log('currentEditor ---> ' + a + ' --- ' + b);
    document.getElementById("displayAppName").innerHTML = $.i18n( 'app_files' ) + " - " + appIdHolder;
    switch (a) {
        case "html":
            editor = CodeMirror.fromTextArea(document.getElementById("rawScreenEditor"), {
                lineNumbers: true,
                theme: "eclipse",
                styleSelectedText: true,
                gutters: ["CodeMirror-linenumbers", "breakpoints"],
                extraKeys: {
                    "Shift-Ctrl-Z": function () {
                        if (previewCount - 1 >= 0) {
                            editor.setValue(previewSourceArray[previewCount - 1]);
                            previewCount--;
                        }
                    },
                    "Shift-Ctrl-Y": function () {
                        if (previewSourceArray.length >= previewCount + 1) {
                            editor.setValue(previewSourceArray[previewCount + 1]);
                            previewCount++;
                        }
                    },
                    "Ctrl-Space": "autocomplete",
                    "Ctrl-S": function () {
                        $("#rawScreenEditor").submit();
                        fileChanged = {'action':false,'path':''};
                    },
                    "Ctrl-D": function () {
                        saveAndDeploy();
                    },
                    "F11": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Shift-Esc": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Esc": function (cm) {

                        document.getElementById("headerBar").removeAttribute("style");

                        cm.setOption("fullScreen", false);
                    }
                },
                mode: "htmlmixed"
            });

            editor.on("gutterClick", function (cm, n) {
                var info = cm.lineInfo(n);
                cm.setGutterMarker(n, "breakpoints", info.gutterMarkers ? null : makeMarker());
            });

            appFramework = $("#frameworkName").val();
            $("#componentHeader").show();
            if (appFramework == 'jquery') {
                registerImagePreview(jqueryComponentIdList);
            }
            $("#headerPreviewButtons").show();
            break;
        case "js":
            editor = CodeMirror.fromTextArea(document.getElementById("rawScreenEditor"), {
                lineNumbers: true,
                theme: "eclipse",
                extraKeys: {
                    "Ctrl-Space": "autocomplete",
                    "Ctrl-S": function () {
                        $("#rawScreenEditor").submit();
                        fileChanged = {'action':false,'path':''};
                    },
                    "Ctrl-D": function () {
                        saveAndDeploy();
                    },
                    "F11": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Shift-Esc": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Esc": function (cm) {

                        document.getElementById("headerBar").removeAttribute("style");

                        cm.setOption("fullScreen", false);
                    }
                },
                mode: {name: "javascript", globalVars: true}
            });
            $("#componentHeader").hide();
            $("#headerPreviewButtons").hide();
            break;
        case "css":
            editor = CodeMirror.fromTextArea(document.getElementById("rawScreenEditor"), {
                lineNumbers: true,
                theme: "eclipse",
                extraKeys: {
                    "Ctrl-Space": "autocomplete",
                    "Ctrl-S": function () {
                        $("#rawScreenEditor").submit();
                        fileChanged = {'action':false,'path':''};
                    },
                    "Ctrl-D": function () {
                        saveAndDeploy();
                    },
                    "F11": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Shift-Esc": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Esc": function (cm) {

                        document.getElementById("headerBar").removeAttribute("style");

                        cm.setOption("fullScreen", false);
                    }
                },
                mode: {name: "css"}
            });
            $("#componentHeader").hide();
            $("#headerPreviewButtons").hide();
            break;
        case "plain":
            editor = CodeMirror.fromTextArea(document.getElementById("rawScreenEditor"), {
                lineNumbers: true,
                theme: "eclipse",
                extraKeys: {
                    "Ctrl-S": function () {
                        $("#rawScreenEditor").submit();
                        fileChanged = {'action':false,'path':''};
                    },
                    "Ctrl-D": function () {
                        saveAndDeploy();
                    },
                    "F11": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Shift-Esc": function (cm) {

                        document.getElementById("headerBar").setAttribute("style", "display:none;");

                        cm.setOption("fullScreen", true);
                    },
                    "Esc": function (cm) {

                        document.getElementById("headerBar").removeAttribute("style");

                        cm.setOption("fullScreen", false);
                    }
                },
                mode: {name: "javascript", globalVars: true}
            });
            $("#componentHeader").hide();
            $("#headerPreviewButtons").hide();
            break;
        default :
            $("#editorSubmit").prop("disabled", true);
            $("#editorSubmitAndDeploy").prop("disabled", true);
            $("#componentHeader").hide();
            $("#headerPreviewButtons").hide();

    }

    if(editor){
        
        editor.on("change", function (cm, n) {
                fileChanged = {'action':true,'path':b};
                //console.log('loadFileContent ---> fileChanged: ' + fileChanged);
                console.log(fileChanged);
        });
        
    }

    bindForm(b);
};

bindForm = function (b) {
    $("form#rawScreenEdit").on("submit", function (event) {
        event.preventDefault();
        var p = document.getElementById("repeater");
        var loading = $('<div class="loading"><i class="fa fa-refresh fa-spin"></i></div>');

        $.ajax({
            url: "/app/filecontent/update",
            type: "POST",
            data: {fileUrl: b, content: editor.doc.getValue()},
            beforeSend: function () {

                loading.appendTo(p);
                loading.fadeIn();

            },
            success: function (data) {

                loading.fadeOut();
                fileChanged = {'action':false,'path':''};

            },
            error: function (response) {

                loading.fadeOut();

                $("#ErrorModal").modal('show');

                document.getElementById("errorText").innerHTML = response.responseText;
            }
        });

    });


};

createMorrisBar = function (id) {
    var contextid = id;
    Morris.Bar({
        element: contextid,
        data: [
            {y: '2006', a: 100, b: 90, c: 10},
            {y: '2007', a: 75, b: 65, c: 10},
            {y: '2008', a: 50, b: 40, c: 10},
            {y: '2009', a: 75, b: 65, c: 10},
            {y: '2010', a: 50, b: 40, c: 10},
            {y: '2011', a: 75, b: 65, c: 10},
            {y: '2012', a: 100, b: 90, c: 10}
        ],
        xkey: 'y',
        ykeys: ['a', 'b', 'c'],
        labels: ['Series A', 'Series B', 'Series C']
    });
};

buttonGroupFadeOut = function (ms) {
    $("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group,#downloadApplication.btn-group").fadeOut(ms);
};

buttonGroupFadeIn = function (ms) {
    $("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group,#downloadApplication.btn-group").fadeIn(ms);
};

registerImagePreview = function (list) {
    var i = 0;
    while (list[i]) {
        var component = document.getElementById(list[i]);
        $(component).imgPreview({
            containerID: 'imgPreviewWithStyles',
            /* Change srcAttr to rel: */
            srcAttr: 'rel',
            imgCSS: {
                // Limit preview size:
                height: 300
            },
            // When container is shown:
            onShow: function (link) {
                // Animate link:
                $(link).stop().animate({opacity: 0.4});
                // Reset image:
                $('img', this).stop().css({opacity: 0});
            },
            // When image has loaded:
            onLoad: function () {
                // Animate image
                $(this).animate({opacity: 1}, 500);
            },
            // When container hides: 
            onHide: function (link) {
                // Animate link:
                $(link).stop().animate({opacity: 1});
            }

        });
        i++;
    }

};

function makeMarker() {
    var marker = document.createElement("div");
    marker.style.color = "#822";
    marker.innerHTML = "x";
    return marker;
}

function addFields() {
    // Number of inputs to create
    var number = document.getElementById("member").value;
    // Container <div> where dynamic content will be placed
    var container = document.getElementById("ptab2");
    // Clear previous contents of the container
    while (container.hasChildNodes()) {
        container.removeChild(container.lastChild);
    }
    for (i = 0; i < number; i++) {
        // Append a node with a random text
        container.appendChild(document.createTextNode("Member " + (i + 1)));
        // Create an <input> element, set its type and name attributes
        var input = document.createElement("input");
        input.type = "text";
        input.name = "member" + i;
        container.appendChild(input);
        // Append a line break 
        container.appendChild(document.createElement("br"));
    }
}

$('input[name=filterApp]').keyup(function() { 
    
    var filterStr = $(this).val();

    console.log('filterApp::change --- > ' + filterStr);
    $("li[data-application-id]").show();
    if (filterStr.length > 0)
        $("li[data-application-id]").filter(function () {
                var str = $(this).text();
                var re = new RegExp(filterStr, "i");
                var result = re.test(str);
                if (!result) {
                    return $(this);
                }
        }).hide();

});
