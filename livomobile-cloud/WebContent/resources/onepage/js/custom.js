$(document).ready(function() {
	Layout.init();
	RevosliderInit.initRevoSlider();

//	$("#signUpButton").click(function(event) {
//		alert("Handler for .submit() called.");
//		event.preventDefault();
//		$.ajax({
//			url : "/signUp",
//			type : "POST",
//			data : {
//				name : "asdasd",
//				email : "asdsadsd"
//			},
//			success : function(response) {
//				alert("success" + response);
//				// $('input[name="switch-animate"]').bootstrapSwitch('state',
//				// !state, state);
//
//			},
//			error : function(response) {
//				alert("error");
//
//			}
//
//		});
//	});
//	
//	 $("#signUpButton").submit(function (event) {
//		 alert("signUpButton submited. ");
//	        //disable the default form submission
//	        event.preventDefault();
//	        //grab all form data  
//	        var formData = new FormData($(this)[0]);
//
//	        $.ajax({
//	            url: '/signUp',
//	            type: 'POST',
//	            data: formData,
//	            async: false,
//	            contentType: false,
//	            processData: false,
//	            success: function (returndata) {
//	            	 alert("success");     
//	            },
//	            error: function (response) {
//	            	alert("error");
// 
//	            }
//	        });
//
//	        return false;
//	    });
//	

	$("#professionalPlanButton").click(function(event) {
		alert("professionalPlanButton called.");
		event.preventDefault();
		// $.ajax({
		// url : "/selectPlan",
		// type : "POST",
		// data : {
		// name : "asdasd",
		// email : "asdsadsd"
		// },
		// success : function(response) {
		// alert("success" + response);
		// // $('input[name="switch-animate"]').bootstrapSwitch('state',
		// // !state, state);
		//
		// },
		// error : function(response) {
		// alert("error");
		//
		// }
		//
		// });
	});

});
