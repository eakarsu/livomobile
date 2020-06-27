/*** Javascript codes of application settings page. ***/

var userHolder;
var clientUserName, clientGroupId = '';

//Start usercreate submission.
$(document).on('click', '#createNewUserButton', function (event) {
    document.getElementById("userCreationResult").innerHTML = "";
    console.log('length: ' + $("#cUserPassword").val().length);
    if ($("#cUserPassword").val().length >= 4) {

        if ($("#createNewUserForm").parsley().isValid()) {
            event.preventDefault();
            $.ajax({
                url: "/createnewuser",
                type: "POST",
                data: {userName: $("#cUserName").val(), userMail: $("#cUserMail").val(), userGroupName: $("#selectGroupForUser").val(), Mail: $("#cUserMail").val(), userPassword: $("#cUserPassword").val()},
                success: function () {
                    clearNewUserFormParameters();
                    $("#sCreateNewUser").modal("hide");
                    $("#idUsersList").click();
                    $("#createNewUserForm").find("input, textarea").val("");

                },
                error: function (response) {
                    clearNewUserFormParameters();
                    $("#sCreateNewUser").modal("hide");
                    $("#idUsersList").click();
                    document.getElementById("userCreationResult").innerHTML = response.responseText;

                }
            });
        }
    } else {
        document.getElementById("userPass3Error").innerHTML = $.i18n( 'pass_must_min6' );
    }
    return false;
});
//
//Start of user list retrieving code.
//User mail change.
$("#submitUserMailChange").click(function (event) {

    if ($("#changeUserMailForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/changeusermail",
            type: "POST",
            data: {userId: userHolder, userMail: $("#iUserMail").val()},
            success: function () {

                document.getElementById("changeUserMailError").innerHTML = "";
                $("#changeUserAttributesModal").modal("hide");
                $("#idUsersList").click();
                $("#changeUserMailForm").find("input, textarea").val("");

            },
            error: function (response) {

                document.getElementById("changeUserMailError").innerHTML = response.responseText;

            }
        });
    }
});

$("#submitUserGroupChange").click(function (event) {
    
    if ($("#changeUserGroupForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/changeusergroup",
            type: "POST",
            data: {userId: userHolder, userGroup: $("#selectGroupForUser2").val()},
            success: function () {

                $("#changeUserAttributesModal").modal("hide");
                $("#idUsersList").click();
                $("#changeUserGroupForm").find("input, textarea").val("");

            },
            error: function (response) {

                //document.getElementById("changeUserMailError").innerHTML = response.responseText;

            }
        });
    }
});


//User name change.
$("#submitUserNameChange").click(function (event) {



    if ($("#changeUserNameForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/changeUserNameWithNew",
            type: "POST",
            data: {userId: userHolder, userName: $("#iUserName").val()},
            success: function () {

                document.getElementById("changeUserNameError").innerHTML = "";
                $("#changeUserAttributesModal").modal("hide");
                $("#idUsersList").click();
                $("#changeUserNameForm").find("input, textarea").val("");

            },
            error: function (response) {

                document.getElementById("changeUserNameError").innerHTML = response.responseText;

            }

        });


    }

});

//User pasword change.
$("#submitUserPasswordChange").click(function (event) {



    if ($("#changeUserPasswordForm").parsley().isValid()) {
        event.preventDefault();
        if ($("#iUserPassword").val().length >= 4) {


            $.ajax({
                url: "/changeuserpassword",
                type: "POST",
                data: {userId: userHolder, userName: clientUserName, userPassword: $("#iUserPassword").val()},
                success: function () {

                    document.getElementById("iUserPasswordError").innerHTML = "";
                    $("#changeUserAttributesModal").modal("hide");
                    $("#idUsersList").click();
                    $("#changeUserPasswordForm").find("input, textarea").val("");

                },
                error: function (response) {

                    document.getElementById("iUserPasswordError").innerHTML = response.responseText;

                }

            });


        } else {

            document.getElementById("iUserPasswordError").innerHTML = $.i18n( 'pass_must_min4' );
        }
    }
});


