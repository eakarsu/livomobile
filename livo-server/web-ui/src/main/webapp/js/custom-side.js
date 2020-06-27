
$(document).ready(function () {

    /*==Left Navigation Accordion ==*/
    if ($.fn.dcAccordion && $('#nav-accordion')) {
        $('#nav-accordion').dcAccordion({
            eventType: 'click',
            autoClose: true,
            saveState: true,
            disableLink: true,
            speed: 'slow',
            showCount: false,
            autoExpand: true,
            classExpand: 'dcjq-current-parent'
        });
    }
    if ($.fn.niceScroll) {

        $(".leftside-navigation").niceScroll({
            cursorcolor: "#959595",
            cursorborder: "0px solid #fff",
            cursorborderradius: "0px",
            cursorwidth: "10px"
        });
        if ($(window).width() < 750)
        {
            $('#sidebar').addClass('hide-left-bar');
            $('#main-content').addClass('merge-left');
        }
        $(".leftside-navigation").getNiceScroll().resize();
        if ($('#sidebar').hasClass('hide-left-bar')) {
            $(".leftside-navigation").getNiceScroll().hide();
        }
        $(".leftside-navigation").getNiceScroll().show();

        $(".right-stat-bar").niceScroll({
            cursorcolor: "#959595",
            cursorborder: "0px solid #fff",
            cursorborderradius: "0px",
            cursorwidth: "2px"
        });

    }
    
    $(".leftside-navigation").mouseover(function () {
        $(".leftside-navigation").getNiceScroll().resize();
    });

    /*==Sidebar Toggle==
    $(".leftside-navigation .sub-menu > a").click(function () {
        var o = ($(this).offset());
        var diff = 80 - o.top;
        if (diff > 0)
            $(".leftside-navigation").scrollTo("-=" + Math.abs(diff), 500);
        else
            $(".leftside-navigation").scrollTo("+=" + Math.abs(diff), 500);
    });*/

    //reloader end.
//};
//

    $("select.image-picker").imagepicker({
        hide_select: true,
    });

    $("select.image-picker.show-labels").imagepicker({
        hide_select: true,
        show_label: true,
    });

    $("select.image-picker.limit_callback").imagepicker({
        limit_reached: function () {
            alert('We are full!')
        },
        hide_select: false
    });
    
    var container = $("select.image-picker.masonry").next("ul.thumbnails");
        container.imagesLoaded(function () {
            container.masonry({
                itemSelector: "li"
        });

    });
    
    $('#userCompanyList').multiSelect();
    $('#userCompanyList2').multiSelect();
    $('#groupCompanyList').multiSelect();
    $('#groupCompanyList2').multiSelect();
    $('#appCompanyList').multiSelect();
    
    $.fn.acceptFileType=function( types )
    {
        console.log(types);
        if ( types == undefined )
        {
            return true;
        }else{
            types = types.split(",")
        }
        this.each(function(){
            $( this ).bind("change",function()
            {
                console.log($( this ).val());
                if( !$.inArray( $( this ).val().replace(/([\d\w.]+)(\.[a-z0-9]+)/i,'\2') , types ) )
                {
                    $( this ).val('');
                    console.log($( this ).val());
                    return false;
                }
                return true;
            });
        });
    };
    
    $( ":file" ).acceptFileType(".zip");

}); //document ready

if ($.fn.niceScroll && $(".leftside-navigation") && !$(".leftside-navigation").niceScroll) {

    $(".leftside-navigation").niceScroll({
        cursorcolor: "#959595",
        cursorborder: "0px solid #fff",
        cursorborderradius: "0px",
        cursorwidth: "10px"
    });
    if ($(window).width() < 750)
    {
        $('#sidebar').addClass('hide-left-bar');
        $('#main-content').addClass('merge-left');
    }
    $(".leftside-navigation").getNiceScroll().resize();
    if ($('#sidebar').hasClass('hide-left-bar')) {
        $(".leftside-navigation").getNiceScroll().hide();
    }
    $(".leftside-navigation").getNiceScroll().show();

    $(".right-stat-bar").niceScroll({
        cursorcolor: "#959595",
        cursorborder: "0px solid #fff",
        cursorborderradius: "0px",
        cursorwidth: "2px"
    });

}

//Getting directory
$.ajax({
    url: "/homedirpath",
    type: "POST",
    data: "",
    processData: false,
    contentType: false,
    success: function (response) {

        $("div#holder").data("holder-value", response);

    },
    error: function () {

        alert("there is an error");

        window.location = "";
    }
});
//end of getting directory

