
function searchQueryController($scope,$mdDateLocale,$filter) {
	var ctrl = this;
	ctrl.showErrorMsg = false;
	console.log('Meta Data passed from parent: ' + JSON.stringify(this.metadata));
	this.formatScheduleFromdate = function(){
	    $mdDateLocale.formatDate = function(date) {
	    	return $filter('date')(ctrl.scheduleFromDate, "dd/MM/yyyy");
	    };
	};
	this.formatScheduleTodate = function(){
	    $mdDateLocale.formatDate = function(date) {
	    	return $filter('date')(ctrl.scheduleToDate, "dd/MM/yyyy");
	    };
	};
	// FORMAT THE DATE FOR THE DATEPICKER

	this.prepareSearch = function() {
		
		console.log('search called');
		console.log('this.scheduleFromDate:' + this.scheduleFromDate);
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
			fromDate: ctrl.formatDate(this.scheduleFromDate),
			toDate: ctrl.formatDate(this.scheduleToDate)
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
	
	ctrl.formatDate = function(date) {
		var d = new Date(date),
		month = '' + (d.getMonth() + 1),
		day = '' + d.getDate(),
		year = d.getFullYear();
		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;
		return [year, month, day].join('-')+'T00:00:00.000Z';;
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