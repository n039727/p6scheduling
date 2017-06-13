function dynamicToDoSetterController($scope) {
	

	console.log("[DEBUG] Dynamic to do setter called"); 
	var ctrl = this;
	
	// Create Column Groups
	ctrl.init = function() {
		console.log("[DEBUG] Current Map changed");
		if (angular.isDefined(ctrl.columns)) {
			console.log("[DEBUG] Number of columns in dynamic to do setter: " + ctrl.columns);
			ctrl.columnGroup = [];
			for (var i = 0; i < ctrl.columns; i++) { 
				ctrl.columnGroup.push([]);
			}
		}
		ctrl.workOrderListWithAll = [];
		// Create 'All' in the work order list
		if (angular.isDefined(ctrl.workOrders) && ctrl.workOrders != null) {
			if (ctrl.workOrders.length > 1) {
				ctrl.workOrderListWithAll = ['All'];
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
					workOrders = ['All'];
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
			console.log("[DEBUG] Current Map: " + JSON.stringify(ctrl.currentMap)); 
			console.log("[DEBUG] Column Group: " + JSON.stringify(ctrl.columnGroup)); 
			console.log("[DEBUG] ctrl.workOrderListWithAll: " + JSON.stringify(ctrl.workOrderListWithAll)); 
		}
	};
	
	ctrl.$onChanges = function (changes) {
		console.log('[DEBUG] On changes called');
		ctrl.init();
	};
	
	ctrl.removeToDo = function(todoList, todo) {
	  
	  if (!angular.isDefined(todoList) || !angular.isDefined(todo)) {
	    console.log("[DEBUG] todoList is empty or todo is undefined");
	    return;
	  }
//	  console.log("[DEBUG] removing" + todo + " from " + JSON.stringify(todoList));
	  var index = todoList.indexOf(todo);
	  if (index >= 0) {
	    todoList.splice(index, 1);
	  }
	};
	
	ctrl.init();
	
	ctrl.onChange = function(workOrders) {
		//ctrl.handleDataChange({map: ctrl.currentMap});
		//ctrl.handleEvent({eventId:"DATA_CHANGE", data:{map: ctrl.currentMap}})
		console.log("[DEBUG] ctrl.columnGroup in onChange: " + JSON.stringify(ctrl.columnGroup));
		console.log("[DEBUG] workOrders in onChange: " + JSON.stringify(workOrders));
		if(angular.isDefined(workOrders)){
			var allIndex = workOrders.indexOf('ALL');
			if (allIndex > -1 && allIndex < ctrl.workOrderListWithAll.length - 1) {
				workOrders.splice(allIndex, 1);
			} else if (allIndex > -1 && allIndex == ctrl.workOrderListWithAll.length - 1) {
				workOrders = ['ALL'];
			} else if (ctrl.workOrderListWithAll.length-1 == workOrders.length) {
				if(ctrl.columnGroup.length > 0){
					var tempColumnGroup = [];
					var tempTodoName = "";
					var isTempNewItem = false;
					tempColumnGroup = ctrl.columnGroup[0];
					for(var i=0;i< tempColumnGroup.length;i++){
						if(ctrl.isEqArrays(tempColumnGroup[i].workOrders ,workOrders)){
							tempColumnGroup[i].workOrders = ['All'];
							tempTodoName = tempColumnGroup[i].todoName;
							isTempNewItem = tempColumnGroup[i].isNewItem;
							ctrl.columnGroup[i] = tempColumnGroup;
						}
					}
					if(!isTempNewItem){
						ctrl.currentMap[tempTodoName] = workOrders;
					}
					
				}
			}
		}
		
	};
	
	ctrl.removeItem = function(todoName, groupIndex) {
	  console.log("[DEBUG] Remove Item called")
	  delete ctrl.currentMap[todoName]; 
		//ctrl.handleDataChange({map: ctrl.currentMap});
		ctrl.handleEvent({eventId:"DATA_CHANGE", data:{map: ctrl.currentMap}});
	    ctrl.isToDoRemoved = true;
		ctrl.init();
	};
	ctrl.newAddIndex = -1;
	ctrl.newItem = {isNewItem:true};
	
	ctrl.addItem = function() {
	  console.log("[DEBUG] Add Item called");
	  if (!angular.isDefined(ctrl.currentMap)) {
		  ctrl.currentMap = {};
	  }
	  ctrl.isToDoRemoved = false;
	  ctrl.handleEvent({eventId:"ADD_TO_DO_IN_PROGRESS", data:{}})
	  ctrl.newAddIndex = Object.keys(ctrl.currentMap).length;
	  ctrl.columnGroup[ctrl.newAddIndex%ctrl.columns].push(ctrl.newItem);
	 console.log("[DEBUG] ctrl.columnGroup in addItem: " + JSON.stringify(ctrl.columnGroup));
	};
	
	ctrl.doneAdding = function() {
	  if(!ctrl.isToDoRemoved){
		  if (!ctrl.validateNewItem(ctrl.newItem)) {
			    console.log("[DEBUG] invalid new Item");
			    return;
			  }
		  
		  ctrl.currentMap[ctrl.newItem.todoName] = (ctrl.newItem.workOrders.indexOf('All') >= 0) ? ctrl.workOrders: ctrl.newItem.workOrders;
//		  ctrl.handleDataChange({map: ctrl.currentMap});
		  ctrl.newItem = {isNewItem:true};
	  }	  
	  console.log("[DEBUG] Done Adding");
	  ctrl.handleEvent({eventId:"DATA_CHANGE", data:{map: ctrl.currentMap}})
	  ctrl.handleEvent({eventId:"ADD_TO_DO_COMPLETED", data:{}})
	  
	  ctrl.init();
	  ctrl.newAddIndex = -1;
	};
	
	ctrl.validateNewItem = function(newItem) {
	  if (!angular.isDefined(newItem.todoName)) {
	    ctrl.addErrorMessage = "Please select a valid to do";
	    ctrl.isAddError = true;
	    return false;
	  }
	  ctrl.isAddError = false;
	  return true;
	};
	

	ctrl.addToDo = function(todoName) { 
	  console.log("[DEBUG] Add to do called");
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