// Start of user list retrieving code.
$("#idUsersList").click(function (event) {

    event.preventDefault();

    $("div#idPageContent").load("/userTable", {"": ""}, function () {
        //this function in users.js
        loadUsersTableDynamics();
        //this function in users.js
        loadUserActions();

        buttonGroupFadeOut(500);
    });
});

$("#idUserGroups").click(function (event) {

    $("div#idPageContent").load("/groupTable", {"": ""}, function () {
        //this function in groups.js
        loadGroupsTableDynamics();
        //this function in groups.js
        loadGroupActions();

        buttonGroupFadeOut(500);
    });

});

//Start of user list retrieving code.
$("#idWebUsersList").click(function (event) {

    event.preventDefault();

    $("div#idPageContent").load("/webUserTable", {"": ""}, function () {
        //this function in userManagement.js
        loadWebUsersTableDynamics();
        //this function in userManagement.js
        loadWebUserActions();

        buttonGroupFadeOut(500);
    });
});
$("#idKnowledgeBase").click(function (event) {
    event.preventDefault();
    $("div#idPageContent").load("/knowledgeBase", {"": ""}, function () {

    });
});

$("#idDevices").click(function (event) {
    event.preventDefault();
    $("div#idPageContent").load("/mDevices", {"": ""}, function () {
       
       loadDevicesTableDynamics();
        
       loadDevicesActions();
       
       buttonGroupFadeOut(500);
       
    });
});
$("#idCompanies").click(function (event) {
    event.preventDefault();
    $("div#idPageContent").load("/cCompanies", {"": ""}, function () {
       
       loadCompaniesTableDynamics();
        
       loadCompaniesActions();
       
       buttonGroupFadeOut(500);
       
    });
});
$("#idDeployVersions").click(function (event) {
    event.preventDefault();
    $("div#idPageContent").load("/deployVersions", {"": ""}, function () {
       
       loadDeploymentTableDynamics();
        
       loadDeploymentActions();
       
       buttonGroupFadeOut(500);
       
    });
});
$("form#ImageUploading").submit(function (event) {
    //disable the default form submission
    event.preventDefault();
    //grab all form data  
    var formData = new FormData($(this)[0]);

    $.ajax({
        url: '/uploadImage',
        type: 'POST',
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        success: function (returndata) {
            document.getElementById('logoIcon').src = returndata;
            document.getElementById('logoIconPreview').src = returndata;
            document.getElementById('imageUploadError').innerHTML = '';
        },
        error: function (response) {

            $("#imageUploadError").fadeIn(300, function () {

                document.getElementById("imageUploadError").innerHTML = response.responseText;

            });
        }
    });

    return false;
});

$("#idGlobalSettings").click(function (event) {
    event.preventDefault();

    $("div#idPageContent").load("/globalSettings", {"": ""}, function () {
        //this function in globalSettings.js
        loadGlobalSettingsPage();

        buttonGroupFadeOut(500);

    });
});

$("#idLdapAD").click(function (event) {
    event.preventDefault();
    //this function in body.ldap.js
    loadLdapPage();

    buttonGroupFadeOut(500);

});

$("#idProfile").click(function (event) {
    event.preventDefault();
    $("#sUploadImage").modal("show");
    document.getElementById("modifyAccountError").innerHTML = "";
    $("#iDeveloperPassword").val("");
    $("#iRetypePassword").val("");
    $("#iDeveloperMail").val("");
});

$("#idServices").click(function (event) {
    event.preventDefault();
    console.log('idServices::click');
    $("div#idPageContent").load("/services", {"": ""}, function () {

        //this function in serviceManagement.js
        loadServicesPage();

    });

});

$('#messageTypeSelect').on('change', function () {

    $("#messageTypeIcon").text(this.value);

});

// End of user list retrieving code.
$("a#applicationUploadSuggesstion").click(function (event) {

    event.preventDefault();

    if ($("input#applicationUpload").is(":visible")) {

        $("input#applicationUpload").removeAttr("required", "required");
        $("input#applicationUpload").removeAttr("data-parsley-trigger", "change");

        $("div#applicationUploadSuggestion").fadeIn(500, function () {

            $("div#applicationUpload").fadeToggle(500);

            $("a#applicationUploadSuggesstion").text($.i18n( 'upload_app_template' ));
        });

    } else {

        $("input#applicationUpload").attr("required", "required");
        $("input#applicationUpload").attr("data-parsley-trigger", "change");

        $("div#applicationUpload").fadeToggle(500, function () {

            $("div#applicationUploadSuggestion").fadeIn(500, function () {

                $("a#applicationUploadSuggesstion").text($.i18n( 'dont_upload_app_template' ));
            });
        });
    }
});


