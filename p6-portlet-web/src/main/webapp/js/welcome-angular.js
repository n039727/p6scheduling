var app = angular.module("todoPortal", [ 'oi.select', 'ngMaterial',
		'angularModalService','ngSanitize' ]);

Array.prototype.contains = function(v) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] === v)
			return true;
	}
	return false;
};

Array.prototype.unique = function() {
	var itemMap = {};
	var arr = [];
	for (var i = 0; i < this.length; i++) {
		itemMap[this[i]] = {};
	}
	$.each(itemMap, function (key, value) {
	   arr.push(key);
	});
	return arr;
};

Array.prototype.returnIfExists = function(value) {
	if (angular.isUndefined(value) || value === null) {
		return "";
	}
	if (this.indexOf(value) >= 0) {
		return value;
	} else {
		return "";
	}
};

app.service('userAccessService', function($http, userdata) {
		this.hasUpdateableAccess = function(functionId) {
			var entry = null;
			if (angular.isDefined(userdata.accessMap) && userdata.accessMap != null) {
				entry = userdata.accessMap[functionId];
			}
			console.log('Entry  for ' + functionId + ' : ' + (entry != null ? JSON.stringify(entry) : 'null'));
			return entry != null && entry.access;
		}
		
		this.hasAccess = function(functionId) {
			return userdata != null 
				&& angular.isDefined(userdata.accessMap)
				&& userdata.accessMap != null
				&& userdata.accessMap[functionId] != null;
		}
		
		this.isAuthEnabled = function() {
			return userdata != null && userdata.isAuthEnabled; 
		}
});

app.service('restTemplate', function($http, userdata) {
	var restTemplate = this;
	this.callService = function(config, successCallback, errorCallback) {
		if (!angular.isDefined(config)) {
			return;
		}
		
		if (!angular.isDefined(config.headers)) {
			config.headers = {};
		}
		config.headers.AUTH_TOKEN = userdata.authToken;
		console.log("Calling rest service: " + config.url + " with authToken: " + userdata.authToken);
		
		$http(config).then(function(response) {
			console.log("Response Headers : ");
			console.log(response.headers());
			if (response.headers('AUTH_TOKEN') == null) {
				console.log('response auth token has not been set');
			} else {
				userdata.authToken = response.headers('AUTH_TOKEN');
				console.log("Auth Token has been set as " + userdata.authToken);
			}
			
			if(angular.isDefined(successCallback) && successCallback != null) {
					successCallback(response);
			}
			
		  }, function(response) {
				console.log("Error occurred while consuming rest service");
				if(angular.isDefined(errorCallback) && errorCallback != null) {
					errorCallback(response);
				}
		  });
	}
});

function bootstrapApplication() {
	angular.element(document).ready(function() {
		angular.bootstrap(document, [ "todoPortal" ]);
	});
}

fetchUserData(app).then(bootstrapApplication);

function fetchUserData(app) {
	var initInjector = angular.injector([ "ng" ]);
	var $http = initInjector.get("$http");
	return $http.get("/p6-portal/web/user/name").then(
			function(response) {
				userData = {};
				console.log("Response Headers : ");
				console.log(response.headers());
				if (response.headers('AUTH_TOKEN') == null) {
					console.log('response auth token has not been set');
				} else {
					userData.authToken = response.headers('AUTH_TOKEN');
					console.log("Auth Token has been set as " + userData.authToken);
				}
				console.log("Received data from server for fetch to dos: "
						+ JSON.stringify(response.data));
				userData.name = response.data.userName;
				userData.accessMap = response.data.accessMap;
				userData.isAuthEnabled = response.data.authEnabled;
				app.constant('userdata', userData);
				return fetchMetaData(app);
			});
}



function fetchMetaData(app) {
	var initInjector = angular.injector([ "ng" ]);
	var $http = initInjector.get("$http");
	return $http.get("/p6-portal-service/scheduler/fetchMetadata").then(
			function(response) {
				console.log("Received data from server for fetch to dos: "
						+ JSON.stringify(response.data));
				metadata = {};
				metadata.crewList = response.data.crews;
				metadata.todoList = response.data.toDoItems;
				metadata.depotCrewMap = response.data.resourceDTO.depotCrewMap;
				metadata.isErrdataAvail = false;
				metadata.setToDateDisable = true;
				app.constant('metadata', metadata);
			});
}

