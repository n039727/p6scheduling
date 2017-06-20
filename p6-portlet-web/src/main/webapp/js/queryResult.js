function queryResultController($scope) {
	var ctrl = this;
	ctrl.onDataChange = function(event) {
		ctrl.handleDataChange({event:event});
	}
}


angular.module('todoPortal').component('queryResult', {
  templateUrl: '../views/queryResult.html',
  controller: queryResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  metadata: '<',
	  visible: '<',
	  handleDataChange: '&'
  }
});