function queryResultController($scope) {
	var ctrl = this;
	//alert('Query Result Controller: ' + ctrl.visible);
	
	
}


angular.module('todoPortal').component('queryResult', {
  templateUrl: '../views/queryResult.html',
  controller: queryResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  visible: '<',
	  handleDataChange: '&'
  }
});