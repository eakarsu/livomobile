/*** Javascript codes of application settings page. ***/
var appId = "", timeUUID = "", deployId = -1;

//activate the selected deployment
$("#activateDeploymentButton").click(function (event) {

    console.log('appId: ' + appId + ' - timeUUID: ' + timeUUID + ' - deployId: ' + deployId);
    $.ajax({
        url: "/activateDeployment",
        type: "POST",
        data: {appId: appId, timeUUID: timeUUID, deployId: deployId},
        success: function (response) {
            if(response === "Succesfully activated."){
                $("#activateDeploymentModal").modal("hide");
                document.getElementById("activateDeploymentError").innerHTML = "";
                main_list.click();
            }
            else{
                document.getElementById("activateDeploymentError").innerHTML = '<b style="color:red">' + response + ' So, it can not be activated.</b>';
            }
                
        },
        error: function (response) {
            console.log(response);
            document.getElementById("activateDeploymentError").innerHTML = response.responseText;
        }

    });
});

$("#delDeploymentButton").click(function () {
    console.log('appId: ' + appId + ' - timeUUID: ' + timeUUID);
    $.ajax({
        url: "/delDeployment",
        type: "POST",
        data: {appId: appId, timeUUID: timeUUID, deployId: deployId},
        success: function (response) {
            if(response === "Succesfully deleted."){
                $("#delDeploymentModal").modal("hide");
                document.getElementById("delDeploymentError").innerHTML = "";
                main_list.click();
            }
            else{
                document.getElementById("delDeploymentError").innerHTML = '<b style="color:red">' + response + ' So, it can not be deleted.</b>';
            }
                
        },
        error: function (response) {
            console.log(response);
            document.getElementById("delDeploymentError").innerHTML = response.responseText;
        }

    });

});

loadDeploymentTableDynamics = function () {

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

loadDeploymentActions = function () {
    
    $('input[type="checkbox"]').bootstrapSwitch();

    $(document).on('click', '.activateDeployment', function (event) {
        
        deployId = $(this).data("deployid");
        appId = $(this).data("appname");
        timeUUID = $(this).data("timeuuid");
        console.log('activateDeployment::appId: ' + appId + ' - timeUUID: ' + timeUUID);
        
        document.getElementById("activateDeploymentError").innerHTML = "";
        $("#activateDeploymentModal").modal("show");

        document.getElementById("activateDeploymentText").innerHTML = $.i18n( 'sure2activate_deployment', deployId);

    });

    $(document).on('click', '.deleteDeployment', function (event) {

        deployId = $(this).data("deployid");
        appId = $(this).data("appname");
        timeUUID = $(this).data("timeuuid");
        console.log('deleteDeployment::appId: ' + appId + ' - timeUUID: ' + timeUUID);
        
        document.getElementById("delDeploymentError").innerHTML = "";
        $("#delDeploymentModal").modal("show");

        document.getElementById("delDeploymentText").innerHTML = $.i18n( 'sure2delete_deployment', deployId);

    });

};
