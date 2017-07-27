function schedulingToDoResultController($scope, restTemplate, userAccessService) {
	var ctrl = this;
	
	// Authorization implementation
	ctrl.isReadOnly = false;
	if (userAccessService.isAuthEnabled()) {
		if (!userAccessService.hasUpdateableAccess(ctrl.functionId)) {
			ctrl.isReadOnly = true;
		}
	}
	
	ctrl.toggleExpansion  = function($event, wo) {
		var button = $event.target;
		
		if($('#'+button.id).hasClass("glyphicon-plus")) {
			$('#'+button.id).removeClass("glyphicon-plus");
			$('#'+button.id).addClass("glyphicon-minus");
			ctrl.fetchToDoAgainstWO(wo);
		} else if ($('#'+button.id).hasClass("glyphicon-minus")) {
			$('#'+button.id).removeClass("glyphicon-minus");
			$('#'+button.id).addClass("glyphicon-plus");
		}
		wo.savedMsgVisible = false;
		
	};
	
	ctrl.todoGrp1 = [];
	ctrl.todoGrp2 = [];
	ctrl.schedulingToDoList = [];
	ctrl.depotToDoList = [];
	
	if(ctrl.metadata.todoList){
		for (i=0; i<ctrl.metadata.todoList.length;i++) {
			if (ctrl.metadata.todoList[i].typeId == 1) {
				ctrl.schedulingToDoList.push(ctrl.metadata.todoList[i].toDoName);
			} else {
				ctrl.depotToDoList.push(ctrl.metadata.todoList[i].toDoName);
			}
		}
		
		for(i=0; i<ctrl.schedulingToDoList.length;i++) {  
			if (i < ctrl.schedulingToDoList.length/2) 
				ctrl.todoGrp1.push(ctrl.schedulingToDoList[i]);
			else
				ctrl.todoGrp2.push(ctrl.schedulingToDoList[i]);
		}
	}
	
		
	ctrl.addRemoveTodo = function ($event, wo, todo) {
		
			var cb = $event.target;
			if (cb.checked) {
				wo.savedMsgVisible = false;
				if (findToDo(wo.toDoItems, todo) == -1) {
					if(!wo.toDoItems)
						wo.toDoItems = [];
						
					wo.toDoItems.push({toDoName:todo, workOrders:[wo.workOrders[0]]});
				}
				if (angular.isDefined(wo.workOrderIdDisplayArray) 
						&& wo.workOrderIdDisplayArray.length > 0) {
					wo.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, todo)] = [wo.workOrderIdDisplayArray[0]];	
				}
			} else {
				wo.savedMsgVisible = false;
				var index = findToDo(wo.toDoItems, todo);
				if (index > -1) {
					wo.toDoItems.splice(index, 1);
				}
				wo.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, todo)] = [];
			}
	};
	
	function findToDo(toDoList, todoName) {
		if (toDoList) {
			for(i=0; i < toDoList.length; i++) {
				if (toDoList[i].toDoName === todoName) {
					return i;
				}
			}
		}
		return -1;
	}
	
	ctrl.calculateEnable = function(wo, todo) {
		if (wo.toDoItems) {
			for(i=0; i < wo.toDoItems.length; i++) {
				if (wo.toDoItems[i].toDoName == todo) {
					return true;
				}
			}
		}
		return false;
	};

	ctrl.saveToDo = function(wo){
		// populate updated to do assignments
		if (wo && wo.toDoItems) {
			for (var i = 0; i< wo.toDoItems.length; i++) {
				var boundWorkOrders = wo.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, wo.toDoItems[i].toDoName)];
				if (boundWorkOrders) {
					if (boundWorkOrders.indexOf('ALL') > -1) {
						boundWorkOrders = wo.workOrders;
					}
					wo.toDoItems[i].workOrders = boundWorkOrders;
				}
			}
		}
