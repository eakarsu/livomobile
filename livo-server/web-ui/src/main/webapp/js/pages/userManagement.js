/*** Javascript codes of application settings page. ***/
var webUserName;



$(document).on('click', '#createNewWebUserButton', function (event) {
    
    var companyList = ',';
    
    if($("#userCompanyList").val())
        $.each( $("#userCompanyList").val(), function( i, val ) {
            companyList += val + ',';
        });
    console.log('companyList -- > ' + companyList + ' -- ' + $("#userCompanyList").val());

    if ($("#cWebUserPass").val().length >= 6) {

        if ($("#createNewWebUserForm").parsley().isValid()) {
            event.preventDefault();
            $.ajax({
                url: "/createNewWebUser",
                type: "POST",
                data: {userName: $("#cWebUserName").val(), userMail: $("#cWebUserMail").val(), userPassword: $("#cWebUserPass").val(), companyList: companyList},
                success: function () {

                    document.getElementById("userCreationResult").innerHTML = "";
                    $("#sCreateNewWebUser").modal("hide");
                    $("#idWebUsersList").click();
                    $("#createNewWebUserForm").find("input, textarea").val("");

                },
                error: function (response) {

                    document.getElementById("webUserCreationResult").innerHTML = response.responseText;
                }
            });
        }
    } else {
        document.getElementById("webUserCreationResult").innerHTML = $.i18n( 'pass_must_min6' );
    }
});
//Web User mail change.
$("#submitWebUserMailChange").click(function (event) {

    if ($("#changeWebUserMailForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/modifyWebUserMail",
            type: "POST",
            data: {userName: webUserName, newUserMail: $("#iWebUserMail").val()},
            success: function () {

                document.getElementById("changeWebUserMailError").innerHTML = "";
                $("#changeWebUserAttributesModal").modal("hide");
                $("#idWebUsersList").click();
                $("#changeWebUserMailForm").find("input, textarea").val("");

            },
            error: function (response) {

                document.getElementById("changeWebUserMailError").innerHTML = response.responseText;

            }
        });
    }
});

//Web User companies change.
$("#submitWebUserCompaniesChange").click(function (event) {

    if ($("#changeWebUserCompaniesForm").parsley().isValid()) {
        event.preventDefault();
        
        var companyList = ',';
    
        if($("#userCompanyList2").val())
            $.each( $("#userCompanyList2").val(), function( i, val ) {
                companyList += val + ',';
            });

        $.ajax({
            url: "/modifyWebUserCompanies",
            type: "POST",
            data: {userName: webUserName, companyList: companyList},
            success: function () {

                $("#changeWebUserAttributesModal").modal("hide");
                $("#idWebUsersList").click();
                $("#changeWebUserCompaniesForm").find("input, textarea").val("");

            },
            error: function (response) {

                console.log('submitWebUserCompaniesChange:error --> ' + response.responseText);

            }
        });
    }
});

//Web User name change.
$("#submitWebUserNameChange").click(function (event) {

    if ($("#changeWebUserNameForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/WebUserName",
            type: "POST",
            data: {userName: webUserName, newUserName: $("#iWebUserName").val()},
            success: function () {

                document.getElementById("changeWebUserNameError").innerHTML = "";
                $("#changeWebUserAttributesModal").modal("hide");
                $("#idWebUsersList").click();
                $("#changeWebUserNameForm").find("input, textarea").val("");

            },
            error: function (response) {

                document.getElementById("changeWebUserNameError").innerHTML = response.responseText;

            }

        });


    }

});
//web User pasword change.
$("#submitWebUserPasswordChange").click(function (event) {

    if ($("#changeWebUserPasswordForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/modifyWebUserPassword",
            type: "POST",
            data: {userName: webUserName, userPassword: $("#iWebUserPassword").val()},
            success: function () {

                document.getElementById("iUserPasswordError").innerHTML = "";
                $("#changeWebUserAttributesModal").modal("hide");
                $("#idWebUsersList").click();
                $("#changeWebUserPasswordForm").find("input, textarea").val("");

            },
            error: function (response) {

                document.getElementById("iWebUserPasswordError").innerHTML = response.responseText;

            }

        });
    }
});


$("#deleteWebUserButton").click(function () {

    $.ajax({
        url: "/deleteWebUser",
        type: "POST",
        data: {userName: webUserName},
        success: function () {

            $("#deleteWebUserModal").modal("hide");

            document.getElementById("deleteWebUserError").innerHTML = "";

            $("#idWebUsersList").click();

        },
        error: function (response) {

            document.getElementById("deleteWebUserError").innerHTML = response.responseText;

        }

    });

});

loadWebUsersTableDynamics = function () {

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

loadWebUserActions = function () {

    //User creation start.
    $("#idCreateNewWebUser").click(function (event) {

        event.preventDefault();
        
        if(typeof currCompanies !== 'undefined'){
            var companyArr = currCompanies.split('::'), tmpArr = null;
            //console.log(companyArr.length + ' !== ' + $("#userCompanyList option").length);
            if(companyArr.length > 1 && companyArr.length !== ($("#userCompanyList option").length)){ //if the current company list is changed,
                $('#userCompanyList').empty().multiSelect('refresh');
                $('#userCompanyList2').empty().multiSelect('refresh');
                $.each(companyArr, function(key, value) { //add the whole current options
                    tmpArr = value.split(';');
                    //console.log(tmpArr[0] + ' --- ' + tmpArr[1]);
                    $('#userCompanyList')
                        .append($("<option></option>")
                                   .attr("value",tmpArr[0])
                                   .text(tmpArr[1]));
                    $('#userCompanyList2')
                        .append($("<option></option>")
                                   .attr("value",tmpArr[0])
                                   .text(tmpArr[1])); 
                });
                $('#userCompanyList').multiSelect('refresh');
                $('#userCompanyList2').multiSelect('refresh');
            }
        }
        $("#sCreateNewWebUser").modal("show");
        document.getElementById("webUserCreationResult").innerHTML = "";

    });

    $(document).on('click', '.editWebUser', function (event) {
        
        if(typeof currCompanies !== 'undefined'){
            var companyArr = currCompanies.split('::'), tmpArr = null;
            //console.log(companyArr.length + ' !== ' + $("#userCompanyList option").length);
            if(companyArr.length > 1 && companyArr.length !== ($("#userCompanyList option").length)){ //if the current company list is changed,
                $('#userCompanyList').empty().multiSelect('refresh');
                $('#userCompanyList2').empty().multiSelect('refresh');
                $.each(companyArr, function(key, value) { //add the whole current options
                    tmpArr = value.split(';');
                    //console.log(tmpArr[0] + ' --- ' + tmpArr[1]);
                    $('#userCompanyList')
                        .append($("<option></option>")
                                   .attr("value",tmpArr[0])
                                   .text(tmpArr[1]));
                    $('#userCompanyList2')
                        .append($("<option></option>")
                                   .attr("value",tmpArr[0])
                                   .text(tmpArr[1])); 
                });
                $('#userCompanyList').multiSelect('refresh');
                $('#userCompanyList2').multiSelect('refresh');
            }
        }

        $("#changeWebUserAttributesModal").modal("show");

        webUserName = $(this).data("username");
        
        setSelectedComp(webUserName);

    });

    $(document).on('click', '.deleteWebUser', function (event) {

        webUserName = $(this).data("username");

        $("#deleteWebUserModal").modal("show");

        document.getElementById("deleteWebUserText").innerHTML = $.i18n( 'sure2delete_user' ) + webUserName;

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