$("#deleteUserButton").click(function () {

    $.ajax({
        url: "/deleteuser",
        type: "POST",
        data: {userId: userHolder},
        success: function () {

            $("#deleteUserModal").modal("hide");

            document.getElementById("deleteUserError").innerHTML = "";

            $("#idUsersList").click();

        },
        error: function (response) {

            document.getElementById("deleteUserError").innerHTML = response.responseText;

        }

    });

});

//Import users from file
$("#controlSuccessButton").click(function (event) {
    event.preventDefault();

    $("#ImportUsersWarningModal").modal("hide");
    $("#sImportNewUserFromFile").modal("show");
});

loadUsersTableDynamics = function () {

    $('#dynamic-table').dataTable({
        "aaSorting": [[4, "desc"]]
    });

    /*
     * Insert a 'details' column to the table
     */
    var nCloneTh = document.createElement('th');
    var nCloneTd = document.createElement('td');
    nCloneTd.innerHTML = '<img src="plugins/advanced-datatable/images/details_open.png">';
    nCloneTd.className = "center";

    $('#hidden-table-info thead tr').each(function () {
        this.insertBefore(nCloneTh, this.childNodes[0]);
    });

    $('#hidden-table-info tbody tr').each(function () {
        this.insertBefore(nCloneTd.cloneNode(true), this.childNodes[0]);
    });

    /*
     * Initialse DataTables, with no sorting on the 'details' column
     */
    var oTable = $('#hidden-table-info').dataTable({
        "aoColumnDefs": [
            {"bSortable": false, "aTargets": [0]}
        ],
        "aaSorting": [[1, 'asc']]
    });

    /* Add event listener for opening and closing details
     * Note that the indicator for showing which row is open is not controlled by DataTables,
     * rather it is done here
     */
    $('#hidden-table-info tbody td img').click(function () {
        var nTr = $(this).parents('tr')[0];
        if (oTable.fnIsOpen(nTr))
        {
            /* This row is already open - close it */
            this.src = "plugins/advanced-datatable/images/details_open.png";
            oTable.fnClose(nTr);
        }
        else
        {
            /* Open this row */
            this.src = "plugins/advanced-datatable/images/details_close.png";
            oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr), 'details');
        }
    });
};

loadUserActions = function () {

    //User creation start.

    $("#idCreateNewUser").click(function (event) {

        event.preventDefault();
        
        if(typeof currGroups !== 'undefined'){
            var groupArr = currGroups.split(','), tmpArr = null;
            if(groupArr.length > 1 && groupArr.length !== ($("#selectGroupForUser option").length - 1)){ //if the current group list is changed, 
                $('#selectGroupForUser')
                    .find('option')
                    .remove()
                    .end()
                    .append('<option value="0">' + $.i18n( 'select_group' ) + '</option>')
                    .val('0');  //remove all options and add infromation option
                $.each(groupArr, function(key, value) { //add the whole current options
                    tmpArr = value.split('(');
                    tmpArr = tmpArr[1].split(')')[0];
                    $('#selectGroupForUser')
                        .append($("<option></option>")
                                   .attr("value",tmpArr)
                                   .text(tmpArr)); 
                           
                });
            }
        }
        $("#sCreateNewUser").modal("show");
        document.getElementById("userCreationResult").innerHTML = "";
    });
    $("#idImportFromFile").click(function (event) {

        event.preventDefault();


        $.ajax({
            url: "/controlSmtpMailProperties",
            type: 'POST',
            success: function () {
//            alert(response);
                $("#sImportNewUserFromFile").modal("show");
//            $("#successModal").modal("show");
//            document.getElementById("alertText").innerHTML = response;
            }, error: function (response) {

                $("#ImportUsersWarningModal").modal("show");
                document.getElementById("warningText").innerHTML = response.responseText;
            },
        });

//      $("#sImportNewUserFromFile").modal("show");
        document.getElementById("importFromFileResponse").innerHTML = "";
    });

    $(document).on('click', '.editClientUser', function (event) {

        $("#changeUserAttributesModal").modal("show");

        userHolder = $(this).data("user");
        clientUserName = $(this).data("username");
        clientGroupId = $(this).data("groupid");
        console.log('clientGroupId ---> ' + clientGroupId);
        if(clientGroupId.length > 0)
            $("#selectGroupForUser2").val(clientGroupId);
        else
            $("#selectGroupForUser2").val('0');

    });

    $(document).on('click', '.deleteClientUser', function (event) {

        $("#deleteUserModal").modal("show");

        document.getElementById("deleteUserText").innerHTML = $.i18n( 'sure2delete_user' ) + $(this).data("username");

        userHolder = $(this).data("user");

    });

    $('.selectpicker-users-group').on('change', function () {
        var selected = $(this).find("option:selected").val();
        //users list from selected group
        getUsersFromGroup(this);

    });

    $('input[name="switch-animate"]').on('switchChange.bootstrapSwitch', function (event, state) {

        //UserName children[0]
        var userName = event.target.parentElement.parentElement.parentElement.parentElement.children[0].innerHTML;
        //UserRole children[1]
//                var userRole = $(event.target).parent().parent().parent().parent().children().eq(1).data("userrole");

        $.ajax({
            url: "/suspendUser",
            type: "POST",
            data: {userName: userName, isActive: state},
            success: function () {
                $("#idUsersList").click();
//                        $('input[name="switch-animate"]').bootstrapSwitch('state', !state, state);

            },
            error: function (response) {
                $(event.target).parent().parent().parent().parent().children().eq(3).children().eq(0).children().eq(0).children().eq(3).bootstrapSwitch('state', !state, !state);
                $("#ErrorModal").modal("show");
                document.getElementById("errorText").innerHTML = response.responseText;
            }

        });

    });

};

