
function searchQueryController($scope,$mdDateLocale,$filter) {
	var ctrl = this;
	ctrl.isValidationErr = false;
	ctrl.isInvalidDateFormat = false;
	ctrl.showErrorMsg = "";
	ctrl.dateFormat = 'DD/MM/YYYY';
	console.log('Meta Data passed from parent: ' + JSON.stringify(this.metadata));
	
	ctrl.depotList = [];
	ctrl.depotCrewMap = {};
	ctrl.crewList = [];
	ctrl.allCrewList = [];
	if (angular.isDefined(ctrl.metadata) && angular.isDefined(ctrl.metadata.depotCrewMap)) {
		console.log("Populating depot and crew");
		ctrl.depotCrewMap = ctrl.metadata.depotCrewMap;
		for (depot in ctrl.depotCrewMap) {
			console.log("Populating depot" + depot);
			ctrl.depotList.push(depot);
			var crews = ctrl.depotCrewMap[depot];
			if (crews != null) {
				crews = crews.unique();
				for (var j = 0; j < crews.length; j++){
					ctrl.allCrewList.push(crews[j]);
				}
			}
			
		}
		ctrl.crewList = ctrl.allCrewList;
		console.log("Depot List: " + JSON.stringify(ctrl.depotList));
		console.log("Crew List: " + JSON.stringify(ctrl.crewList));
	}
	
	ctrl.onDepotChange = function() {
		console.log("On Depot Change Called with depot names" + JSON.stringify(ctrl.selectedDepotList));
		ctrl.crewList = [];
		if (angular.isDefined(ctrl.selectedDepotList) && ctrl.selectedDepotList.length > 0) {
			for (var i =0; i < ctrl.selectedDepotList.length; i++) {
				var depot = ctrl.selectedDepotList[i];
				var crews = ctrl.depotCrewMap[depot];
				if (crews != null) {
					for (var j = 0; j < crews.length; j++){
						ctrl.crewList.push(crews[j]);
					}
				}
			}
		} else {
			ctrl.crewList = ctrl.allCrewList;
		}
	}
	
	// FORMAT THE DATE FOR THE DATEPICKER
	$mdDateLocale.formatDate = function(date) {
   	 console.log('date in formatDate: ' + JSON.stringify(date));
    	if(angular.isDefined(date) && date !== null){
    		ctrl.isInvalidDateFormat = false;
    		return $filter('date')(date, "dd/MM/yyyy");
    	}else{
    		return null;
    	}
    };
    $mdDateLocale.parseDate = function(dateString) {
    	 var m = moment(dateString, ctrl.dateFormat, true);
       	 console.log('m in parseDate: ' + JSON.stringify(m));
       	 if(!m.isValid()){
       		ctrl.isInvalidDateFormat = true;
       	 }else{
        	ctrl.isInvalidDateFormat = false;
       	 }
    	 return m.isValid() ? m.toDate() : new Date(NaN);
    };

	
	this.prepareSearch = function() {
		
		console.log('search called');
		console.log('this.scheduleFromDate:' + this.scheduleFromDate);
		$scope.depots = [];
		for (var i = 0, l = this.selectedDepotList.length; i < l; i++) {
			$scope.depots.push (this.selectedDepotList[i].name);
		}
		$scope.crews = [];
		for (var i = 0, l = this.selectedCrewList.length; i < l; i++) {
			$scope.crews.push (this.selectedCrewList[i].crewId);
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
		console.log('ctrl.activeContext in search :' + JSON.stringify(ctrl.activeContext));
		if(ctrl.isInvalidDateFormat){
			ctrl.showErrorMsg = 'Date format is invalid,format should be ' + ctrl.dateFormat;
			ctrl.isValidationErr = true;
			return false;
		}
		if(ctrl.activeContext == 'ADD_SCHEDULING_TODO' || ctrl.activeContext == 'VIEW_TODO_STATUS' 
				|| ctrl.activeContext == 'CREATE_EXECUTION_PACKAGE' ){
			if(ctrl.scheduleFromDate == null || ctrl.scheduleFromDate == ""){
				ctrl.showErrorMsg = 'Planned Start From Date is required';
				ctrl.isValidationErr = true;
				return false;
			}
			else{
				ctrl.isValidationErr = false;
				return true;
			}
		}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS' || ctrl.activeContext == 'DEPOT_ADD_SCHEDULING_TODO'){
			if(ctrl.scheduleFromDate == null || ctrl.scheduleFromDate == "" 
				|| ctrl.scheduleToDate == null || ctrl.scheduleToDate == ""){
				ctrl.showErrorMsg = 'Planned Start From and To Date  is required';
				ctrl.isValidationErr = true;
				return false;
			}else if(ctrl.scheduleToDate !==""){
				this.schFromDate = ctrl.formatDate(this.scheduleFromDate);
				this.schToDate = ctrl.formatDate(this.scheduleToDate);
				var isSamedate = moment(this.schFromDate).isSame(this.schToDate);
				var isPastDate = moment(this.schFromDate).isBefore(this.schToDate); 
				if(!isSamedate && !isPastDate){
					ctrl.showErrorMsg = 'To Date must be future date of From Date';
					ctrl.isValidationErr = true;
					return false;
				}else if(moment(this.schToDate).diff(this.schFromDate,'days') > 14){
					ctrl.showErrorMsg = 'Search is allowed for a maximum of 2 weeks period';
					ctrl.isValidationErr = true;
					return false;
				}else{
					ctrl.isValidationErr = false;
					return true;
				}
			}else{
				ctrl.isValidationErr = false;
				return true;
				
			}
			
		}
	};
	
	ctrl.formatDate = function(date) {
		console.log('date in formatDate of search :' + JSON.stringify(date));
		if(angular.isDefined(date) && date !== ""){
		var d = new Date(date),
		month = '' + (d.getMonth() + 1),
		day = '' + d.getDate(),
		year = d.getFullYear();
		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;
		return [year, month, day].join('-')+'T00:00:00.000Z';
		}else {
		return null;
		}
		};
	
	this.refresh();
	
}


angular.module("todoPortal").component('searchQuery', {
  templateUrl: '../views/searchQuery.html',
  controller: searchQueryController,
  bindings: {
	activeContext: '<',
    metadata: '<',
    visible: '<',
	search: '&'
  }
});