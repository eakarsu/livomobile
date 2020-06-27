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

// End of user list retrieving code.

    $("#idDashboard").click();

}); //document ready