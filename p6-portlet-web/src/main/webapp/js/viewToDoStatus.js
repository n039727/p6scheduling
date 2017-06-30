function viewToDoStatusController($scope, ModalService, restTemplate, userAccessService) {
	var ctrl = this;

	// Authorization implementation
	ctrl.isReadOnly = false;
	if (userAccessService.isAuthEnabled()) {
		if (!userAccessService.hasUpdateableAccess(ctrl.functionId)) {
			//	ctrl.isReadOnly = true;
		}
	}

	ctrl.isAllCompletedStatus = false;

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

	ctrl.saveToDo = function(wo){

		if (wo.todoAssignments) {
			for (var i =0; i<wo.todoAssignments.length; i++) {
				if(angular.isDefined(wo.todoAssignments[i].reqByDt) && wo.todoAssignments[i].reqByDt !== null){
					wo.todoAssignments[i].reqByDate = ctrl.formatDate(wo.todoAssignments[i].reqByDt);
				}else{
					wo.todoAssignments[i].reqByDate = "";

				}

				if(wo.todoAssignments[i].status == "Completed"){
					ctrl.isAllCompletedStatus = true;
				}else{
					ctrl.isAllCompletedStatus = false;
				}
			}

			if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS'){
				for (var i =0; i<wo.todoAssignments.length; i++) {
					if(wo.todoAssignments[i].status == "Completed"){
						ctrl.isAllCompletedStatus = true;
					}else{
						ctrl.isAllCompletedStatus = false;
						break;
					}
				}

			}
		}

		if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal/web/scheduler/saveWorkOrderForViewToDoStatus";
		}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal/web/depot/updateTodo";

		}

		var req = {
				method: 'POST',
				url: serviceUrl,
				headers: {
					'Content-Type': 'application/json'
				},
				data: JSON.stringify(wo)
		};
		restTemplate.callService(req, function (response) {
			$scope.fetchedData = response.data;
			if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS'){			
				if(wo && ctrl.isAllCompletedStatus){
					wo.completed = 'Y';
				}else if(wo && !ctrl.isAllCompletedStatus){
					wo.completed = 'N';
				}
			}

			if(angular.isDefined(wo.exctnPckgName) && wo.exctnPckgName !== ""){
				wo.successSavedMsg = "Package has been saved successfully";
			}else{
				wo.successSavedMsg = "Work order task has been saved successfully";
			}
			wo.savedMsgVisible = true;

		}, null);

		ctrl.handleDataChange({event: {eventId:'TO_DO_DETAILS_SAVED'}});
	};

	ctrl.fetchToDoAgainstWO = function(wo) {
		if(ctrl.activeContext == 'VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal/web/scheduler/fetchWOForTODOStatus";
		}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS'){
			serviceUrl = "/p6-portal/web/depot/viewTodo";
		}
