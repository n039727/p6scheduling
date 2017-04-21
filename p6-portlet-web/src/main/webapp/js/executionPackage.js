
function executionPackageResultController($scope, $http,ModalService) {
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

    ctrl.show = function(wo) {
		console.log('Create Exc called with WO: ' + JSON.stringify(wo));
        ModalService.showModal({
            templateUrl: '../views/executionPackagePopup.html',
            controller: "ComplexController",
            inputs: {
                wo: wo,
              }
            
        }).then(function(modal) {
            modal.element.modal();
            modal.close.then(function(result) {
                $scope.complexResult  = "Name: " + result.name + ", age: " + result.age;
            });
        });
    };
	
}

app.controller('ComplexController', [
	  '$scope', '$element', 'wo', 'close', 
	  function($scope, $element, wo, close) {
			console.log('Create Exc called with WO in popup: ' + JSON.stringify(wo));
		  
	  $scope.wo = wo;
	  $scope.age = null;
	  
	  //  This close function doesn't need to use jQuery or bootstrap, because
	  //  the button has the 'data-dismiss' attribute.
	  $scope.close = function() {
	 	  close({
	      wo: $scope.wo,
	      age: $scope.age
	    }, 50); // close, but give 500ms for bootstrap to animate
	  };

}]);

angular.module('todoPortal').component('executionPackageResult', {
  templateUrl: '../views/executionPackage.html',
  controller: executionPackageResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  handleDataChange: '&'
  }
});