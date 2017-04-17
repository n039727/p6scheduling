function sidePanelController($scope) {
	var ctrl = this;
	
	ctrl.setContext = function(viewContext) {
		console.log('viewContext: ' + viewContext);
		ctrl.onChangeContext({context:viewContext});
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