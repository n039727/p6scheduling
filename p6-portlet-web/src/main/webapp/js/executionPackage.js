
function executionPackageResultController($scope, $http,ModalService) {
	var ctrl = this;
	ctrl.selectedExecPckg = [];
	console.log('data received in execution package: ' + JSON.stringify(ctrl.data));
	
	ctrl.addRemoveWorkOrder = function($event, wo){
		var cb = $event.target;
		if (cb.checked) {
			if (findCheckedWO(ctrl.selectedExecPckg, wo.workOrders[0]) == -1) {
				if(!ctrl.selectedExecPckg)
					ctrl.selectedExecPckg = [];
					
				ctrl.selectedExecPckg.push({leadCrew:wo.leadCrew, workOrders:[wo.workOrders[0]]});
				console.log('WO after adding execution pckg: ' + JSON.stringify(ctrl.selectedExecPckg));
			}
			
				
		} else {
			var index = findCheckedWO(ctrl.selectedExecPckg, wo.workOrders[0]);
			if (index > -1) {
				ctrl.selectedExecPckg.splice(index, 1);
				console.log('WO after removing checked: ' + JSON.stringify(ctrl.selectedExecPckg));
			}
		}
		
	};
	
	function findCheckedWO(selectedList, woId) {
		if (selectedList) {
			for(i=0; i < selectedList.length; i++) {
				if (selectedList[i].workOrders == woId) {
					return i;
				}
			}
		}
		return -1;
	}
	
	ctrl.selectedLabelList = [];
	ctrl.isSelectAll = function(){
/*    	console.log('select checkbox:' + JSON.stringify(ctrl.data.length));
		  if(ctrl.selectedAll){
			  ctrl.selectedAll = true;
			  alert("select checkbox1:" + ctrl.data.length);
		    for(var i=0;i<ctrl.data.length;i++){
		    	console.log('select checkbox2:' + JSON.stringify(ctrl.data[i].workOrders[0]));
		     ctrl.selectedLabelList.push(ctrl.data[i].workOrders[0]);  
		    }
		  }
		  else{
			  ctrl.selectedAll = false;
		  }	
		  angular.forEach(ctrl.data, function (wo) {
			    wo.selected = ctrl.selectedAll;
			  });	*/	 
		console.log('ctrl.selectedAll:' + JSON.stringify(ctrl.selectedAll));
		var status = ctrl.selectedAll;
		angular.forEach(ctrl.data,function(wo){
			wo.selected = status;
		});
		
	};
	
	ctrl.checkIfAllSelected = function(){
	};

    ctrl.show = function(wo) {
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
			$scope.woList =[];
			$scope.leadCrewList =[];
			if(wo){
				for(i=0; i<wo.length;i++) {  
					$scope.woList.push(wo[i].workOrders);
					$scope.leadCrewList.push(wo[i].leadCrew);
				}
			}

			console.log('$scope.woList in popup: ' + JSON.stringify($scope.woList));
			
			$scope.wo = $scope.woList;
			$scope.age = null;
	  
	  //  This close function doesn't need to use jQuery or bootstrap, because
	  //  the button has the 'data-dismiss' attribute.
	  $scope.cancel = function() {
			console.log('called close() with WO in popup: ' );
		  
	 	  close({
	      wo: $scope.wo,
	      age: $scope.age
	    }, 50); // close, but give 500ms for bootstrap to animate
	 	  return true;
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