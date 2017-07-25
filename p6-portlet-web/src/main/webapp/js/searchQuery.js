
function searchQueryController($scope,$mdDateLocale,$filter) {
	var ctrl = this;
	ctrl.isValidationErr = false;
	ctrl.isInvalidDateFormat = false;
	ctrl.showErrorMsg = "";
	ctrl.dateFormat = 'DD/MM/YYYY';
	ctrl.isActiveContext = false;
	
	ctrl.depotList = [];
	ctrl.depotCrewMap = {};
	ctrl.crewList = [];
	ctrl.allCrewList = [];
	if (angular.isDefined(ctrl.metadata) && angular.isDefined(ctrl.metadata.depotCrewMap)) {
		ctrl.depotCrewMap = ctrl.metadata.depotCrewMap;
		for (depot in ctrl.depotCrewMap) {
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
		ctrl.depotList.sort();
		ctrl.crewList.sort();
	}
	
	ctrl.onDepotChange = function() {
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
    	if(angular.isDefined(date) && date !== null){
    		ctrl.isInvalidDateFormat = false;
    		return $filter('date')(date, "dd/MM/yyyy");
    	}else{
    		return null;
    	}
    };
    $mdDateLocale.parseDate = function(dateString) {
    	 var m = moment(dateString, ctrl.dateFormat, true);
       	 if(!m.isValid()){
       		ctrl.isInvalidDateFormat = true;
       	 }else{
        	ctrl.isInvalidDateFormat = false;
       	 }
    	 return m.isValid() ? m.toDate() : new Date(NaN);
    };

	
	this.prepareSearch = function() {
		
		ctrl.depots = [];
		if(ctrl.activeContext == 'ADD_SCHEDULING_TODO' || ctrl.activeContext == 'VIEW_TODO_STATUS' 
			|| ctrl.activeContext == 'CREATE_EXECUTION_PACKAGE' || ctrl.activeContext == 'VIEW_MATERIAL_REQUISITION'){
			this.scheduleToDate = "";
			ctrl.metadata.selectedExecPckg = [];
			ctrl.metadata.selectedAll = false;
			ctrl.metadata.errorExecPckgMsgVisiable = false;
		}	
		for (var i = 0 ; i < ctrl.selectedDepotList.length; i++) {
			ctrl.depots.push (ctrl.selectedDepotList[i].trim());
		}
		ctrl.crews = [];
		for (var j = 0;  j < ctrl.selectedCrewList.length; j++) {
			ctrl.crews.push (ctrl.selectedCrewList[j].trim());
		}
		var queryObj = {
			depotList: ctrl.depots,
			crewList: ctrl.crews,
			workOrderId: this.wo,
			fromDate: ctrl.formatDate(this.scheduleFromDate),
			toDate: ctrl.formatDate(this.scheduleToDate)
		};
		if(this.validateForm()){
			this.search({query:queryObj});
		}
	};
	
	this.refresh = function() {
		this.selectedDepotList = "";
		this.selectedCrewList = "";
		this.wo = "";
		this.scheduleFromDate = "";
		this.scheduleToDate = "";
	};
	
	this.validateForm = function(){
		if(ctrl.isInvalidDateFormat){
			ctrl.metadata.isErrdataAvail = true;
			ctrl.metadata.errorMsg= 'Date format is invalid,format should be ' + ctrl.dateFormat;

			//ctrl.showErrorMsg = 'Date format is invalid,format should be ' + ctrl.dateFormat;
			//ctrl.isValidationErr = true;
			return false;
		}
		if(angular.isUndefined(ctrl.activeContext)){
			ctrl.metadata.isErrdataAvail = true;
			ctrl.metadata.errorMsg= '*Please select an Action(Scheduling/Depot) for searching the data' ;

			//ctrl.showErrorMsg = '*Please select an Action(Scheduling/Depot) for searching the data' ;
			//ctrl.isValidationErr = true;
			return false;
		}
		
		if(ctrl.activeContext == 'ADD_SCHEDULING_TODO' || ctrl.activeContext == 'VIEW_TODO_STATUS' 
				|| ctrl.activeContext == 'CREATE_EXECUTION_PACKAGE' || ctrl.activeContext == 'VIEW_MATERIAL_REQUISITION'){
			if(this.wo == null || this.wo == ""){
				if(ctrl.scheduleFromDate == null || ctrl.scheduleFromDate == ""){
					//ctrl.showErrorMsg = 'Planned Start From Date is required';
					//ctrl.isValidationErr = true;
					ctrl.metadata.isErrdataAvail = true;
					ctrl.metadata.errorMsg= 'Planned Start From Date is required';

					return false;
				}
				else{
					ctrl.metadata.isErrdataAvail = false;
					//ctrl.isValidationErr = false;
					return true;
				}
			}else{
				ctrl.metadata.isErrdataAvail = false;
				//ctrl.isValidationErr = false;
				return true;
			}
		}else if(ctrl.activeContext == 'DEPOT_VIEW_TODO_STATUS' || ctrl.activeContext == 'DEPOT_ADD_SCHEDULING_TODO' || ctrl.activeContext == 'DEPOT_VIEW_MATERIAL_REQUISITION'){
			if(this.wo == null || this.wo == ""){
				if(ctrl.scheduleFromDate == null || ctrl.scheduleFromDate == "" ){
//					ctrl.showErrorMsg = 'Planned Start From Date  is required';
//					ctrl.isValidationErr = true;
					ctrl.metadata.isErrdataAvail = true;
					ctrl.metadata.errorMsg= 'Planned Start From Date is required';
					return false;
				}else if(ctrl.scheduleToDate !=="" && ctrl.scheduleToDate !== null){
					this.schFromDate = ctrl.formatDate(this.scheduleFromDate);
					this.schToDate = ctrl.formatDate(this.scheduleToDate);
					var isSamedate = moment(this.schFromDate).isSame(this.schToDate);
					var isPastDate = moment(this.schFromDate).isBefore(this.schToDate); 
					if(!isSamedate && !isPastDate){
						//ctrl.showErrorMsg = 'To Date must be future date of From Date';
						//ctrl.isValidationErr = true;
						ctrl.metadata.isErrdataAvail = true;
						ctrl.metadata.errorMsg= 'To Date must be future date of From Date';
						return false;
					}else if(moment(this.schToDate).diff(this.schFromDate,'days') >= 14){
//						ctrl.showErrorMsg = 'Search is allowed for a maximum of 2 weeks period';
//						ctrl.isValidationErr = true;
						ctrl.metadata.isErrdataAvail = true;
						ctrl.metadata.errorMsg= 'Search is allowed for a maximum of 2 weeks period';
						return false;
					}else{
						//ctrl.isValidationErr = false;
						ctrl.metadata.isErrdataAvail = false;
						return true;
					}
				}else{
					ctrl.metadata.isErrdataAvail = false;
//					ctrl.isValidationErr = false;
					return true;
				}
			}else{
				ctrl.metadata.isErrdataAvail = false;
//				ctrl.isValidationErr = false;
				return true;
			}
		}
	};
	
	ctrl.formatDate = function(date) {
		
		if(angular.isDefined(date) && date !== "" && date !== null){
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
	search: '&',
	handleActiveContext: '&'
	
  }
});