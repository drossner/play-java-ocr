/**
 * Created by florian on 29.11.15.
 */
// Row Class
var Job = function(id, initialJob){
    var self = this;
    self.id = id;
    self.job = ko.observable(initialJob);
}

function Language(id, initialLanguage){
    var self = this;
    self.id = id;
    self.language = ko.observable(initialLanguage);
}


function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    self.languages = ko.observableArray([]);
    self.jobTypes = ko.observableArray([]);

    $.getJSON("/json/jobType", function(result){
        self.jobTypes = result;
    });

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            console.log(result[i].name);
            self.languages.push(new Language(i + 1, result[i].name));
        }
    });

    $.getJSON("/json/jobHistory", function(result){
        for(var i = 0; i < result.length; i++){
            self.jobs.push(new Job(i + 1, result[i]));
        }
    });



    self.modal = {
        header: "Add/Edit Comment",
        comment: ko.observable(""),
        closeLabel: "Cancel",
        primaryLabel: "Save",
        entryData: undefined,
        show: ko.observable(false),
        /* Set to true to show initially */
        onClose: function () {
            self.onModalClose();
        },
        onAction: function () {
            self.onModalAction();
        }
    };

    console.log(ko.isObservable(self.modal.comment));

    self.showModal = function (jobDataModel) {
        self.modal.comment(jobDataModel.id);
        self.modal.entryData = jobDataModel;
        self.modal.show(true);
    };

    self.onModalClose = function () {
        // alert("CLOSE!");
    };
    self.onModalAction = function () {
        // alert("ACTION!");
        self.modal.entryData.comment(self.modal.comment());
        self.modal.show(false);
    };
}

ko.applyBindings(new JobHistoryViewModel());