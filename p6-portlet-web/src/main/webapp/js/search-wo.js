$(document).ready(function() {
	$('#datePicker')
		.datepicker({
			format: 'mm/dd/yyyy'
		});
	
	$('#crew-select').multiselect();
	
	$('[id^=to-do').multiselect();
	
	$(".row-expander").click(function(){
	
		if($(this).hasClass("glyphicon-plus")) {
			$(this).removeClass("glyphicon-plus");
			$(this).addClass("glyphicon-minus");
		} else if ($(this).hasClass("glyphicon-minus")) {
			$(this).removeClass("glyphicon-minus");
			$(this).addClass("glyphicon-plus");
		}
	});
	
});