//		console.log('fetching To-Dos for work order: ' + wo.workOrders[0]);
//		console.log('serviceUrl: ' + serviceUrl);
		var query = {};
		if(angular.isDefined(wo.exctnPckgName) && wo.exctnPckgName !== ""){
			query = {execPckgName:wo.exctnPckgName};
		}else{
			query = {workOrderId:wo.workOrders[0]};

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
//			console.log("Received data from server for fetchWOForTODOStatus: " + JSON.stringify(response.data));
			wo.todoAssignments = [];
			wo.todoAssignments = response.data.todoAssignments;
			wo.todoAssignments = ctrl.formathyperLinks(wo.todoAssignments);
			if (wo.todoAssignments) {
				for (var i =0; i<wo.todoAssignments.length; i++) {
					if(wo.workOrders.length==wo.todoAssignments[i].workOrders.length && wo.workOrders.length>1){
						wo.todoAssignments[i].displayWorkOrders = ['ALL'];
					}else{
						wo.todoAssignments[i].displayWorkOrders = wo.todoAssignments[i].workOrders;
					}
					if(angular.isDefined(wo.todoAssignments[i].reqByDate) && wo.todoAssignments[i].reqByDate !== null && wo.todoAssignments[i].reqByDate !== ""){					
						wo.todoAssignments[i].reqByDt = new Date(ctrl.formatYYYYMMDate(wo.todoAssignments[i].reqByDate));
					}
				}
			}
			wo.schedulingComment = response.data.schedulingComment;
			wo.deportComment = response.data.deportComment;
//			console.log("Work Order after fetch todo: " + JSON.stringify(wo));
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
		ctrl.formatYYYYMMDate = function(date) {
			var arr= [];
			arr = date.split('/');
			var dateStr = "";
			dateStr = [arr[2],arr[1],arr[0]].join('-');
			var d = new Date(dateStr),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = d.getFullYear();

			if (month.length < 2) month = '0' + month;
			if (day.length < 2) day = '0' + day;

			return [year, month, day].join('-');
		}

	}
	ctrl.formathyperLinks = function(todoAssignments) {
		for (var i = 0; i< todoAssignments.length; i++){
			todoAssignments[i].displaySupportingDoc = ctrl.formateUrl(todoAssignments[i].supportingDoc);
			if(todoAssignments[i].displaySupportingDoc!==null && todoAssignments[i].displaySupportingDoc!==""){
				todoAssignments[i].sdEditMode=false;
			}
		}
		return todoAssignments;
	};
	ctrl.formateUrl = function(urlvalu){
		if(urlvalu !== null && urlvalu!=="") {
			var urlRegex = /(\b(http?|https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
			urlvalu = urlvalu.replace(urlRegex, function (url) {
				return '<a href="' + url + '" target="_blank">' + url + '</a>';
			});
		}
		return urlvalu;
	}
	ctrl.showPopup = function(todo) {
		var templateUrl = "";
		if (ctrl.activeContext === 'VIEW_TODO_STATUS') {
			templateUrl = '../views/supportingDocumentsPopup.html';
		} else {
			templateUrl = '../views/supportingDocumentsPopup1.html';
		}
		ModalService.showModal({
			templateUrl: templateUrl,
			controller: "supportingDocPopupController",
			inputs: {
				todo: todo
			}

		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(result) {
				if (result.status === 'SUCCESS') {
					todo.displaySupportingDoc = ctrl.formateUrl(todo.supportingDoc);
					todo.supportingDoc = result.data.supportingDoc;
				}
			});
		});
	}
};

app.controller('supportingDocPopupController', [
	'$scope', '$element', 'todo', 'close', 
	function($scope, $element, todo, close) {

		$scope.todo = todo;
		$scope.leadCrews = $scope.leadCrewList;
		if($scope.todo.supportingDoc==null || $scope.todo.supportingDoc==""){
			$scope.todo.sdEditMode = true;
		}else{
			$scope.todo.sdEditMode = false;
		}
		$scope.toggleEditMode = function(todo){
			$scope.todo.sdEditMode = true;
		}
		$scope.toggleNonEditMode = function(todo){
			if($scope.todo.supportingDoc==null || $scope.todo.supportingDoc==""){
				$scope.todo.sdEditMode = true;
			}else{todo.sdEditMode = false;
			$scope.todo.displaySupportingDoc = $scope.formateUrl(todo.supportingDoc);
			}
		}
		$scope.formateUrl = function(urlvalu){
			if(urlvalu !== null && urlvalu!=="") {
				var urlRegex = /(\b(http?|https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
				urlvalu = urlvalu.replace(urlRegex, function (url) {
					return '<a href="' + url + '" target="_blank">' + url + '</a>';
				});
			}
			return urlvalu;
		}

		$scope.update = function(){
			close({
				status: 'SUCCESS',
				data: {
					supportingDoc: $scope.todo.supportingDoc
				}
			}, 500);
		}
		//  This close function doesn't need to use jQuery or bootstrap, because
		//  the button has the 'data-dismiss' attribute.
		$scope.cancel = function() {
			close({
				status: 'CANCELLED'
			}, 500); // close, but give 500ms for bootstrap to animate

		};
	}]);

angular.module('todoPortal').component('viewToDoResult', {
	templateUrl: '../views/viewToDoStatus.html',
	controller: viewToDoStatusController,
	bindings: {
		activeContext: '<',
		data: '<',
		handleDataChange: '&',
		functionId: '<'
	}
});