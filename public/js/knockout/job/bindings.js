/**
 * Created by florian on 29.11.15.
 */

/* Custom binding for making modals */
ko.bindingHandlers.bootstrapModal = {
    init: function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        console.log("rendere!");

        // Für Modal Step
        $(element).modalSteps();


        var props = valueAccessor(),
            vm = bindingContext.createChildContext(viewModel);
        ko.utils.extend(vm, props);
        vm.close = function () {
            vm.show(false);
            vm.onClose();
        };
        vm.action = function () {
            vm.onAction();
        };
        ko.utils.toggleDomNodeCssClass(element, "modal fade", true);
        ko.renderTemplate("commentsModal", vm, null, element);
        var showHide = ko.computed(function () {
            $(element).modal(vm.show() ? 'show' : 'hide');
        });

        console.log("fertig");
        return {
            controlsDescendantBindings: true
        };
    }
};

ko.bindingHandlers.fadeVisible = {
    init: function(element, valueAccessor) {
        // Start visible/invisible according to initial value
        var shouldDisplay = valueAccessor();
        $(element).toggle(shouldDisplay);
    },
    update: function(element, valueAccessor) {
        // On update, fade in/out
        var shouldDisplay = valueAccessor();
        shouldDisplay ? $(element).fadeIn() : $(element).fadeOut();
    }
};