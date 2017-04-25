var app = angular.module("todoPortal", ['oi.select', 'ngMaterial','angularModalService']);

function bootstrapApplication() {
	angular.element(document).ready(function() {
		angular.bootstrap(document, ["todoPortal"]);
	});
}
fetchMetaData(app).then(bootstrapApplication);

function fetchMetaData(app) {
	var initInjector = angular.injector(["ng"]);
	var $http = initInjector.get("$http");
	return $http.get("/p6-portal-service/scheduler/fetchToDos").then(function (response) {
		console.log("Received data from server for fetch to dos: " + JSON.stringify(response.data));
		metadata = {};
		metadata.depotList = [{id:1, name:'Depot1'}, {id:2, name:'Depot2'}, {id:3, name:'Depot3'}];
		metadata.crewList = [{id:1, name:'MOST1'}, {id:2, name:'MOST2'}, {id:3, name:'MOST3'}];
		metadata.todoList = response.data;
		app.constant('metadata', metadata);
	});
}

app.controller("toDoPortalCOntroller", function($scope, $http, metadata) {
	
	var ctrl = this;
	console.log('metadata: ' + JSON.stringify(metadata));
	ctrl.metadata = metadata;
	ctrl.workOrders = [];
	ctrl.savedMsgVisible = false;
				
	ctrl.reload = function(success) {
		$http.post("/p6-portal-service/retrieveJobs").then(function (response) {
			console.log("Received data from server");
			ctrl.fetchedData = response.data;
			console.log("Data from server: " + JSON.stringify(ctrl.fetchedData));
			success(response.data);
		});
	};
	
	ctrl.search = function (query) {
			console.log('Query:' + JSON.stringify(query));
			var serviceUrl = "";
			//if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
			//} 
			ctrl.reload(function(data) {
					ctrl.workOrders = data;
					ctrl.resultVisible = true;
					ctrl.savedMsgVisible = false;
				}); 
			 
	};
	
	ctrl.activeContext = 'ADD_SCHEDULING_TODO';
	
	ctrl.handleContext = function(context) {
		console.log('handle context called with context: ' + context);
		ctrl.activeContext = context;
		ctrl.resultVisible = false;
	};
	
	ctrl.handleDataChange = function(event) {
		console.log('Data has changed for event: ' + JSON.stringify(event));
		if (event&&event.eventId === 'EXECUTION_PKG_CREATED') {
			ctrl.handleContext('ADD_SCHEDULING_TODO');
			var eventData = event.eventData;
			if(eventData) {
				var executionPkgName = eventData.executionPkgName;
				ctrl.search({executionPkgName:executionPkgName});
			}
		}
	}
	
	
});
