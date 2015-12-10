/**
 * Created by florian on 29.11.15.
 */
// Row Class
var Job = function(id, initialJob){
    var self = this;
    self.template = "modal-sample-1";

    self.id = id;
    self.job = ko.observable(initialJob);
};

function Language(id, initialLanguage){
    var self = this;
    self.id = id;
    self.language = ko.observable(initialLanguage);
};

function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    self.languages = ko.observableArray([]);
    self.jobTypes = ko.observableArray([]);

    loadData(self);

    self.showModal = function(job) {
        var options = this;
        console.log(job);
        options.viewModel =  job;
        if (typeof options === "undefined") throw new Error("An options argument is required.");
        if (typeof options.viewModel !== "object") throw new Error("options.viewModel is required.");

        var viewModel = options.viewModel;
        var template = options.template || viewModel.template;
        var context = options.context;

        if (!template) throw new Error("options.template or options.viewModel.template is required.");

        return createModalElement(template, viewModel)
            .pipe($) // jQueryify the DOM element
            .pipe(function($ui) {
                var deferredModalResult = $.Deferred();
                addModalHelperToViewModel(viewModel, deferredModalResult, context);
                showTwitterBootstrapModal($ui);
                whenModalResultCompleteThenHideUI(deferredModalResult, $ui);
                whenUIHiddenThenRemoveUI($ui);
                return deferredModalResult;
            });
    };
};

JobHistoryViewModel.prototype.template = "modal-sample-1";

ko.applyBindings(new JobHistoryViewModel());