(function() {
	'use strict';
	
	/**
	* Initializes view model and apply bindings
	*/
	function initializeDemoData() {
        // Get container for binding view model
		var container = document.getElementById('bind-container');

        // Create list view model
		var listViewModel = new ListViewModel();

		$.getJSON("/json/getUserFolders", function(result){
			// Add folders to list
				for (var j = 0; j < result.length; j++) {
					var folder = result[j],
							folderViewModel = new ItemViewModel(listViewModel, true, folder.id, folder.title, folder.parentId);

					listViewModel.allItems.push(folderViewModel);
				}


			// Initialize list view model
			listViewModel.initialize();

		});


		// Bind view model to view
		ko.applyBindings(listViewModel, container);
	}
		
	initializeDemoData();
})();