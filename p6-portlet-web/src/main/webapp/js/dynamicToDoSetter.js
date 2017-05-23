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
  	
  	// Create rows from to do maps
		if (angular.isDefined(ctrl.currentMap)) { 
			var index = 0;
			for(var todo in ctrl.currentMap) {
				ctrl.columnGroup[index%ctrl.columns].push({
					todoName:todo, 
					enabled:ctrl.currentMap[todo] ? ctrl.currentMap[todo].length > 0:false, 
					workOrders:ctrl.currentMap[todo], 
					isNewItem:false
				});
				ctrl.removeToDo(ctrl.toDos, todo);
				index++;
			}
			console.log("[DEBUG] Current Map: " + JSON.stringify(ctrl.currentMap)); 
			console.log("[DEBUG] Column Group: " + JSON.stringify(ctrl.columnGroup)); 
		}
	}
	
	ctrl.$onChanges = function (changes) {
		console.log('[DEBUG] On changes called');
		if (changes.currentMap) {
		  ctrl.init();
		}
	};
	
	ctrl.removeToDo = function(todoList, todo) {
	  
	  if (!angular.isDefined(todoList) || !angular.isDefined(todo)) {
	    console.log("[DEBUG] todoList is empty or todo is undefined");
	    return;
	  }
	  console.log("[DEBUG] removing" + todo + " from " + JSON.stringify(todoList));
	  var index = todoList.indexOf(todo);
	  if (index >= 0) {
	    todoList.splice(index, 1);
	  }
	}
	
	ctrl.init();
	
	ctrl.onChange = function() {
		ctrl.handleDataChange({map: ctrl.currentMap});
	};
	
	ctrl.removeItem = function(todoName, groupIndex) {
	  console.log("[DEBUG] Remove Item called")
	  delete ctrl.currentMap[todoName]; 
		ctrl.handleDataChange({map: ctrl.currentMap});
		ctrl.init();
	};
	ctrl.newAddIndex = -1;
	ctrl.newItem = {isNewItem:true};
	
	ctrl.addItem = function() {
	  console.log("[DEBUG] Add Item called");
	  if (!angular.isDefined(ctrl.currentMap)) {
		  ctrl.currentMap = {};
	  }
	  ctrl.newAddIndex = Object.keys(ctrl.currentMap).length;
	  ctrl.columnGroup[ctrl.newAddIndex%ctrl.columns].push(ctrl.newItem);
	}
	
	ctrl.doneAdding = function() {
	  if (!ctrl.validateNewItem(ctrl.newItem)) {
	    console.log("[DEBUG] invalid new Item");
	    return;
	  }
	  console.log("[DEBUG] Done Adding");
	  ctrl.currentMap[ctrl.newItem.todoName] = ctrl.newItem.workOrders;
	  ctrl.handleDataChange({map: ctrl.currentMap});
	  ctrl.init();
	  ctrl.newAddIndex = -1;
	  ctrl.newItem = {isNewItem:true};
	}
	
	ctrl.validateNewItem = function(newItem) {
	  if (!angular.isDefined(newItem.todoName)) {
	    ctrl.addErrorMessage = "Please select a valid to do";
	    ctrl.isAddError = true;
	    return false;
	  }
	  ctrl.isAddError = false;
	  return true;
	}
	
	/*ctrl.onChangeWorOrders = function(row) {
	  console.log("[DEBUG] On change work order called");
	  if (angular.isDefined(row) && angular.isDefined(row.workOrders)) {
	    row.enabled = row.workOrders.length > 0;
	  } else {
	    row.enabled = false;
	  }
	}*/
	
	ctrl.addToDo = function(todoName) { 
	  console.log("[DEBUG] Add to do called");
	  if (!angular.isDefined(ctrl.toDos)) {
	    ctrl.toDos = [];
	  }
	  ctrl.toDos.push(todoName);
	}

}


angular.module('todoPortal').component('dynamicToDoSetter', {
  templateUrl: '../views/dynamicToDoSetter.html',
  controller: dynamicToDoSetterController,
  bindings: {
	  columns: '<',
	  workOrders: '<',
	  toDos: '<',
	  currentMap: '<',
	  handleDataChange: '&'
  }
});