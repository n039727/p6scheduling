function viewToDoStatusController($scope,restTemplate) {
	var ctrl = this;
	ctrl.successSavedMsg = "";
	ctrl.savedMsgVisible = false;
	
	console.log('$ctrl.activeContext in view: ' + JSON.stringify(ctrl.activeContext));
	console.log('data received: ' + JSON.stringify(ctrl.data));
	
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
	
	ctrl.saveToDo = function(wo){
		
		if (wo.todoAssignments) {
			for (var i =0; i<wo.todoAssignments.length; i++) {
				if(angular.isDefined(wo.todoAssignments[i].reqByDt) && wo.todoAssignments[i].reqByDt !== null){
					wo.todoAssignments[i].reqByDate = ctrl.formatDate(wo.todoAssignments[i].reqByDt);
				}else{
					wo.todoAssignments[i].reqByDate = "";
					
				}
			}
		}
		
		if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal-service/scheduler/saveWorkOrderForViewToDoStatus";
		}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal-service/depot/updateTodo";
			
		}
		console.log('serviceUrl in Save To Do called with WO: ' + JSON.stringify(serviceUrl));
		console.log('Save To Do called with WO: ' + JSON.stringify(wo));
		
		var req = {
			 method: 'POST',
			 url: serviceUrl,
			 headers: {
			   'Content-Type': 'application/json'
			 },
			 data: JSON.stringify(wo)
		};
/*		$http(req).then(function (response) {
			console.log("Received data from server");
			$scope.fetchedData = response.data;
			console.log("Data from server: " + JSON.stringify($scope.fetchedData));
			ctrl.savedMsgVisible = true;
		});*/
		restTemplate.callService(req, function (response) {
			console.log("Received data from server");
			$scope.fetchedData = response.data;
			console.log("Data from server: " + JSON.stringify($scope.fetchedData));
			if(angular.isDefined(wo.exctnPckgName) && wo.exctnPckgName !== null){
				ctrl.successSavedMsg = "Package has been saved successfully";
			}else{
				ctrl.successSavedMsg = "Work order task has been saved successfully";
			}
			ctrl.savedMsgVisible = true;
			
		}, null);
		
		ctrl.handleDataChange({event: {eventId:'TO_DO_DETAILS_SAVED'}});
	};
	
	ctrl.fetchToDoAgainstWO = function(wo) {
		if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal-service/scheduler/fetchWOForTODOStatus";
		}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal-service/depot/viewTodo";
		}
		console.log('fetching To-Dos for work order: ' + wo.workOrders[0]);
		console.log('serviceUrl: ' + serviceUrl);
		var query = {};
		if(angular.isDefined(wo.exctnPckgName) && wo.exctnPckgName !== null){
			query = {execPckgName:wo.exctnPckgName};
		}else{
			query = {workOrderId:wo.workOrders[0]};
			
		}
		console.log('request fetching To-Dos for work order: ' + JSON.stringify(query));
		var req = {
				method: 'POST',
				url: serviceUrl,
				headers: {
				   'Content-Type': 'application/json'
				},
				data: JSON.stringify(query)
		};
	/*	$http(req).then(function (response) {
			console.log("Received data from server for fetchWOForTODOStatus: " + JSON.stringify(response.data));
			wo.todoAssignments = [];
			wo.todoAssignments = response.data.todoAssignments;
			if (wo.todoAssignments) {
				for (var i =0; i<wo.todoAssignments.length; i++) {
					console.log("req by date for fetchWOForTODOStatus: " + JSON.stringify(wo.todoAssignments[i].reqByDate));
					if(angular.isDefined(wo.todoAssignments[i].reqByDate) && wo.todoAssignments[i].reqByDate !== null && wo.todoAssignments[i].reqByDate !== ""){					
						wo.todoAssignments[i].reqByDt = new Date(wo.todoAssignments[i].reqByDate);
					}
				}
			}
			wo.schedulingComment = response.data.schedulingComment;
			console.log("Work Order after fetch todo: " + JSON.stringify(wo));
		});*/
		restTemplate.callService(req, function (response) {
			console.log("Received data from server for fetchWOForTODOStatus: " + JSON.stringify(response.data));
			wo.todoAssignments = [];
			wo.todoAssignments = response.data.todoAssignments;
			if (wo.todoAssignments) {
				for (var i =0; i<wo.todoAssignments.length; i++) {
					console.log("req by date for fetchWOForTODOStatus: " + JSON.stringify(wo.todoAssignments[i].reqByDate));
					if(angular.isDefined(wo.todoAssignments[i].reqByDate) && wo.todoAssignments[i].reqByDate !== null && wo.todoAssignments[i].reqByDate !== ""){					
						wo.todoAssignments[i].reqByDt = new Date(wo.todoAssignments[i].reqByDate);
					}
				}
			}
			wo.schedulingComment = response.data.schedulingComment;
			console.log("Work Order after fetch todo: " + JSON.stringify(wo));
		}, null);
		
		
	ctrl.formatDate = function(date) {
			var d = new Date(date),
				month = '' + (d.getMonth() + 1),
				day = '' + d.getDate(),
				year = d.getFullYear();

			if (month.length < 2) month = '0' + month;
			if (day.length < 2) day = '0' + day;

			return [day, month, year].join('/');
		}
	}
	
}


angular.module('todoPortal').component('viewToDoResult', {
  templateUrl: '../views/viewToDoStatus.html',
  controller: viewToDoStatusController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  handleDataChange: '&'
  }
});