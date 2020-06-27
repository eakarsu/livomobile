$(document).ready(function() {
	console.log("Metronic ready");
	Metronic.init(); // init metronic core componets
	Layout.init(); // init layout
	Demo.init(); // init demo features
	Index.init(); // init index page
	Tasks.initDashboardWidget(); // init tash dashboard widget

});




$(document).on('click', '#dashboardPage', function (event) {
	$(".page-content-main").load("dashboard", {
		"" : ""
	}, function() {
//		console.log("load dashboard");
		// $(reloader());
		// location.reload();
	});

});

$(document).on('click', '#licensingPage', function (event) {
	$(".page-content-main").load("licensing", {
		"" : ""
	}, function() {
//		console.log("load dashboard");
		// $(reloader());
		// location.reload();
	});

});

$(document).on('click', '#termsofUseVerifiedButton', function (event) {
	event.preventDefault();
	$.ajax({
		url : "/termsVerified",
		type : "POST",
		data : {
			name : "asdasd",
			email : "asdsadsd"
		},
		success : function(response) {
			$(".page-content-main").load("termsVerified", {
				"" : ""
			}, function() {
//				console.log("load dashboard");
		 
			});

		},
		error : function(response) {
			alert("error");

		}

	});
});
