function viewToDoStatusController($scope, $http) {
	var ctrl = this;
	
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
	};
	
	//ctrl.todoGrp1 = ["ESA","DEC Permit","DBYD","Gas Permit","Rail Permit","Water Permit","ENAR"];
	//ctrl.todoGrp2 = ["Traffic","Lay Down Area Arrangements","Fibre Optics","Inductions (Mine Site)","Specialised Plant / Equipment Availability","Additional Trades","Others"];
		

	ctrl.saveToDo = function(wo){
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
			alert("Scheduling TO DO saved for " + wo.workOrders[0]);
		});
		ctrl.handleDataChange();
	};
	
	ctrl.fetchToDoAgainstWO = function(wo) {
		serviceUrl = "/p6-portal-service/scheduler/fetchWOForTODOStatus";
		console.log('fetching To-Dos for work order: ' + wo.workOrders[0]);
		var query = {workOrderId:wo.workOrders[0]};
		var req = {
				method: 'POST',
				url: serviceUrl,
				headers: {
				   'Content-Type': 'application/json'
				},
				data: JSON.stringify(query)

			};
		$http(req).then(function (response) {
			console.log("Received data from server for fetchWOForTODOStatus: " + JSON.stringify(response.data[0].todoAssignments));
			wo.toDoItems = [];
			wo.toDoItems = response.data[0].todoAssignments;
			console.log("Work Order after fetch todo: " + JSON.stringify(wo));
			
		});
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