app.controller("toDoPortalCOntroller", function($scope, metadata, restTemplate, userdata) {

	var ctrl = this;
	console.log('metadata: ' + JSON.stringify(metadata));
	ctrl.metadata = metadata;
	ctrl.workOrders = [];
	ctrl.savedMsgVisible = false;
	
	if (angular.isDefined(userdata) && userdata != null) {
		$scope.userName = userdata.name;
	}

	ctrl.reload = function(query, success) {
		console.log('data == ' + JSON.stringify(query))
		var serviceUrl = "";
		if (ctrl.activeContext === 'CREATE_EXECUTION_PACKAGE') {
			serviceUrl = "/p6-portal-service/executionpackage/searchByExecutionPackage";
		} else {
			serviceUrl = "/p6-portal-service/scheduler/search";
		}
		var config = {
			method : 'POST',
			url : serviceUrl,
			data : JSON.stringify(query),
			headers : {
				'Content-Type' : 'application/json'
			}
		};
		restTemplate.callService(config, function(response) {
					console.log("Received data from server");
					ctrl.fetchedData = response.data;
					console.log("Data from server: "
							+ JSON.stringify(ctrl.fetchedData));
					success(response.data);

				}, null);
	};

	ctrl.search = function(query) {
		console.log('Query:' + JSON.stringify(query));
		var serviceUrl = "";
		ctrl.reload(query, function(data) {
			ctrl.workOrders = data;
			ctrl.resultVisible = data.length > 0;
			if(ctrl.resultVisible){
				ctrl.metadata.isErrdataAvail = false;
			}else{
				ctrl.metadata.isErrdataAvail = true;
			}
			ctrl.savedMsgVisible = false;
		});
	};

	ctrl.handleContext = function(context) {
		console.log('handle context called with context: ' + context);
		ctrl.activeContext = context;
		ctrl.resultVisible = false;
		ctrl.handleActiveContext({eventId:"ACTIVE_CONTEXT_CHANGE"});
	};

	ctrl.handleActiveContext = function(eventId){
		console.log('Data has changed for handleActiveContext: ' + JSON.stringify(eventId));
		if (angular.isUndefined(eventId) || eventId == null) {
			return;
		}
		
			if(ctrl.activeContext == 'ADD_SCHEDULING_TODO' || ctrl.activeContext == 'VIEW_TODO_STATUS' 
				|| ctrl.activeContext == 'CREATE_EXECUTION_PACKAGE' || ctrl.activeContext == 'VIEW_MATERIAL_REQUISITION'){
				console.log('schudular context in handleActiveContext: ' + JSON.stringify(eventId));
				metadata.setToDateDisable = true;
			}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS' || ctrl.activeContext == 'DEPOT_ADD_SCHEDULING_TODO' || ctrl.activeContext == 'DEPOT_VIEW_MATERIAL_REQUISITION'){
				console.log('depot context in handleActiveContext: ' + JSON.stringify(eventId));
				metadata.setToDateDisable = false;
			}
	}
	
	ctrl.handleDataChange = function(event) {
		console.log('Data has changed for event: ' + JSON.stringify(event));
		if (event && event.eventId === 'EXECUTION_PKG_CREATED') {
			ctrl.handleContext('ADD_SCHEDULING_TODO');
			ctrl.workOrders = [ event.eventData ];
			ctrl.resultVisible = true;
			ctrl.savedMsgVisible = false;
		} else if (event && event.eventId === 'DEPOT_TODO_SAVED') {
			var config = {
				method : 'GET',
				url : "/p6-portal-service/scheduler/fetchMetadata"
			};
			restTemplate.callService(config, function(response) {
					console.log("Received data from server: " + JSON.stringify(response.data));
					metadata.crewList = response.data.crews;
					metadata.todoList = response.data.toDoItems;
				}, null);
		}
	}
	
});
