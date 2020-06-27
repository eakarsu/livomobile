/*** Javascript codes of groups page. ***/

/* Global variables definition */
var groupHolder;
var groupDescriptionHolder;




$(document).on('click', '#createNewGroupButton', function (event) {
    document.getElementById("groupCreationResult").innerHTML = "";

    if ($("#createNewGroupForm").parsley().isValid()) {
        event.preventDefault(); 
        $.ajax({
            url: "/createNewGroup",
            type: "POST",
            data: {groupName: $("#cGroupName").val(), groupDescription: $("#cGroupDescription").val(), groupCompanyList: "" + $("#groupCompanyList").val()},
            success: function (response) {
//                        clearNewGroupFormParameters();
                if(response.indexOf('Failed:') === -1){
                    $("#sCreateNewGroup").modal("hide");
                    $("#idUserGroups").click();
                    $("#createNewGroupForm").find("input, textarea").val("");
                }
                else
                    document.getElementById("groupCreationResult").innerHTML = '<b style="color:red">' + $.i18n( 'select_company' ) + '</b><br><br>';
                console.log(response);

            },
            error: function (response) {
//                        clearNewUserFormParameters();
                $("#idUserGroups").click();
                document.getElementById("groupCreationResult").innerHTML = response.responseText;

            }
        });
    }

    return false;
});


$(document).on('click', '.editGroup', function (event) {
    $("#changeGroupAttributesModal").modal("show");

    groupHolder = $(this).data("group");

    groupDescriptionHolder = $(this).data("description");

    $("#iGroupName").val(groupHolder);
    $("#iGroupDescription").val(groupDescriptionHolder);
    
    setSelectedGroupComp(groupHolder);

//    console.log(groupHolder + groupDescriptionHolder);

});

$(document).on('click', '.deleteGroup', function (event) {

//        alert("Delete Group.");
    $("#deleteGroupModal").modal("show");
//
    document.getElementById("deleteGroupText").innerHTML = $.i18n( 'sure2delete_group' ) + $(this).data("group");
//
    groupHolder = $(this).data("group");

});


$("#updateGroupButton").click(function (event) {
    var newGroupName = $("#iGroupName").val();
    var newGroupDescription = $("#iGroupDescription").val();

    $.ajax({
        url: "/updateGroup",
        type: "POST",
        data: {groupName: groupHolder, newGroupName: newGroupName, newGroupDescription: newGroupDescription, groupCompanyList: "" + $("#groupCompanyList2").val()},
        success: function () {
            $("#changeGroupAttributesModal").modal("hide");

            $("div#idPageContent").load("/groupTable", {"": ""}, function () {

                loadGroupsTableDynamics();

                loadGroupActions();

                buttonGroupFadeOut(500);
                
            });

        },
        error: function (response) {
            $("#changeGroupAttributesModal").modal("hide");

            $("#ErrorModal").modal("show");

            document.getElementById("errorText").innerHTML = response.responseText;
        }
    });
});

$("#deleteGroupButton").click(function (event) {
    var newGroupName = $("#iGroupName").val();
    var newGroupDescription = $("#iGroupDescription").val();
     console.log('deleteGroupButton ---> ' + groupHolder);

    $.ajax({
        url: "/deleteGroup",
        type: "POST",
        data: {groupName: groupHolder},
        success: function () {
            $("#deleteGroupModal").modal("hide");

            $("div#idPageContent").load("/groupTable", {"": ""}, function () {

                loadGroupsTableDynamics();

                loadGroupActions();

                buttonGroupFadeOut(500);
                
            });

        },
        error: function (response) {
            $("#deleteGroupModal").modal("hide");
            $("#ErrorModal").modal("show");

            document.getElementById("errorText").innerHTML = response.responseText;

        }

    });


});

loadGroupsTableDynamics = function () {

    $('#dynamic-table-groups').dataTable({
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


loadGroupActions = function () {
    //User creation start.
    $("#idCreateNewGroup").click(function (event) {

        event.preventDefault();
        if(typeof currCompanies !== 'undefined'){
            var companyArr = currCompanies.split('::'), tmpArr = null;
            //console.log(companyArr.length + ' !== ' + $("#groupCompanyList option").length);
            if(companyArr.length > 1 && companyArr.length !== ($("#groupCompanyList option").length)){ //if the current company list is changed,
                $('#groupCompanyList').empty().multiSelect('refresh');
                $('#groupCompanyList2').empty().multiSelect('refresh');
                $.each(companyArr, function(key, value) { //add the whole current options
                    tmpArr = value.split(';');
                    //console.log(tmpArr[0] + ' --- ' + tmpArr[1]);
                    $('#groupCompanyList')
                        .append($("<option></option>")
                                   .attr("value",tmpArr[0])
                                   .text(tmpArr[1]));
                    $('#groupCompanyList2')
                        .append($("<option></option>")
                                   .attr("value",tmpArr[0])
                                   .text(tmpArr[1])); 
                });
                $('#groupCompanyList').multiSelect('refresh');
                $('#groupCompanyList2').multiSelect('refresh');
            }
        }
        $("#createNewGroupForm").find("input, textarea").val("");
        $("#sCreateNewGroup").modal("show");
        document.getElementById("userCreationResult").innerHTML = "";
    });

};

