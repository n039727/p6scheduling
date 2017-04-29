
function searchQueryController($scope) {
	var ctrl = this;
	ctrl.showErrorMsg = false;
	console.log('Meta Data passed from parent: ' + JSON.stringify(this.metadata));
	
	this.prepareSearch = function() {
		console.log('search called');
		
		$scope.depots = [];
		for (var i = 0, l = this.selectedDepotList.length; i < l; i++) {
			$scope.depots.push (this.selectedDepotList[i].name);
		}
		$scope.crews = [];
		for (var i = 0, l = this.selectedCrewList.length; i < l; i++) {
			$scope.crews.push (this.selectedCrewList[i].name);
		}
		var queryObj = {
			depotList: $scope.depots,
			crewList: $scope.crews,
			workOrderId: this.wo,
			fromDate: this.scheduleFromDate,
			toDate: this.scheduleToDate
		};
		if(this.validateForm()){
			console.log('queryObject:' + JSON.stringify(queryObj));
			this.search({query:queryObj});
		}
	};
	
	this.refresh = function() {
		console.log('refresh called');
		this.selectedDepotList = "";
		this.selectedCrewList = "";
		this.wo = "";
		this.scheduleFromDate = "";
		this.scheduleToDate = "";
	};
	
	this.validateForm = function(){
		var isValid = false;
		if(ctrl.scheduleFromDate == null || ctrl.scheduleFromDate == ""){
			ctrl.showErrorMsg = true;
			//alert('Planned Start From Date must be selected');
			isValid = false;
		}else{
			isValid = true;
			ctrl.showErrorMsg = false;
		}
		return isValid;
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