//		console.log('Save To Do called with WO: ' + JSON.stringify(wo));
		var req = {
			 method: 'POST',
			 url: '/p6-portal/web/scheduler/saveWorkOrder',
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: JSON.stringify(wo)
			 
		};
		restTemplate.callService(req, function (response) {
			$scope.fetchedData = response.data;
			if(angular.isDefined(wo.exctnPckgName) && wo.exctnPckgName !== ""){
				wo.successSavedMsg = "Package has been saved successfully";
			}else{
				wo.successSavedMsg = "Work order task has been saved successfully";
			}
			wo.actioned = 'Y';
			wo.savedMsgVisible = true;
			
		}, null);
		
		ctrl.handleDataChange({event:{eventId:'SCHEDULING_TODO_SAVED'}});
	};
	
	
	ctrl.fetchToDoAgainstWO = function(wo) {
		serviceUrl = "/p6-portal/web/scheduler/fetchWOForAddUpdateToDo";
		var query = {};
		if (wo.exctnPckgName) {
			query.execPckgName = wo.exctnPckgName;
		} else {
			query.workOrderId = wo.workOrders[0];
		}
		var req = {
				method: 'POST',
				url: serviceUrl,
				headers: {
				   'Content-Type': 'application/json'
				},
				data: JSON.stringify(query)

			};
		restTemplate.callService(req, function (response) {
			wo.toDoItems = [];
			wo.schedulingToDoComment = "";
			if (response.data[0] && response.data[0].toDoItems) {
				wo.toDoItems = response.data[0].toDoItems;
				wo.schedulingToDoComment = response.data[0].schedulingToDoComment;
				wo.executionPkgComment = response.data[0].executionPkgComment;
			}
			ctrl.populateToDoBindings(wo, wo.toDoItems);
			ctrl.populateWorkOrderDisplayList(wo);
		}, null);
	}

	ctrl.updateBindingVarOnSelect= function(wo, todoName) {
		var key = ctrl.getWorkOrderToDoKey(wo, todoName);
		wo.savedMsgVisible = false;

		var selectedValArray = wo.toDoBindingVar[key];
		if (angular.isDefined(selectedValArray) && selectedValArray.length > 0) {
			var allIndex = selectedValArray.indexOf('ALL');
			if (allIndex > -1 && allIndex < selectedValArray.length - 1) {
				selectedValArray.splice(allIndex, 1);
			} else if (allIndex > -1 && allIndex == selectedValArray.length - 1) {
				selectedValArray = ['ALL'];
			} else if (selectedValArray.length == wo.workOrders.length) {
				selectedValArray = ['ALL'];
			}
			wo.toDoBindingVar[key] = selectedValArray;

		}else{
			var enableKey = ctrl.getWorkOrderToDoKeyForCheckBox(wo, todoName);
			if(angular.isDefined($('#'+'enable-' + enableKey))){
				$('#'+'enable-' + enableKey).prop('checked', false);
			}
		}
	}
	
	ctrl.populateToDoBindings = function(workOrder) {
		workOrder.toDoBindingVar = {};
			if (workOrder && workOrder.toDoItems) {
				for (var i = 0; i< workOrder.toDoItems.length; i++) {
					var boundWorkOrders = workOrder.toDoItems[i].workOrders;
					if (angular.isDefined(boundWorkOrders) && angular.isDefined(workOrder) && boundWorkOrders.length == workOrder.workOrders.length) {
						if (boundWorkOrders.length > 1) {
								boundWorkOrders = ['ALL'];
						}
					}
					workOrder.toDoBindingVar[ctrl.getWorkOrderToDoKey(workOrder, workOrder.toDoItems[i].toDoName)] = boundWorkOrders;
				}
			}
	}
	
	ctrl.getWorkOrderToDoKey = function(workOrder, todoName) {
		if (angular.isUndefined(workOrder))
			return "";
		return (angular.isDefined(workOrder.exctnPckgName)?workOrder.exctnPckgName:workOrder.workOrders[0]) + "-" + todoName;
	}
	ctrl.getWorkOrderToDoKeyForCheckBox = function(workOrder, todoName) {
		return ctrl.getWorkOrderToDoKey(workOrder,todoName).replace(" ","_");
	}
	ctrl.disbledDropdown = function(wo, todoName){
		var key = ctrl.getWorkOrderToDoKey(wo, todoName);
		if(angular.isDefined(wo.toDoBindingVar)){
			var selectedValArray = wo.toDoBindingVar[key];
			if (angular.isDefined(selectedValArray) && selectedValArray.length > 0) {
				return true;
			}else if(angular.isDefined(selectedValArray) && selectedValArray.length == 0){
				return false;
				
			}
		}
	};
	
	ctrl.populateWorkOrderDisplayList = function(wo) {
		wo.workOrderIdDisplayArray = [];
		if (angular.isDefined(wo) 
				&& angular.isDefined(wo.workOrders)) {
			if (wo.workOrders.length > 1) {
				wo.workOrderIdDisplayArray.push("ALL");
			} 
			for (var i = 0; i < wo.workOrders.length; i++) {
				wo.workOrderIdDisplayArray.push(wo.workOrders[i]);
			}
		} 
	}
	
}


angular.module('todoPortal').component('schedulingToDoResult', {
  templateUrl: '../views/schedulingToDo.html',
  controller: schedulingToDoResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  metadata: '<',
	  handleDataChange: '&',
	  functionId: '<'
  }
});