var app = angular.module("todoPortal", ['oi.select', 'ngMaterial']);
app.controller("toDoPortalCOntroller", function($scope, $http) {

	var ctrl = this;
	ctrl.workOrders = [];
	ctrl.savedMsgVisible = false;
				
	/*$http.post("/p6-portal-service/retrieveJobs").then(function (response) {
		console.log("Received data from server");
		$scope.fetchedData = response.data;
		console.log("Data from server: " + JSON.stringify($scope.fetchedData));
	});*/
	
	ctrl.reload = function() {
		$http.post("/p6-portal-service/retrieveJobs").then(function (response) {
			console.log("Received data from server");
			ctrl.fetchedData = response.data;
			console.log("Data from server: " + JSON.stringify(ctrl.fetchedData));
		});
	};
	
	ctrl.fetchToDos = function(){
			var req = {
					 method: 'GET',
					 url: '/p6-portal-service/scheduler/fetchToDos',
					 headers: {
					   'Content-Type': 'application/json'
					 },
					 data: JSON.stringify()
					 
				};
				$http(req).then(function (response) {
					console.log("Received data from server for fetch to dos");
					ctrl.metadata.todoList = response.data;
					console.log("Data from server: " + JSON.stringify($scope.fetchedData));
					
				});
			
		
	}
	
	ctrl.reload();
	
	ctrl.fetchedData = [{"workOrders":["Y6UIOP67"],"scheduleDate":"2017-04-15","leadCrew":"MOST2","toDoItems":[{"todoNam":"ESA","workOrders":["Y6UIOP67"]}]},{"workOrders":["Y6UIOP97"],"scheduleDate":"2017-04-15","leadCrew":"MOST1","toDoItems":[{"todoNam":"ENAR","workOrders":["Y6UIOP97"]}]},{"workOrders":["Y6UIOP87"],"scheduleDate":"2017-04-15","leadCrew":"MOST3"}];
	
	ctrl.metadata = {};
	ctrl.metadata.depotList = [{id:1, name:'Depot1'}, {id:2, name:'Depot2'}, {id:3, name:'Depot3'}];
	ctrl.metadata.crewList = [{id:1, name:'MOST1'}, {id:2, name:'MOST2'}, {id:3, name:'MOST3'}];
	ctrl.resultVisible = false;
	ctrl.fetchToDos();	
	console.log('in Parent, metadata:' + JSON.stringify(ctrl.metadata));
	
	ctrl.search = function (query) {
			console.log('Query:' + JSON.stringify(query));
			var serviceUrl = "";
			if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
				serviceUrl = "/p6-portal-service/scheduler/fetchWOForTODOStatus";
				var req = {
						method: 'POST',
						url: serviceUrl,
						headers: {
						   'Content-Type': 'application/json'
						},
						data: JSON.stringify(query)

					};
					$http(req).then(function (response) {
						console.log("Received data from server for fetchWOForTODOStatus");
						$scope.fetchedData = response.data;
						ctrl.workOrders = ctrl.fetchedData;
						console.log("Data from server: " + JSON.stringify($scope.fetchedData));
						ctrl.resultVisible = true;
						ctrl.savedMsgVisible = false;
						
					});
				
				
			} 
			
			ctrl.workOrders = ctrl.fetchedData
			ctrl.resultVisible = true;
			ctrl.savedMsgVisible = false; 
	};
	
	ctrl.activeContext = 'ADD_SCHEDULING_TODO';
	
	ctrl.handleContext = function(context) {
		console.log('handle context called with context: ' + context);
		ctrl.activeContext = context;
		ctrl.resultVisible = false;
	};
	
	
	
	/*$scope.saveToDo = function(wo){
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
	};*/
	
	ctrl.handleDataChange = function() {
		console.log('Data has changed')
		ctrl.reload();
	}
	
	
});
