function materialRequisitionResultController($scope, restTemplate) {
	var ctrl = this;
	console.log('data received in material req.: ' + JSON.stringify(ctrl.data));
	
	ctrl.toggleExpansion  = function($event, wo) {
		var button = $event.target;
		
		if($('#'+button.id).hasClass("glyphicon-plus")) {
			$('#'+button.id).removeClass("glyphicon-plus");
			$('#'+button.id).addClass("glyphicon-minus");
		} else if ($('#'+button.id).hasClass("glyphicon-minus")) {
			$('#'+button.id).removeClass("glyphicon-minus");
			$('#'+button.id).addClass("glyphicon-plus");
		}
		ctrl.savedMsgVisible = false;
	};
	
}

angular.module('todoPortal').component('materialRequisitionResult', {
  templateUrl: '../views/materialRequisition.html',
  controller: materialRequisitionResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  metadata: '<',
	  handleDataChange: '&'
  }
});