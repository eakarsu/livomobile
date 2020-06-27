/*** Javascript codes of application settings page. ***/
var recordId = "";

$(document).on('click', '#createNewCompanyButton', function (event) {

    if ($("#cCompanyPass").val().length >= 4) {

        if ($("form#createNewCompanyForm").parsley().isValid()) {
            
            event.preventDefault();
            $.ajax({
                url: "/createNewCompany",
                type: "POST",
                data: {companyId: $("#cCompanyID").val(), companyTitle: $("#cCompanyTitle").val(), loginPermSwitch: $('#loginPermSwitch').bootstrapSwitch('state'), companyPassword: $("#cCompanyPass").val()},
                success: function () {

                    document.getElementById("companyCreationResult").innerHTML = "";
                    $("#sCreateNewCompany").modal("hide");
                    $("#idCompanies").click();
                    $("form#createNewCompanyForm").find("input, textarea").val("");

                },
                error: function (response) {

                    document.getElementById("companyCreationResult").innerHTML = response.responseText;
                }
            });
            
        }
    } else {
        document.getElementById("companyCreationResult").innerHTML = "Company password length must be minimum 4 characters.";
    }
});

//update Company Info
$("#updateCompanyButton").click(function (event) {

    if ($("form#updateCompanyForm").parsley().isValid()) {
        event.preventDefault();

        $.ajax({
            url: "/updateCompany",
            type: "POST",
            data: {recordId: recordId, companyId: $("#uCompanyID").val(), companyTitle: $("#uCompanyTitle").val(), loginPermSwitch: $('#loginPermSwitch2').bootstrapSwitch('state'), companyPassword: $("#uCompanyPass").val()},
            success: function () {

                document.getElementById("companyUpdateResult").innerHTML = "";
                $("#updateCompanyInfo").modal("hide");
                $("#idCompanies").click();
                $("form#updateCompanyForm").find("input, textarea").val("");

            },
            error: function (response) {

                document.getElementById("changeWebUserMailError").innerHTML = response.responseText;

            }
        });
    }
});

$("#deleteCompanyButton").click(function () {

    $.ajax({
        url: "/deleteCompany",
        type: "POST",
        data: {recordId: recordId},
        success: function (response) {
            if(response === "Succesfully deleted."){
                $("#deleteCompanyModal").modal("hide");
                document.getElementById("deleteCompanyError").innerHTML = "";
                $("#idCompanies").click();
            }
            else{
                document.getElementById("deleteCompanyError").innerHTML = '<b style="color:red">' + response + ' So, it can not be deleted.</b>';
            }
                
        },
        error: function (response) {
            console.log(response);
            document.getElementById("deleteCompanyError").innerHTML = response.responseText;
        }

    });

});

loadCompaniesTableDynamics = function () {

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
    
    $('input[name="loginPermSwitch"]').bootstrapSwitch();
    $('input[name="loginPermSwitch2"]').bootstrapSwitch();

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

loadCompaniesActions = function () {

    //User creation start.
    $("#idCreateNewCompany").click(function (event) {

        event.preventDefault();
        $("#sCreateNewCompany").modal("show");
        document.getElementById("companyCreationResult").innerHTML = "";

    });

    $(document).on('click', '.editCompany', function (event) {
        
        var form = $("form#updateCompanyForm");
        form.find("input[name='uCompanyID']").val($(this).data("companyid"));
        form.find("input[name='uCompanyTitle']").val($(this).data("ctitle"));
        form.find("input[name='loginPermSwitch2']").bootstrapSwitch('state', $(this).data("loginp"));

        $("#updateCompanyInfo").modal("show");

        recordId = $(this).data("recordid");

    });

    $(document).on('click', '.deleteCompany', function (event) {

        var companyId = $(this).data("companyid");
        recordId = $(this).data("recordid");
        
        document.getElementById("deleteCompanyError").innerHTML = "";
        $("#deleteCompanyModal").modal("show");

        document.getElementById("deleteCompanyText").innerHTML = $.i18n( 'sure2delete_company' ) + companyId;

    });

};
