
function executionPackageResultController($scope, ModalService, userAccessService) {
	var ctrl = this;
	
	// Authorization implementation
	ctrl.isReadOnly = false;
	if (userAccessService.isAuthEnabled()) {
		if (!userAccessService.hasUpdateableAccess(ctrl.functionId)) {
			ctrl.isReadOnly = true;
		}
	}
	
	ctrl.selectedExecPckg = [];
	ctrl.errorMsgVisiable = false;
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
					
				ctrl.selectedExecPckg.push({leadCrew:wo.crewNames,crewNames:wo.crewNames, workOrders:[wo.workOrders[0]],scheduleDate:wo.scheduleDate,executionPckgName:wo.exctnPckgName});
			}
			
				
		} else {
			var index = findCheckedWO(ctrl.selectedExecPckg, wo.workOrders[0]);
			if (index > -1) {
				ctrl.selectedExecPckg.splice(index, 1);
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
	

    ctrl.showPopup = function(wo) {
    	var isValid = false;
    	if(ctrl.selectedExecPckg.length < 2){
    		isValid = false;
    		ctrl.errorMsgVisiable = true;
    	}else{
    		isValid = true;
    		ctrl.errorMsgVisiable = false;
    		
    	}
    	if(isValid){
	        ModalService.showModal({
	            templateUrl: '../views/executionPackagePopup.html',
	            controller: "executionPkgPopupController",
	            inputs: {
	                wo: wo
	              }
	            
	        }).then(function(modal) {
	            modal.element.modal();
	            modal.close.then(function(result) {
					if (result.status === 'SUCCESS') {
						ctrl.handleDataChange({event:{eventId:'EXECUTION_PKG_CREATED', eventData:result.data}});
					}
	            });
	        });
    	}
    };
	
}

app.controller('executionPkgPopupController', [
	  '$scope', '$element', 'wo', 'close','restTemplate', 
	  function($scope, $element, wo, close, restTemplate) {
			$scope.createExecPkgWOs = [];
			$scope.woList =[];
			$scope.leadCrewList =[];
			if(wo){
				wo.sort(function(a,b) {return (a.executionPckgName > b.executionPckgName) ? 1 : ((b.executionPckgName > a.executionPckgName) ? -1 : 0);} ); 				
				for(i=0; i<wo.length;i++) {  
					$scope.woList.push(wo[i].workOrders[0]);
					if ($scope.leadCrewList.indexOf(wo[i].leadCrew) == -1){
						$scope.leadCrewList.push(wo[i].leadCrew);
					}
					$scope.createExecPkgWOs.push({workOrderId:wo[i].workOrders[0],scheduleDate:wo[i].scheduleDate,crewNames:wo[i].crewNames });
					$scope.scheduleDate = wo[i].scheduleDate;
				}
			}
			
			$scope.wo = $scope.woList;
			$scope.leadCrews = $scope.leadCrewList;
	  
	  //  This close function doesn't need to use jQuery or bootstrap, because
	  //  the button has the 'data-dismiss' attribute.
	  $scope.cancel = function() {
			close({
					status: 'CANCELLED'
				  }, 500); // close, but give 500ms for bootstrap to animate
		  
	  };
	  $scope.saveExecutionPackage = function() {
		  $scope.createExecPkgReq = {workOrders:$scope.createExecPkgWOs,leadCrew:$scope.selectedLeadCrew};
			var req = {
				 method: 'POST',
				 url: '/p6-portal/web/executionpackage/createOrUpdate',
				 headers: {
				   'Content-Type': 'application/json'
				 },
				 data: JSON.stringify($scope.createExecPkgReq)
			};
			restTemplate.callService(req, function (response) {
				close({
					status: 'SUCCESS',
					data: {
						exctnPckgName: response.data.exctnPckgName, 
						workOrders:$scope.wo, 
						leadCrew:$scope.selectedLeadCrew, 
						crewNames: response.data.crewNames,
						crewAssigned: $scope.leadCrewList,
						scheduleDate: $scope.scheduleDate,
						toDoItems:[]
						}
				}, 500); // close, but give 500ms for bootstrap to animate
		  
			}, null);
			
		 	
	  };

}]);

angular.module('todoPortal').component('executionPackageResult', {
  templateUrl: '../views/executionPackage.html',
  controller: executionPackageResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  handleDataChange: '&',
	  functionId: '<'
  }
});