var serverURL = "http://developer.livomobile.com/";

$("#createInstanceButton").click(function(event) {
	event.preventDefault();
	$.ajax({
		url : "/LivoCloud/createInstance",
		type : "POST",
		success : function(response) {
			document.getElementById("instanceIp").innerHTML= response;
			
			
		},
		error : function(response) {
			alert("error : " +response.responseText);

		}

	});

});

$("#devPlanButton").click(function(event) {
	event.preventDefault();
	
	$( "#starter-confirm" ).dialog({
	      resizable: false,
	      height: "auto",
	      width: 400,
	      modal: true,
	      hidden: false,
	      buttons: {
	        "Activate": function() {
	        	
	        	//console.log(wUser);
	        	//console.log(currCompany);
	        	
	        	$.ajax({
	        		url : serverURL + "rest/crtComp",
	        		type : "POST",
	        		contentType : "application/json",
	        		dataType : 'json',
	        		data: currCompany,
	        		success : function(response) {

	        			console.log(response);
	        			
	        			$.ajax({
	    	        		url : serverURL + "rest/crtWuser",
	    	        		type : "POST",
	    	        		contentType : "application/json",
	    	        		dataType : 'json',
	    	        		data: wUser,
	    	        		success : function(response) {

	    	        			console.log(response);
	    	        			console.log(response.userMail);
	    	        			
	    	        			$.ajax({
	    	        				url : "/sendEmail4Complete",
	    	        				type : "POST",
	    	        				data: {email:response.userMail, free: true},
	    	        				success : function(response) {
	    	        					console.log(response);
	    	        				},
	    	        				error : function(response) {
	    	        					alert("error : " +response.responseText);

	    	        				}

	    	        			});
	    	        			
	    	        			$( "#starter-message" ).dialog({
	    	        			      modal: true,
	    	        			      width: 400,
	    	        			      buttons: {
	    	        			        Ok: function() {
	    	        			          $( this ).dialog( "close" );
	    	        			        }
	    	        			      }
	    	        			    });

	    	        		},
	    	        		error : function(response) {
	    	        			alert("error : " +response.responseText);

	    	        		}

	    	        	});
	        			
	        		},
	        		error : function(response) {
	        			alert("error : " +response.responseText);

	        		}

	        	});
	        	
	          $( this ).dialog( "close" );
	        },
	        Cancel: function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });

});

var setupOnComplete = function (){
	
	var serverURL = "http://developer.livomobile.com/";
	
	$.ajax({
		url : serverURL + "rest/crtComp",
		type : "POST",
		contentType : "application/json",
		dataType : 'json',
		data: currCompany,
		success : function(response) {

			console.log(response);
			
			$.ajax({
        		url : serverURL + "rest/crtWuser",
        		type : "POST",
        		contentType : "application/json",
        		dataType : 'json',
        		data: wUser,
        		success : function(response) {

        			console.log(response);
        			console.log(response.userMail);
        			
        			$.ajax({
        				url : "/sendEmail4Complete",
        				type : "POST",
        				data: {email:response.userMail, free: false},
        				success : function(response) {
        					console.log(response);
        				},
        				error : function(response) {
        					alert("error : " +response.responseText);

        				}

        			});
        			
        			$( "#starter-message" ).dialog({
        			      modal: true,
        			      width: 400,
        			      buttons: {
        			        Ok: function() {
        			          $( this ).dialog( "close" );
        			        }
        			      }
        			    });

        		},
        		error : function(response) {
        			alert("error : " +response.responseText);

        		}

        	});
			
		},
		error : function(response) {
			alert("error : " +response.responseText);

		}

	});
	
};
