/*** Javascript codes of application settings page. ***/
var companyId = "";
var userPrincipal = "";
var deviceId = "";

$("#deleteDeviceButton").click(function () {

    $.ajax({
        url: "/deleteDevice",
        type: "POST",
        data: {companyId: companyId, userPrincipal: userPrincipal, deviceId: deviceId},
        success: function () {

            $("#deleteDeviceModal").modal("hide");

            document.getElementById("deleteDeviceError").innerHTML = "";

            $("#idDevices").click();

        },
        error: function (response) {

            document.getElementById("deleteDeviceError").innerHTML = response.responseText;

        }

    });

});

loadDevicesTableDynamics = function () {

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

loadDevicesActions = function () {

    $(document).on('click', '.deleteDevice', function (event) {

        companyId = $(this).data("companyid");
        userPrincipal = $(this).data("userprincipal");
        deviceId = $(this).data("deviceid");
        console.log('companyId ---> ' + companyId);

        $("#deleteDeviceModal").modal("show");

        document.getElementById("deleteDeviceText").innerHTML = $.i18n( 'sure2delete_device', deviceId, companyId, userPrincipal);

    });

};
