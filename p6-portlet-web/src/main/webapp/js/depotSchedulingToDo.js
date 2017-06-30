function depotSchedulingToDoResultController($scope, restTemplate, userAccessService) {
	var ctrl = this;
	
	// Authorization implementation
	ctrl.isReadOnly = false;
	if (userAccessService.isAuthEnabled()) {
		if (!userAccessService.hasUpdateableAccess(ctrl.functionId)) {
			ctrl.isReadOnly = true;
		}
	}
	
	ctrl.emptyStr = "";
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
				if (findToDo(wo.toDoItems, todo) == -1) {
					if(!wo.toDoItems)
						wo.toDoItems = [];
						
					wo.toDoItems.push({toDoName:todo, typeId: 1, workOrders:[wo.workOrders[0]]});
				}
				if (angular.isDefined(wo.workOrderIdDisplayArray) 
						&& wo.workOrderIdDisplayArray.length > 0) {
					ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, todo)] = [wo.workOrderIdDisplayArray[0]];	
				}
			} else {
				var index = findToDo(wo.toDoItems, todo);
				if (index > -1) {
					wo.toDoItems.splice(index, 1);
				}
				ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, todo)] = [];
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
				var boundWorkOrders = ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, wo.toDoItems[i].toDoName)];
				if (boundWorkOrders) {
					if (boundWorkOrders.indexOf('ALL') > -1) {
						boundWorkOrders = wo.workOrders;
					}
					wo.toDoItems[i].workOrders = boundWorkOrders;
				}
			}
			ctrl.populateToDoItemsFromMap(wo);
		}
		if(angular.isDefined(wo) && angular.isDefined(wo.toDoItems) && wo.toDoItems.length == 0){
			wo.actioned = 'N';
		}else if(angular.isDefined(wo) && angular.isDefined(wo.toDoItems) && wo.toDoItems.length > 0){
			wo.actioned = 'Y';
		}
		var req = {
			 method: 'POST',
			 url: '/p6-portal/web/depot/addTodo',
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: JSON.stringify(wo)
			 
		};
		restTemplate.callService(req, function (response) {
			$scope.fetchedData = response.data;
			wo.successSavedMsg = "Depot To Do Saved Successfully";
			wo.savedMsgVisible = true;
			
		}, null);
		
		ctrl.handleDataChange({event:{eventId:'DEPOT_TODO_SAVED'}});
	};
	
	
	ctrl.fetchToDoAgainstWO = function(wo) {
		serviceUrl = "/p6-portal/web/depot/fetchTaskForUpdateTodo";
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
			if (response.data[0]) {
				if (response.data[0].toDoItems) {
					wo.toDoItems = response.data[0].toDoItems;
				}
				wo.schedulingToDoComment = response.data[0].schedulingToDoComment;
				wo.executionPkgComment = response.data[0].executionPkgComment;
				wo.depotToDoComment = response.data[0].depotToDoComment;
			}
			ctrl.populateToDoBindings(wo, wo.toDoItems);
			ctrl.populateWorkOrderDisplayList(wo);
			ctrl.createToDoMap(wo);
		}, null);
	}
	
	ctrl.toDoBindingVar = {};
	
	ctrl.populateToDoBindings = function(workOrder) {
			ctrl.toDoBindingVar = {};
			if (workOrder && workOrder.toDoItems) {
				for (var i = 0; i< workOrder.toDoItems.length; i++) {
					if (workOrder.toDoItems[i].typeId === 1) {
						var boundWorkOrders = workOrder.toDoItems[i].workOrders;
						if (angular.isDefined(boundWorkOrders) && angular.isDefined(workOrder) && boundWorkOrders.length == workOrder.workOrders.length) {
							if (boundWorkOrders.length > 1) {
									boundWorkOrders = ['ALL'];
							}
						}
						ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(workOrder, workOrder.toDoItems[i].toDoName)] = boundWorkOrders;
					}
				}
			}
	}
	
	ctrl.getWorkOrderToDoKey = function(workOrder, todoName) {
		return (angular.isDefined(workOrder.exctnPckgName)?workOrder.exctnPckgName:workOrder.workOrders[0]) + "-" + todoName;
	}
	
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
	
	ctrl.createToDoMap = function(workOrder) {
		var todoMap = {};
		if(angular.isDefined(workOrder)
				&& angular.isDefined(workOrder.toDoItems)) {
			for (var i = 0; i< workOrder.toDoItems.length; i++) {
				if (workOrder.toDoItems[i].typeId === 2) {
					todoMap[workOrder.toDoItems[i].toDoName] = workOrder.toDoItems[i].workOrders;
				}
			}
			workOrder.todoMap = todoMap;
		}
		//return todoMap;
	}
	
	ctrl.populateToDoItemsFromMap = function(workOrder) {
		var updatedToDoItems = [];
		if (angular.isDefined(workOrder.toDoItems)) {
			for (var i = 0; i < workOrder.toDoItems.length; i++) {
				if (angular.isDefined(workOrder.toDoItems[i].typeId) 
						&& workOrder.toDoItems[i].typeId === 1) {
					updatedToDoItems.push(workOrder.toDoItems[i]);
					continue;
				}

				var workOrders = workOrder.todoMap[workOrder.toDoItems[i].toDoName];
				if (angular.isDefined(workOrders)
						&& workOrders.length > 0) {
					workOrder.toDoItems[i].workOrders = workOrders;
					updatedToDoItems.push(workOrder.toDoItems[i]);
					delete workOrder.todoMap[workOrder.toDoItems[i].toDoName];
				} 
			}
		}
		
		for(var todo in workOrder.todoMap) {
			updatedToDoItems.push({toDoName:todo, workOrders:workOrder.todoMap[todo]});
		}
		workOrder.toDoItems = updatedToDoItems;
	}
	
	ctrl.populateDepotToDo = function(todoMap, workOrder) {
		workOrder.todoMap = todoMap;
	}

	ctrl.handleEvent = function(wo, eventId, data) {
		if (angular.isUndefined(eventId) || eventId == null) {
			return;
		}

		switch(eventId) {
			case "DATA_CHANGE":
				if (angular.isDefined(data) && data != null) {
					ctrl.populateDepotToDo(data.map, wo);
				}
				break;
			case "ADD_TO_DO_IN_PROGRESS":
				ctrl.disableSaving = true;
				break;
			case "ADD_TO_DO_COMPLETED":
				ctrl.disableSaving = false;
				break;
			default:
//				console.log("No handler found for event: " + eventId);
		} 
	}
	
}


angular.module('todoPortal').component('depotSchedulingToDoResult', {
  templateUrl: '../views/depotSchedulingToDo.html',
  controller: depotSchedulingToDoResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  metadata: '<',
	  handleDataChange: '&',
	  isAuthEnabled:'<',
	  functionId: '<'
  }
});