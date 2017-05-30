function sidePanelController($scope, userAccessService) {
	var ctrl = this;
	
	ctrl.setContext = function(viewContext) {
		console.log('viewContext: ' + viewContext);
		ctrl.onChangeContext({context:viewContext});
	}
	
	ctrl.isDisabled = function(functionId) {
		console.log("is disabled called for " + functionId);
		if (userAccessService.isAuthEnabled()) {
			console.log("Has access: " + userAccessService.hasAccess(functionId));
			return !userAccessService.hasAccess(functionId);
		}
		
		return false;
	}
	
}


angular.module('todoPortal').component('sidePanel', {
  templateUrl: '../views/sidePanel.html',
  controller: sidePanelController,
  bindings: {
	  activeContext: '<',
	  onChangeContext: "&"
  }
});