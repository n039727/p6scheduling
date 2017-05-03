var app = angular.module("todoPortal", [ 'oi.select', 'ngMaterial',
		'angularModalService' ]);

Array.prototype.contains = function(v) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] === v)
			return true;
	}
	return false;
};

Array.prototype.unique = function() {
	var arr = [];
	for (var i = 0; i < this.length; i++) {
		if (!arr.contains(this[i])) {
			arr.push(this[i]);
		}
	}
	return arr;
}

function bootstrapApplication() {
	angular.element(document).ready(function() {
		angular.bootstrap(document, [ "todoPortal" ]);
	});
}
fetchMetaData(app).then(bootstrapApplication);

function fetchMetaData(app) {
	var initInjector = angular.injector([ "ng" ]);
	var $http = initInjector.get("$http");
	return $http.get("/p6-portal-service/scheduler/fetchToDos").then(
			function(response) {
				console.log("Received data from server for fetch to dos: "
						+ JSON.stringify(response.data));
				metadata = {};
				metadata.depotList = [ {
					id : 1,
					name : 'Depot1'
				}, {
					id : 2,
					name : 'Depot2'
				}, {
					id : 3,
					name : 'Depot3'
				} ];
				metadata.crewList = [ {
					id : 1,
					name : 'MOST1'
				}, {
					id : 2,
					name : 'MOST2'
				}, {
					id : 3,
					name : 'MOST3'
				} ];
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

	ctrl.reload = function(query, success) {
		console.log('data == ' + JSON.stringify(query))
		$http({
			method : 'POST',
			url : '/p6-portal-service/scheduler/search',
			data : JSON.stringify(query),
			headers : {
				'Content-Type' : 'application/json'
			}

		}).then(
				function(response) {
					console.log("Received data from server");
					ctrl.fetchedData = response.data;
					console.log("Data from server: "
							+ JSON.stringify(ctrl.fetchedData));
					success(response.data);

				});
	};

	ctrl.search = function(query) {
		console.log('Query:' + JSON.stringify(query));
		var serviceUrl = "";
		// if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
		// }
		ctrl.reload(query, function(data) {
			ctrl.workOrders = data;
			ctrl.resultVisible = true;
			ctrl.savedMsgVisible = false;
			if(ctrl.activeContext == 'CREATE_EXECUTION_PACKAGE'){
				console.log('called function for execution:');
				ctrl.displayOnExecPkg(data);
			}
		});
	};

	ctrl.activeContext = 'ADD_SCHEDULING_TODO';

	ctrl.displayOnExecPkg = function(data){
		ctrl.tempData = [];
		if(data.length > 0){
			for(var i=0;i<data.length; i++){
				if(data[i].workOrders.length > 1){
					workOrderList = [];
					crewNames = [];
					workOrderList = data[i].workOrders;
					crewNames = data[i].crewNames.split(',');
					scheduleFromDate = data[i].scheduleDate;
					leadCrew = data[i].leadCrew;
					execPkgName = data[i].exctnPckgName;
					for(var j=0;j< workOrderList.length;j++){
						ctrl.tempData.push({exctnPckgName:execPkgName,workOrders:[workOrderList[j]],scheduleDate:scheduleFromDate,crewNames:crewNames[j]});
					}
				}else{
					ctrl.tempData.push(data[i]);
				}	
			}
		}
		console.log('ctrl.tempData: ' + JSON.stringify(ctrl.tempData));
		ctrl.workOrders = ctrl.tempData;
		console.log('ctrl.data: ' + JSON.stringify(ctrl.workOrders));
	};
	
	ctrl.handleContext = function(context) {
		console.log('handle context called with context: ' + context);
		ctrl.activeContext = context;
		ctrl.resultVisible = false;
	};

	ctrl.handleDataChange = function(event) {
		console.log('Data has changed for event: ' + JSON.stringify(event));
		if (event && event.eventId === 'EXECUTION_PKG_CREATED') {
			ctrl.handleContext('ADD_SCHEDULING_TODO');
			ctrl.workOrders = [ event.eventData ];
			ctrl.resultVisible = true;
			ctrl.savedMsgVisible = false;
		}
	}

	$http({
		method : "GET",
		url : '/p6-portal/web/user/name'
	}).success(function(data) {
		$scope.userName = data.userName;
	}).error(function() {

	});
});