function fnFormatDetails(oTable, nTr)
{
    var aData = oTable.fnGetData(nTr);
    var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
    sOut += '<tr><td>' + $.i18n( 'rendering_engine' ) + '</td><td>' + aData[1] + ' ' + aData[4] + '</td></tr>';
    sOut += '<tr><td>' + $.i18n( 'link2source' ) + '</td><td>' + $.i18n( 'provide_link_here' ) + '</td></tr>';
    sOut += '<tr><td>' + $.i18n( 'extra_info' ) + '</td><td>' + $.i18n( 'any_further_details_here' ) + '</td></tr>';
    sOut += '</table>';

    return sOut;
}

function getUsersFromGroup(group) {

    event.preventDefault();

    $("div#idPageContent").load("/userTableFromGroup", {group: group.value}, function () {

        loadUsersTableDynamics();

        loadUserActions();

//          $("#savedStates.btn-group,#deployment.btn-group,#deleteApplication.btn-group,#preview.btn-group,#applicationSettings.btn-group").fadeOut(500);

        $('input[name="switch-animate"]').on('switchChange.bootstrapSwitch', function (event, state) {

            //UserName children[0]
            var userName = event.target.parentElement.parentElement.parentElement.parentElement.children[0].innerHTML;
            //UserRole children[1]
//                var userRole = $(event.target).parent().parent().parent().parent().children().eq(1).data("userrole");

            $.ajax({
                url: "/suspendUser",
                type: "POST",
                data: {userName: userName, isActive: state},
                success: function () {
                    $("#idUsersList").click();
//                        $('input[name="switch-animate"]').bootstrapSwitch('state', !state, state);

                },
                error: function (response) {
                    $(event.target).parent().parent().parent().parent().children().eq(3).children().eq(0).children().eq(0).children().eq(3).bootstrapSwitch('state', !state, !state);
                    $("#ErrorModal").modal("show");
                    document.getElementById("errorText").innerHTML = response.responseText;
                }

            });
        });
        buttonGroupFadeOut(500);
    });

}

// Auto generate password
$(document).on('click', '#generatePassButton', function (event) {
    event.preventDefault();
    document.getElementById("iUserPasswordError").innerHTML = "";

    $.ajax({
        url: "/autoGeneratePassword",
        type: "POST",
        data: {},
        success: function (data) {

//                $("#changeUserAttributesModal").modal("hide");
//                $("#idUsersList").click();
            $("#iUserPassword").val(data);
            $("#iRetypeUserPassword").val(data);
        },
        error: function (response) {
            $("#iUserPassword").val("newPassword");
            $("#iRetypeUserPassword").val("newPassword");
            document.getElementById("iUserPasswordError").innerHTML = response.responseText;

        }

    });

});

function clearNewUserFormParameters() {

    $("#cUserName").val('');
    $("#cUserMail").val('');
    $("#cUserPassword").val('');
    $("#cReTypePassword").val('');

}