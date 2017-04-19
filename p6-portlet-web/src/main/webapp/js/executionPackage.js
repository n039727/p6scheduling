function executionPackageResultController($scope, $http) {
	var ctrl = this;
	
	console.log('data received: ' + JSON.stringify(ctrl.data));
	
	ctrl.selectAll = function(){
		var status = !ctrl.selectedAll;
		angular.forEach(ctrl.data,function(wo){
			wo.selected = status;
		});
	};
	
	ctrl.checkIfAllSelected = function(){
	};

	
}


angular.module('todoPortal').component('executionPackageResult', {
  templateUrl: '../views/executionPackage.html',
  controller: executionPackageResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  handleDataChange: '&'
  }
});