$("#drafts-jquery-div").hide();
$('#createApplication input[name=framework]').on('change', function () {
    var framework = $('input[name=framework]:checked', '#createApplication').val();
    switch (framework) {
        case 'angularjs':
            $("#drafts-jquery-div").hide();
            $("#drafts-angularjs-div").show();
            break;
        case 'jquery':
            $("#drafts-angularjs-div").hide();
            $("#drafts-jquery-div").show();
            break;
        case 'empty' :
            $("#drafts-angularjs-div").hide();
            $("#drafts-jquery-div").hide();
            break;
    }
});

$(document).on('click', '#logoIcon', function () {

    $("#sUploadImage").modal("show");

});

$("form#ImageUploading").submit(function (event) {
    //disable the default form submission
    event.preventDefault();
    //grab all form data  
    var formData = new FormData($(this)[0]);

    $.ajax({
        url: '/uploadImage',
        type: 'POST',
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        success: function (returndata) {
            document.getElementById('logoIcon').src = returndata;
            document.getElementById('logoIconPreview').src = returndata;
            document.getElementById('imageUploadError').innerHTML = '';
        },
        error: function (response) {

            $("#imageUploadError").fadeIn(300, function () {

                document.getElementById("imageUploadError").innerHTML = response.responseText;

            });
        }
    });

    return false;
});

$("form#modifyAccountInfosForm").submit(function (event) {
    //disable the default form submission
    event.preventDefault();
    //grab all form data  
//      var formData = new FormData($(this)[0]);

    var userName = $("#iDeveloperUserName").val();
    var password = $("#iDeveloperPassword").val();
    var retypePassword = $("#iRetypePassword").val();
    var userMail = $("#iDeveloperMail").val();

    if (password !== "" && retypePassword === "") {
        document.getElementById("iDeveloperPasswordError").innerHTML = $.i18n( 'retype_pass_req' );
        return false;
    }

    $.ajax({
        url: '/modifySelfAccount',
        type: 'POST',
        data: {userName: userName, password: password, userMail: userMail},
        success: function () {
            $("#sUploadImage").modal("hide");
            $("#idDashboard").click();
        },
        error: function (response) {

            document.getElementById("modifyAccountError").innerHTML = response.responseText;

        }
    });

});

$(".dcjq-parent-li.app #app").click(function (event) {

    event.preventDefault();

    var applicationId = $(this).parent().data("application-id");
    console.log('App clicked --- > ' + applicationId);

    if ($("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group,#downloadApplication.btn-group").is(":visible")) {

        if (appIdHolder == applicationId) {

//			$("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group").fadeOut(500);
            buttonGroupFadeOut(500);
//                    	$("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group").fadeIn(500);
            buttonGroupFadeIn(500);
        } else {

            //give id of the app to hold for later use
            //$("#savedStates.btn-group").data("application-id", applicationId);

            $("#deployment.btn-group").data("application-id", applicationId);

            $("#deleteApplication.btn-group").data("application-id", applicationId);

            $("#preview.btn-group").data("application-id", applicationId);

            $("#applicationSettings.btn-group").data("application-id", applicationId);

            $("#downloadApplication.btn-group").data("application-id", applicationId);

        }

        //loadContent("/welcome", true);
        loadContent("/apps/", true, applicationId);

        appIdHolder = $(this).parent().data("application-id");

    } else {

        //show save|delete|deploy buttons
//			$("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group").fadeIn(500);
        buttonGroupFadeIn(500);
        //give id of the app to hold for later use
        //$("#savedStates.btn-group").data("application-id", applicationId);

        $("#deployment.btn-group").data("application-id", applicationId);

        $("#deleteApplication.btn-group").data("application-id", applicationId);

        $("#preview.btn-group").data("application-id", applicationId);

        $("#applicationSettings.btn-group").data("application-id", applicationId);

        $("#downloadApplication.btn-group").data("application-id", applicationId);

        //save current app id to global holder
        appIdHolder = $(this).parent().data("application-id");

        loadContent("/apps/", true, applicationId);

        loadApplicationSavedStates(applicationId);
    }
});

$(".dcjq-parent-li:not(.app)").off("click").click(function (event) {
    console.log('.dcjq-parent-li:not(.app)');
//		$("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group").fadeOut(400);
    buttonGroupFadeOut(400);
});