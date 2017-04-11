var app = angular.module("todoPortal", []);
app.controller("toDoPortalCOntroller", function($scope, $http) {

	$scope.workOrders = [];
			
	$http.post("/p6-portal-service/retrieveJobs").then(function (response) {
		console.log("Received data from server");
		$scope.fetchedData = response.data;
		console.log("Data from server: " + JSON.stringify($scope.fetchedData));
	});
	
	$scope.todoGrp1 = ["ESA","DEC Permit","DBYD","Gas Permit","Rail Permit","Water Permit","ENAR"];
	$scope.todoGrp2 = ["Traffic","Lay Down Area Arrangements","Fibre Optics","Inductions (Mine Site)","Specialised Plant / Equipment Availability","Additional Trades","Others"];
	$scope.depots = ["Depot1","Depot2","Depot3","Depot4"];
	$scope.crews = ["MOST1","MOST2","MOST3"];
	$scope.resultVisible = false;
	
	$scope.queryCrew = "";
	$scope.queryScheduleDate = "";
	
	$scope.search = function () {
	
			/*var filteredResult = [];
			console.log('queryCrew: ' + $scope.queryCrew);
			console.log('queryScheduleDate: ' + $scope.queryScheduleDate);
			if ($scope.fetchedData) {
				for (i = 0; i < $scope.fetchedData.length; i++) {
					var wo = $scope.fetchedData[i];
					if (wo.leadCrew == $scope.queryCrew
							&& wo.scheduleDate == $scope.queryScheduleDate) {
						filteredResult.push(wo);
					}
				}
			}*/
			
			$scope.workOrders = $scope.fetchedData
			$scope.resultVisible = true;
			$('[id^=datePicker')
				.datepicker({
					format: 'dd/mm/yyyy'
			});
	};
	$scope.refresh = function () {
			$scope.queryCrew = "";
			$scope.queryScheduleDate = "";
			$scope.queryDepot = "";
			$scope.resultVisible = false;
	};
	$scope.addRemoveTodo = function ($event, wo, todo) {
			var cb = $event.target;
			if (cb.checked) {
				if (findToDo(wo.toDoItems, todo) == -1) {
					if(!wo.toDoItems)
						wo.toDoItems = [];
						
					wo.toDoItems.push({toDoName:todo, workOrders:[wo.workOrders[0]]});
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
	
	function findToDo(toDoList, toDoName) {
		if (toDoList) {
			for(i=0; i < toDoList.length; i++) {
				if (toDoList[i].toDoName === toDoName) {
					return i;
				}
			}
		}
		return -1;
	}
	
	$scope.calculateEnable = function(wo, todo) {
		//console.log('Work Order: ');
		//console.log(JSON.stringify(wo));
		if (wo.toDoItems) {
			for(i=0; i < wo.toDoItems.length; i++) {
				//console.log('Comparing ' + wo.toDoItems[i].toDoName + ' with ' + todo);
				if (wo.toDoItems[i].toDoName === todo) {
					//console.log('returning true');
					return true;
				}
			}
		}
		
		//console.log('returning false');
		return false;
	};
	
	$scope.saveToDo = function(wo){
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
	};
	
	$scope.toggleExpansion  = function($event) {
		var button = $event.target;
		
		if($('#'+button.id).hasClass("glyphicon-plus")) {
			$('#'+button.id).removeClass("glyphicon-plus");
			$('#'+button.id).addClass("glyphicon-minus");
		} else if ($('#'+button.id).hasClass("glyphicon-minus")) {
			$('#'+button.id).removeClass("glyphicon-minus");
			$('#'+button.id).addClass("glyphicon-plus");
		}
	};
	
	$scope.page = 'ADD';
	
	$scope.viewToDoStatus = function() {
		$scope.page = 'VIEW';
		$scope.workOrders = [];
		$scope.resultVisible = false;
	} 
	
	$scope.addUpdateSchedulingToDo = function() {
		$scope.page = 'ADD';
		$scope.workOrders = [];
		$scope.resultVisible = false;
	} 
	
});
			