$(document).ready(function() {
	$('[id^=datePicker')
		.datepicker({
			format: 'dd/mm/yyyy'
		});
	
	$('#crew-select').multiselect({
		buttonWidth: '100%'
	});
	$('#depot-select').multiselect({
		buttonWidth: '100%'
	});
	
	/*$('[id^=to-do').multiselect({
		buttonWidth: '100%'
	});*/
	
	/*$(".row-expander").click(function(){
	
		if($(this).hasClass("glyphicon-plus")) {
			$(this).removeClass("glyphicon-plus");
			$(this).addClass("glyphicon-minus");
		} else if ($(this).hasClass("glyphicon-minus")) {
			$(this).removeClass("glyphicon-minus");
			$(this).addClass("glyphicon-plus");
		}
	});*/
	
	/*$('[id^=save-to-do').click(function(){
		alert("Scheduling TO DO saved for the execution Package or Job Id");
	});*/
});