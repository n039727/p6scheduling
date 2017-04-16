
function searchQueryController($scope) {
	var ctrl = this;
	
	console.log('Meta Data passed from parent: ' + JSON.stringify(this.metadata));
	
	this.prepareSearch = function() {
		console.log('search called');
		var queryObj = {
			depots: this.selectedDepotList,
			crews: this.selectedCrewList,
			wo: this.wo,
			scheduleFromDate: this.scheduleFromDate,
			scheduleToDate: this.scheduleToDate
		};
		console.log('queryObject:' + JSON.stringify(queryObj));
		this.search({query:queryObj});
	};
	
	this.refresh = function() {
		console.log('refresh called');
		this.selectedDepotList = "";
		this.selectedCrewList = "";
		this.wo = "";
		this.scheduleFromDate = "";
		this.scheduleToDate = "";
	};
	
	this.refresh();
	
}


angular.module("todoPortal").component('searchQuery', {
  templateUrl: '../views/searchQuery.html',
  controller: searchQueryController,
  bindings: {
    metadata: '<',
	search: '&'
  }
});