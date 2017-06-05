function materialRequisitionResultController($scope, restTemplate, userAccessService) {
	var ctrl = this;
	
	// Authorization implementation
	ctrl.isReadOnly = false;
	if (userAccessService.isAuthEnabled()) {
		if (!userAccessService.hasUpdateableAccess(ctrl.functionId)) {
			//ctrl.isReadOnly = true;
		}
	}	
	console.log('data received in material req.: ' + JSON.stringify(ctrl.data));
	//ctrl.woMatReqMap = {};
	//ctrl.woReqs = [];
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
			wo.woMatReqMap = response.data.materialRequisitionMap;
			console.log("wo.woMatReqMap: " + JSON.stringify(wo.woMatReqMap));
			if(wo.woMatReqMap){
				wo.woReqs = [];
				for ( wos in wo.woMatReqMap) {
					wo.woReqs.push({woNum:wos,reqList: wo.woMatReqMap[wos]});				
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
	  functionId: '<'
  }
});