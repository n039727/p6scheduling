function dynamicToDoSetterController($scope) {
	var ctrl = this;

	// Create Column Groups
	ctrl.init = function() {
		ctrl.disableRemove = false;
		if (angular.isDefined(ctrl.columns)) {
			ctrl.columnGroup = [];
			for (var i = 0; i < ctrl.columns; i++) { 
				ctrl.columnGroup.push([]);
			}
		}
		ctrl.workOrderListWithAll = [];
		// Create 'All' in the work order list
		if (angular.isDefined(ctrl.workOrders) && ctrl.workOrders != null) {
			if (ctrl.workOrders.length > 1) {
				ctrl.workOrderListWithAll = ['ALL'];
				for (var i =0; i < ctrl.workOrders.length; i++) {
					ctrl.workOrderListWithAll.push(ctrl.workOrders[i]);
				}
			} else {
				ctrl.workOrderListWithAll = ctrl.workOrders;
			}
		}

		// Create rows from to do maps
		if (angular.isDefined(ctrl.currentMap)) { 
			var index = 0;
			for(var todo in ctrl.currentMap) {
				var workOrders = ctrl.currentMap[todo];
				if (workOrders != null && workOrders.length != 1 && workOrders.length == ctrl.workOrders.length) {
					workOrders = ['ALL'];
				}
				ctrl.columnGroup[index%ctrl.columns].push({
					todoName:todo, 
					enabled:ctrl.currentMap[todo] ? ctrl.currentMap[todo].length > 0:false, 
							workOrders:workOrders, 
							isNewItem:false
				});
				ctrl.removeToDo(ctrl.toDos, todo);
				index++;
			}
		}
	};

	ctrl.$onChanges = function (changes) {
		ctrl.init();
	};

	ctrl.removeToDo = function(todoList, todo) {

		if (!angular.isDefined(todoList) || !angular.isDefined(todo)) {
			return;
		}
		var index = todoList.indexOf(todo);
		if (index >= 0) {
			todoList.splice(index, 1);
		}
	};

	ctrl.init();

	ctrl.onChange = function(workOrders) {
		if(angular.isDefined(workOrders)){
			var allIndex = workOrders.indexOf('ALL');
			if (allIndex == 0 && allIndex < ctrl.workOrderListWithAll.length - 1) {
//				console.log("[DEBUG] allIndex == 0 in onChange: " + JSON.stringify(allIndex));
				if(ctrl.columnGroup.length > 0){
					var tempColumnGroup = [];
					var tempTodoName = "";
					var isTempNewItem = false;
					tempColumnGroup = ctrl.columnGroup[0];
					for(var i=0;i< tempColumnGroup.length;i++){
						if(ctrl.isEqArrays(tempColumnGroup[i].workOrders ,workOrders)){
							//console.log("[DEBUG] tempColumnGroup[i].workOrders1 in onChange: " + JSON.stringify(tempColumnGroup[i].workOrders));
							tempTodoName = tempColumnGroup[i].todoName;
							//console.log("[DEBUG] tempTodoName1 in onChange: " + JSON.stringify(tempTodoName));
							isTempNewItem = tempColumnGroup[i].isNewItem;
							if(workOrders.length > 1){
								workOrders.splice(allIndex, 1);
							}
							tempColumnGroup[i].workOrders = workOrders;
							ctrl.columnGroup[i] = tempColumnGroup;
							if(isTempNewItem){
								break;
							}
						}
					}
					if(!isTempNewItem){
						ctrl.currentMap[tempTodoName] = workOrders;
					}
					//console.log("[DEBUG] ctrl.columnGroup in onChange: " + JSON.stringify(ctrl.columnGroup));
					//console.log("[DEBUG] ctrl.currentMap in onChange: " + JSON.stringify(ctrl.currentMap));
				}
			} else if (allIndex > -1 && allIndex < ctrl.workOrderListWithAll.length - 1) {
				if(ctrl.columnGroup.length > 0){
//					console.log("[DEBUG] allIndex > -1 in onChange: " + JSON.stringify(allIndex));
					var tempColumnGroup = [];
					var tempTodoName = "";
					var isTempNewItem = false;
					tempColumnGroup = ctrl.columnGroup[0];
					for(var i=0;i< tempColumnGroup.length;i++){
						if(ctrl.isEqArrays(tempColumnGroup[i].workOrders ,workOrders)){
							//	console.log("[DEBUG] tempColumnGroup[i].workOrders2 in onChange: " + JSON.stringify(tempColumnGroup[i].workOrders));
							tempTodoName = tempColumnGroup[i].todoName;
							//	console.log("[DEBUG] tempTodoName2 in onChange: " + JSON.stringify(tempTodoName));
							isTempNewItem = tempColumnGroup[i].isNewItem;
							tempColumnGroup[i].workOrders = ['ALL'];
							ctrl.columnGroup[i] = tempColumnGroup;
							break;
						}
					}
					if(!isTempNewItem){
						ctrl.currentMap[tempTodoName] = ctrl.workOrders;
					}
					//console.log("[DEBUG] ctrl.columnGroup in onChange: " + JSON.stringify(ctrl.columnGroup));
					//console.log("[DEBUG] ctrl.currentMap in onChange: " + JSON.stringify(ctrl.currentMap));
				}
			} else if (ctrl.workOrderListWithAll.length-1 == workOrders.length) {
				if(ctrl.columnGroup.length > 0){
//					console.log("[DEBUG] ctrl.workOrderListWithAll.length-1 == workOrders.length in onChange: " + JSON.stringify(allIndex));
					var tempColumnGroup = [];
					var tempTodoName = "";
					var isTempNewItem = false;
					tempColumnGroup = ctrl.columnGroup[0];
					for(var i=0;i< tempColumnGroup.length;i++){
						if(ctrl.isEqArrays(tempColumnGroup[i].workOrders ,workOrders)){
							//	console.log("[DEBUG] tempColumnGroup[i].workOrders3 in onChange: " + JSON.stringify(tempColumnGroup[i].workOrders));
							tempColumnGroup[i].workOrders = ['ALL'];
							tempTodoName = tempColumnGroup[i].todoName;
							//	console.log("[DEBUG] tempTodoName3 in onChange: " + JSON.stringify(tempTodoName));

							isTempNewItem = tempColumnGroup[i].isNewItem;
							ctrl.columnGroup[i] = tempColumnGroup;
							break;
						}
					}
					if(!isTempNewItem){
						ctrl.currentMap[tempTodoName] = workOrders;
					}
					//console.log("[DEBUG] ctrl.columnGroup in onChange: " + JSON.stringify(ctrl.columnGroup));
					//console.log("[DEBUG] ctrl.currentMap in onChange: " + JSON.stringify(ctrl.currentMap));
				}
			}
		}

	};
	ctrl.removeItem = function(todoName, groupIndex) {
		delete ctrl.currentMap[todoName]; 
		//ctrl.handleDataChange({map: ctrl.currentMap});
		ctrl.handleEvent({eventId:"DATA_CHANGE", data:{map: ctrl.currentMap}});
		ctrl.isToDoRemoved = true;
		ctrl.init();
	};
	ctrl.newAddIndex = -1;
	ctrl.newItem = {isNewItem:true};

	ctrl.addItem = function() {
	  if (!angular.isDefined(ctrl.currentMap)) {
		  ctrl.currentMap = {};
	  }
	  ctrl.disableRemove = true;
	  
	  ctrl.isToDoRemoved = false;
	  ctrl.handleEvent({eventId:"ADD_TO_DO_IN_PROGRESS", data:{}})
	  ctrl.newAddIndex = Object.keys(ctrl.currentMap).length;
	  ctrl.columnGroup[ctrl.newAddIndex%ctrl.columns].push(ctrl.newItem);
	};

	ctrl.doneAdding = function() {
	  if(!ctrl.isToDoRemoved){
		  if (!ctrl.validateNewItem(ctrl.newItem)) {
			    return;
			  }
		  
		  ctrl.currentMap[ctrl.newItem.todoName] = (ctrl.newItem.workOrders.indexOf('ALL') >= 0) ? ctrl.workOrders: ctrl.newItem.workOrders;
		  ctrl.newItem = {isNewItem:true};
	  }	  
	  ctrl.handleEvent({eventId:"DATA_CHANGE", data:{map: ctrl.currentMap}})
	  ctrl.handleEvent({eventId:"ADD_TO_DO_COMPLETED", data:{}})
	  ctrl.disableRemove = false;
	  
	  ctrl.init();
	  ctrl.newAddIndex = -1;
	};

	ctrl.validateNewItem = function(newItem) {
		if (!angular.isDefined(newItem.todoName)) {
			ctrl.addErrorMessage = "Please select a valid to do";
			ctrl.isAddError = true;
			return false;
		}else if(ctrl.isNotEmpty(ctrl.currentMap)){
			for(var todo in ctrl.currentMap) {
				var workOrders = ctrl.currentMap[todo];
				if(todo===ctrl.newItem.todoName){

					ctrl.addErrorMessage = "This To-Do is a duplicate one, Please try adding different one";
					ctrl.isAddError = true;
					return false;

				}else{
					ctrl.isAddError = false;
				}
			}
			return true;
		}else{
			ctrl.isAddError = false;
			return true;
		}
	};

	ctrl.isNotEmpty = function(obj) {
		return !(obj === undefined || obj === null || Object.keys(obj).length === 0)
	};
	ctrl.addToDo = function(todoName) { 
		if (!angular.isDefined(ctrl.toDos)) {
			ctrl.toDos = [];
		}
		ctrl.toDos.push(todoName);
	};
	ctrl.isEqArrays = function(arr1, arr2) {
		if ( arr1.length !== arr2.length ) {
			return false;
		}
		for ( var i = arr1.length; i--; ) {
			if ( !ctrl.inArray( arr2, arr1[i] ) ) {
				return false;
			}
		}
		return true;
	};
	ctrl.inArray = function (array, el) {
		for ( var i = array.length; i--; ) {
			if ( array[i] === el ) return true;
		}
		return false;
	};
}


angular.module('todoPortal').component('dynamicToDoSetter', {
	templateUrl: '../views/dynamicToDoSetter.html',
	controller: dynamicToDoSetterController,
	bindings: {
		columns: '<',
		workOrders: '<',
		toDos: '<',
		currentMap: '<',
		handleEvent: '&'
	}
});