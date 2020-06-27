/*** Javascript codes of application settings page. ***/

$("#applicationSettings").click(function (event) {
        event.preventDefault();
        
        $("#componentHeader").hide();
        $("#headerPreviewButtons").hide();
        
        if (appIdHolder != $("#applicationSettings.btn-group").data("application-id")) {

            alert("Why are you trying dirty things????");
        } else {
            setTimeout(function () {
                $("div#fileContent").load("/apps/settings/" + appIdHolder, {"": ""}, function (responseText, textStatus, jqXHR) {
                    document.getElementById("displayFileName").innerHTML = $.i18n( 'app_props' );
                    console.log(textStatus);
                    $("form#androidPushNotificationForm").submit(function () {

                        var formData = new FormData($(this)[0]);
//        console.log(formData);
                        $.ajax({
                            url: "/apps/notifications/pushNotification/android",
                            type: 'POST',
                            data: formData,
                            async: false,
                            success: function (response) {
                                $("#successModal").modal("show");
                                document.getElementById("alertText").innerHTML = response;
                                $("form#androidPushNotificationForm").find("input, textarea").val("");
                            }, error: function (response) {
                                $("#ErrorModal").modal("show");
                                document.getElementById("errorText").innerHTML = response.responseText;
                            },
                            cache: false,
                            contentType: false,
                            processData: false
                        });

                        return false;
                    });
                    
                    $("form#androidIconSplashForm").submit(function () {

                        var formData = new FormData($(this)[0]);
//        console.log(formData);
                        $.ajax({
                            url: "/apps/iconsplashes/assets/android",
                            type: 'POST',
                            data: formData,
                            async: false,
                            success: function (response) {
                                $("#successModal").modal("show");
                                document.getElementById("alertText").innerHTML = response;
                                $("form#androidIconSplashForm").find("input, textarea").val("");
                            }, error: function (response) {
                                $("#ErrorModal").modal("show");
                                document.getElementById("errorText").innerHTML = response.responseText;
                            },
                            cache: false,
                            contentType: false,
                            processData: false
                        });

                        return false;
                    });
                    
                    $("form#appleIconSplashForm").submit(function () {

                        var formData = new FormData($(this)[0]);
//        console.log(formData);
                        $.ajax({
                            url: "/apps/iconsplashes/assets/ios",
                            type: 'POST',
                            data: formData,
                            async: false,
                            success: function (response) {
                                $("#successModal").modal("show");
                                document.getElementById("alertText").innerHTML = response;
                                $("form#appleIconSplashForm").find("input, textarea").val("");
                            }, error: function (response) {
                                $("#ErrorModal").modal("show");
                                document.getElementById("errorText").innerHTML = response.responseText;
                            },
                            cache: false,
                            contentType: false,
                            processData: false
                        });

                        return false;
                    });
                    
                    $("#authApplicationSubmitForm").click(function (event) {
                        console.log($("#authorizedUserGroupSelect").val() + " -- " + $("#groupList").val());
                        var authorizedSource = $("#authorizedUserGroupSelect").val();
                        var groupList = ',';
                        if($("#groupList").val())
                            $.each( $("#groupList").val(), function( i, val ) {
                                groupList += val + ',';
                            });
                        $.ajax({
                            url: "/apps/saveAuthSettings/" + appIdHolder,
                            type: "POST",
                            data: {authorizedSource: authorizedSource, groupList: groupList}, 
                            success: function () {

                                document.getElementById("alertText").innerHTML = $.i18n( 'auth_sett_saved' ) + appIdHolder;
                                $("#successModal").modal("show");
                                $("#authApplicationSubmitForm").find("input, textarea").val("");

                            },
                            error: function (response) {

                                $("#successModal").modal("hide");

                            }
                        });
                    });
                    
                    $("#relationSubmitForm").click(function (event) {
                        var companyList = ',';
                        if($("#companyList").val())
                            $.each( $("#companyList").val(), function( i, val ) {
                                companyList += val + ',';
                            });
                        $.ajax({
                            url: "/apps/saveRelSettings/" + appIdHolder,
                            type: "POST",
                            data: {authorizedOwner: $("#authorizedOwner").val(), deviceLimit: 1000, companyList: companyList}, //$("#deviceLimit").val()
                            success: function () {

                                document.getElementById("alertText").innerHTML = $.i18n( 'rel_sett_saved' ) + appIdHolder;
                                $("#successModal").modal("show");
                                $("#relationSubmitForm").find("input, textarea").val("");

                            },
                            error: function (response) {

                                $("#successModal").modal("hide");

                            }
                        });
                    });

                    $('#pnPlatformSelect').on('change', function (e) {
//                        var platformType = $("option:selected", this);
                        var valueSelected = this.value;
                        if (valueSelected !== 0) {
                            if (valueSelected === "android") {
                                $("#IOSNotificationSettings").hide();
                                $("#androidNotificationSettings").show();
                            } else if (valueSelected === "ios") {
                                $("#androidNotificationSettings").hide();
                                $("#IOSNotificationSettings").show();

                            } else {
                                $("#androidNotificationSettings").hide();
                                $("#IOSNotificationSettings").hide();
//                                alert("00");
                            }

                        } else {
//                            alert("0");
                            $("#androidNotificationSettings").hide();
                            $("#IOSNotificationSettings").hide();
                        }
                    });

                    $('#authorizedUserGroupSelect').on('change', function (e) {

                        var valueSelected = this.value;

                        if (valueSelected !== "0") {

                            if (valueSelected === "any" || valueSelected === "company") {

                                $('#groupList').css("display", "none");
                                $('#ldapList').css("display", "none");
                                $('#ms-groupList').css("display", "none");
                                $('#ms-ldapList').css("display", "none");

                            } else if (valueSelected === "ldap" || valueSelected === "ldap_withcomp") {
                                // Groups Combobox add ldap connections groups
                                $('#ldapList').css("display", "inline-block");
                                $('#ms-ldapList').css("display", "inline-block");
                                $('#groupList').css("display", "none");
                                $('#ms-groupList').css("display", "none");

                            } else if (valueSelected === "System" || valueSelected === "System_withcomp") {
                                // Groups Combobox add system  groups    
                                $('#groupList').css("display", "inline-block");
                                $('#ms-groupList').css("display", "inline-block");
                                $('#ldapList').css("display", "none");
                                $('#ms-ldapList').css("display", "none");

                            }
                        } else {
                            $('#groupList').css("display", "none");
                            $('#ldapList').css("display", "none");
                            $('#ms-groupList').css("display", "none");
                            $('#ms-ldapList').css("display", "none");
                        }
                    });

                });
            }, 800);
        }
    });

