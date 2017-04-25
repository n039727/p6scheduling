
function executionPackageResultController($scope, $http,ModalService) {
	var ctrl = this;
	ctrl.selectedExecPckg = [];
	console.log('data received in execution package: ' + JSON.stringify(ctrl.data));
	
	ctrl.addRemoveWOOnSelectAll = function($event, wo){
		for(var i=0;i<wo.length; i++){
			ctrl.addRemoveWorkOrder($event,wo[i]);
		}
	};
	
	ctrl.addRemoveWorkOrder = function($event, wo){
		var cb = $event.target;
		if (cb.checked) {
			if (findCheckedWO(ctrl.selectedExecPckg, wo.workOrders[0]) == -1) {
				if(!ctrl.selectedExecPckg)
					ctrl.selectedExecPckg = [];
					
				ctrl.selectedExecPckg.push({leadCrew:wo.leadCrew, workOrders:[wo.workOrders[0]],scheduleDate:wo.scheduleDate});
				console.log('WO after adding execution pckg: ' + JSON.stringify(ctrl.selectedExecPckg));
			}
			
				
		} else {
			var index = findCheckedWO(ctrl.selectedExecPckg, wo.workOrders[0]);
			if (index > -1) {
				ctrl.selectedExecPckg.splice(index, 1);
				console.log('WO after removing checked: ' + JSON.stringify(ctrl.selectedExecPckg));
			}
			ctrl.selectedAll = false;
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
	
	ctrl.isSelectAll = function(){
		var status = ctrl.selectedAll;
		angular.forEach(ctrl.data,function(wo){
			wo.selected = status;
		});
		
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
				console.log('Result returned from modal:' + JSON.stringify(result));
            });
        });
    };
	
}

app.controller('ComplexController', [
	  '$scope', '$element', 'wo', 'close','$http', 
	  function($scope, $element, wo, close,$http) {
			console.log('Create Exc called with WO in popup: ' + JSON.stringify(wo));
			
			$scope.createExecPkgWOs = [];
			$scope.woList =[];
			$scope.leadCrewList =[];
			if(wo){
				for(i=0; i<wo.length;i++) {  
					$scope.woList.push(wo[i].workOrders[0]);
					$scope.leadCrewList.push(wo[i].leadCrew);
					$scope.createExecPkgWOs.push({workOrderId:wo[i].workOrders[0],scheduleDate:wo[i].scheduleDate,crewAssigned:wo[i].leadCrew });
				}
			}

			console.log('$scope.woList in popup: ' + JSON.stringify($scope.woList));
			console.log('$scope.leadCrewList in popup: ' + JSON.stringify($scope.leadCrewList));
			console.log('$scope.createExecPkgWOs in popup: ' + JSON.stringify($scope.createExecPkgWOs));
			
			$scope.wo = $scope.woList;
			$scope.leadCrews = $scope.leadCrewList;
	  
	  //  This close function doesn't need to use jQuery or bootstrap, because
	  //  the button has the 'data-dismiss' attribute.
	  $scope.cancel = function() {
			console.log('$scope.wo in close: ' + JSON.stringify($scope.wo));
	 	  close({
	      wo: $scope.wo
	    }, 500); // close, but give 500ms for bootstrap to animate
	 	  //return true;
	  };
	  $scope.saveExecutionPackage = function() {
			console.log('$scope.createExecPkgWOs in saveExecutionPackage: ' + JSON.stringify($scope.createExecPkgWOs));
		  $scope.createExecPkgReq = {createDTO:$scope.createExecPkgWOs,leadCrew:$scope.selectedLeadCrew};
			console.log('Save execution package called with createExecPkgReq: ' + JSON.stringify($scope.createExecPkgReq));
			var req = {
				 method: 'POST',
				 url: '/p6-portal-service/scheduler/saveExecutionPackages',
				 headers: {
				   'Content-Type': 'application/json'
				 },
				 data: JSON.stringify($scope.createExecPkgReq)
			};
			$http(req).then(function (response) {
				console.log("Received data from server");
				//$scope.fetchedData = response.data;
				console.log("Data for execution package from server: " + JSON.stringify(response.data));
			});
		 	close({
			      wo: $scope.wo
			 }, 500); // close, but give 500ms for bootstrap to animate
		  
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