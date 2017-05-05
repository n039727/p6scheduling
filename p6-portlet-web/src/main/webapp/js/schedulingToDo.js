function schedulingToDoResultController($scope, $http) {
	var ctrl = this;
	
	console.log('data received: ' + JSON.stringify(ctrl.data));
	console.log('active context: ' + JSON.stringify(ctrl.activeContext));
	
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
		ctrl.savedMsgVisible = false;
		
	};
	
	//ctrl.todoGrp1 = ["ESA","DEC Permit","DBYD","Gas Permit","Rail Permit","Water Permit","ENAR"];
	//ctrl.todoGrp2 = ["Traffic","Lay Down Area Arrangements","Fibre Optics","Inductions (Mine Site)","Specialised Plant / Equipment Availability","Additional Trades","Others"];
	//console.log('fetch todoList:' + JSON.stringify(ctrl.metadata));
	
	ctrl.todoGrp1 = [];
	ctrl.todoGrp2 = [];
	if(ctrl.metadata.todoList){
		for(i=0; i<ctrl.metadata.todoList.length;i++) {  
			if (i < ctrl.metadata.todoList.length/2) 
				ctrl.todoGrp1.push(ctrl.metadata.todoList[i].toDoName);
			else
				ctrl.todoGrp2.push(ctrl.metadata.todoList[i].toDoName);
		}
	}
	
		
	ctrl.addRemoveTodo = function ($event, wo, todo) {
			var cb = $event.target;
			if (cb.checked) {
				if (findToDo(wo.toDoItems, todo) == -1) {
					if(!wo.toDoItems)
						wo.toDoItems = [];
						
					wo.toDoItems.push({toDoName:todo, workOrders:[wo.workOrders[0]]});
					console.log('WO after adding To Do: ' + JSON.stringify(wo));
				}
				if (angular.isDefined(wo.workOrderIdDisplayArray) 
						&& wo.workOrderIdDisplayArray.length > 0) {
					ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, todo)] = [wo.workOrderIdDisplayArray[0]];	
				}
			} else {
				var index = findToDo(wo.toDoItems, todo);
				if (index > -1) {
					wo.toDoItems.splice(index, 1);
					console.log('WO after removing To Do: ' + JSON.stringify(wo));
				}
				ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, todo)] = [];
			}
				//wo.todos.remove(todo);
			
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
		//console.log('Work Order: ');
		//console.log(JSON.stringify(wo));
		if (wo.toDoItems) {
			for(i=0; i < wo.toDoItems.length; i++) {
				//console.log('Comparing ' + wo.toDoItems[i].toDoName + ' with ' + todo);
				if (wo.toDoItems[i].toDoName == todo) {
					//console.log('returning true');
					return true;
				}
			}
		}
		
		//console.log('returning false');
		return false;
	};

	ctrl.saveToDo = function(wo){
		// populate updated to do assignments
		if (wo && wo.toDoItems) {
			for (var i = 0; i< wo.toDoItems.length; i++) {
				wo.toDoItems[i].workOrders = ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(wo, wo.toDoItems[i].toDoName)];
			}
		}
		
		console.log('Save To Do called with WO: ' + JSON.stringify(wo));
		var req = {
			 method: 'POST',
			 url: '/p6-portal-service/scheduler/saveWorkOrder',
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: JSON.stringify(wo)
			 
		};
		$http(req).then(function (response) {
			console.log("Received data from server");
			$scope.fetchedData = response.data;
			console.log("Data from server: " + JSON.stringify($scope.fetchedData));
			//alert("Scheduling TO DO saved for " + wo.workOrders[0]);
			ctrl.savedMsgVisible = true;
			
		});
		ctrl.handleDataChange({event:{eventId:'SCHEDULING_TODO_SAVED'}});
	};
	
	ctrl.updateBindingVarOnSelect= function(wo, todoName) {
		console.log('update binding var is called: ' + todoName);
		var key = ctrl.getWorkOrderToDoKey(wo, todoName);
		var selectedValArray = ctrl.toDoBindingVar[key];
		if (angular.isDefined(selectedValArray)) {
			var allIndex = selectedValArray.indexOf('ALL');
			if (allIndex > -1 && allIndex < selectedValArray.length - 1) {
				selectedValArray.splice(allIndex, 1);
				console.log('ALL is removed from key: ' + key);
			} else if (allIndex > -1 && allIndex == selectedValArray.length - 1) {
				selectedValArray = ['ALL'];
			} else if (selectedValArray.length == wo.workOrders.length) {
				selectedValArray = ['ALL'];
			}
			ctrl.toDoBindingVar[key] = selectedValArray;
		}
	}
	
	ctrl.fetchToDoAgainstWO = function(wo) {
		serviceUrl = "/p6-portal-service/scheduler/fetchWOForAddUpdateToDo";
		var query = {};
		if (wo.exctnPckgName) {
			query.execPckgName = wo.exctnPckgName;
		} else {
			query.workOrderId = wo.workOrders[0];
		}
		
		console.log('fetching To-Dos for : ' + JSON.stringify(query));
		
		var req = {
				method: 'POST',
				url: serviceUrl,
				headers: {
				   'Content-Type': 'application/json'
				},
				data: JSON.stringify(query)

			};
		$http(req).then(function (response) {
			console.log("Received data from server for fetchWOForTODOStatus: " + JSON.stringify(response.data));
			wo.toDoItems = [];
			wo.schedulingToDoComment = "";
			if (response.data[0] && response.data[0].toDoItems) {
				wo.toDoItems = response.data[0].toDoItems;
				wo.schedulingToDoComment = response.data[0].schedulingToDoComment;
			}
			ctrl.populateToDoBindings(wo, wo.toDoItems);
			ctrl.populateWorkOrderDisplayList(wo);
			console.log("Work Order after fetch todo: " + JSON.stringify(wo));
		});
	}
	
	ctrl.toDoBindingVar = {};
	
	ctrl.populateToDoBindings = function(workOrder) {
			ctrl.toDoBindingVar = {};
			if (workOrder && workOrder.toDoItems) {
				for (var i = 0; i< workOrder.toDoItems.length; i++) {
					ctrl.toDoBindingVar[ctrl.getWorkOrderToDoKey(workOrder, workOrder.toDoItems[i].toDoName)] = workOrder.toDoItems[i].workOrders;
				}
			}
			console.log("toDoBindingVar: " + JSON.stringify(ctrl.toDoBindingVar));
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
		
		console.log('Work Order Id List: ' + wo.workOrderIdDisplayArray);
	}
	
}


angular.module('todoPortal').component('schedulingToDoResult', {
  templateUrl: '../views/schedulingToDo.html',
  controller: schedulingToDoResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  metadata: '<',
	  handleDataChange: '&'
  }
});