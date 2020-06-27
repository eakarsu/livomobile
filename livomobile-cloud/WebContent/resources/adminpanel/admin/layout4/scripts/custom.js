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
	    	        					var platformType = response.trim();
	    	        					console.log(platformType);
	    	        					if(platformType == 'Starter'){
	    	        						$("#devPlanButton").val('Go to Developer Site');
	    	        						$("#devPlanButton").unbind( "click" );
	    	        						$("#devPlanButton").click(function(event) {
	    	        							event.preventDefault();
	    	        							window.location.href = "http://developer.livomobile.com";
	    	        						});
	    	        						$('#starter_head').children('h3').html('Starter Plan <img src="' + imgSrc + '" alt="Purchased">');
	    	        					}
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
        					var platformType = response.trim();
        					console.log(platformType);
        					if(platformType == 'Monthly'){
        						$("#proPlanButton").val('Go to Developer Site');
        						$("#proPlanButton").click(function(event) {
        							event.preventDefault();
        							window.location.href = "http://developer.livomobile.com";
        						});
        						$('#monthly_head').children('h3').html('Monthly Plan <img src="' + imgSrc + '" alt="Purchased">'); //<img src="<c:url value="/resources/adminpanel/admin/layout4/img/ok.png"/>" alt="Purchased">
        					}
        					else
        					if(platformType == 'Annual'){
        						$("#enterprisePlanButton").val('Go to Developer Site');
        						$("#enterprisePlanButton").click(function(event) {
        							event.preventDefault();
        							window.location.href = "http://developer.livomobile.com";
        						});
        						$('#annual_head').children('h3').html('Annual Plan <img src="' + imgSrc + '" alt="Purchased">'); //<img src="<c:url value="/resources/adminpanel/admin/layout4/img/ok.png"/>" alt="Purchased">
        					}
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
			
		},
		error : function(response) {
			alert("error : " +response.responseText);

		}

	});
	
};
