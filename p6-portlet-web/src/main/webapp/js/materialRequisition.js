function materialRequisitionResultController($scope, restTemplate, userAccessService) {
	var ctrl = this;
	
	// Authorization implementation
	ctrl.isReadOnly = false;
	if (ctrl.isAuthEnabled) {
		if (!userAccessService.hasUpdateableAccess(ctrl.functionId)) {
			ctrl.isReadOnly = true;
		}
	}
	
	console.log('data received in material req.: ' + JSON.stringify(ctrl.data));
	ctrl.woMatReqMap = {};
	ctrl.woReqs = [];
	ctrl.toggleExpansion  = function($event, wo) {
		var button = $event.target;
		
		if($('#'+button.id).hasClass("glyphicon-plus")) {
			$('#'+button.id).removeClass("glyphicon-plus");
			$('#'+button.id).addClass("glyphicon-minus");
			ctrl.fetchMaterialReqAgainstWO(wo);			
		} else if ($('#'+button.id).hasClass("glyphicon-minus")) {
			$('#'+button.id).removeClass("glyphicon-minus");
			$('#'+button.id).addClass("glyphicon-plus");
		}
	};
	ctrl.fetchMaterialReqAgainstWO = function(wo) {
		serviceUrl = "/p6-portal-service/scheduler/fetchMetReqData";
		var query = {};
		var workOrderArr = [];
		if(wo && wo.workOrders){
			for(var i=0;i< wo.workOrders.length;i++ ){
				workOrderArr.push(wo.workOrders[i].substring(0,8));
			}
		}
		query.workOrderList = workOrderArr.unique();
		console.log('fetching Material Requisition for : ' + JSON.stringify(query));
		
		var req = {
				method: 'POST',
				url: serviceUrl,
				headers: {
				   'Content-Type': 'application/json'
				},
				data: JSON.stringify(query)

			};
		
		restTemplate.callService(req, function (response) {
			console.log("Received data from server for fetchWOForTODOStatus: " + JSON.stringify(response.data));
			ctrl.woMatReqMap = response.data.materialRequisitionMap;
			console.log("ctrl.woMatReqMap: " + JSON.stringify(ctrl.woMatReqMap));
			if(ctrl.woMatReqMap){
				for ( wo in ctrl.woMatReqMap) {
					console.log("Populating wo" + wo);
					console.log("reqs: " + JSON.stringify(ctrl.woMatReqMap[wo]));
					ctrl.woReqs.push({woNum:wo,reqList: ctrl.woMatReqMap[wo]});				
				}
			}	


		}, null);
	}
	
}

angular.module('todoPortal').component('materialRequisitionResult', {
  templateUrl: '../views/materialRequisition.html',
  controller: materialRequisitionResultController,
  bindings: {
	  activeContext: '<',
	  data: '<',
	  metadata: '<',
	  handleDataChange: '&',
	  isAuthEnabled:'<',
	  functionId: '<'
  }
});