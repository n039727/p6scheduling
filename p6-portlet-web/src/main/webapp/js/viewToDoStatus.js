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
		
		if (wo.todoAssignments) {
			for (var i =0; i<wo.todoAssignments.length; i++) {
				if(angular.isDefined(wo.todoAssignments[i].reqByDt) && wo.todoAssignments[i].reqByDt !== null){
					wo.todoAssignments[i].reqByDate = ctrl.formatDate(wo.todoAssignments[i].reqByDt);
				}else{
					wo.todoAssignments[i].reqByDate = "";
					
				}
			}
		}
		
		console.log('Save To Do called with WO: ' + JSON.stringify(wo));
		
		var req = {
			 method: 'POST',
			 url: '/p6-portal-service/scheduler/saveWorkOrderForViewToDoStatus',
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
		ctrl.handleDataChange({event: {eventId:'TO_DO_DETAILS_SAVED'}});
	};
	
	ctrl.fetchToDoAgainstWO = function(wo) {
		serviceUrl = "/p6-portal-service/scheduler/fetchWOForTODOStatus";
		console.log('fetching To-Dos for work order: ' + wo.workOrders[0]);
		console.log('wo.exctnPckgName: ' + wo.exctnPckgName);
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
		$http(req).then(function (response) {
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
			
		});
		
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