function schedulingToDoResultController($scope, $http) {
	var ctrl = this;
	
	console.log('data received: ' + JSON.stringify(ctrl.data));
	
	ctrl.toggleExpansion  = function($event) {
		var button = $event.target;
		
		if($('#'+button.id).hasClass("glyphicon-plus")) {
			$('#'+button.id).removeClass("glyphicon-plus");
			$('#'+button.id).addClass("glyphicon-minus");
		} else if ($('#'+button.id).hasClass("glyphicon-minus")) {
			$('#'+button.id).removeClass("glyphicon-minus");
			$('#'+button.id).addClass("glyphicon-plus");
		}
	};
	
	ctrl.todoGrp1 = ["ESA","DEC Permit","DBYD","Gas Permit","Rail Permit","Water Permit","ENAR"];
	ctrl.todoGrp2 = ["Traffic","Lay Down Area Arrangements","Fibre Optics","Inductions (Mine Site)","Specialised Plant / Equipment Availability","Additional Trades","Others"];
		
	ctrl.addRemoveTodo = function ($event, wo, todo) {
			var cb = $event.target;
			if (cb.checked) {
				if (findToDo(wo.toDoItems, todo) == -1) {
					if(!wo.toDoItems)
						wo.toDoItems = [];
						
					wo.toDoItems.push({todoName:todo, workOrders:[wo.workOrders[0]]});
					console.log('WO after adding To Do: ' + JSON.stringify(wo));
				}
					
			} else {
				var index = findToDo(wo.toDoItems, todo);
				if (index > -1) {
					wo.toDoItems.splice(index, 1);
					console.log('WO after removing To Do: ' + JSON.stringify(wo));
				}
			}
				//wo.todos.remove(todo);
			
	};
	
	function findToDo(toDoList, todoName) {
		if (toDoList) {
			for(i=0; i < toDoList.length; i++) {
				if (toDoList[i].todoName === todoName) {
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
				//console.log('Comparing ' + wo.toDoItems[i].todoName + ' with ' + todo);
				if (wo.toDoItems[i].todoName === todo) {
					//console.log('returning true');
					return true;
				}
			}
		}
		
		//console.log('returning false');
		return false;
	};

	ctrl.saveToDo = function(wo){
		console.log('Save To Do called with WO: ' + JSON.stringify(wo));
		var req = {
			 method: 'POST',
			 url: '/p6-portal-service/saveWorkOrder',
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: JSON.stringify(wo)
		};
		$http(req).then(function (response) {
			console.log("Received data from server");
			$scope.fetchedData = response.data;
			console.log("Data from server: " + JSON.stringify($scope.fetchedData));
			alert("Scheduling TO DO saved for " + wo.workOrders[0]);
		});
		ctrl.handleDataChange();
	};
	
}


angular.module('todoPortal').component('schedulingToDoResult', {
  templateUrl: '../views/schedulingToDo.html',
  controller: schedulingToDoResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  handleDataChange: '&'
  }
});