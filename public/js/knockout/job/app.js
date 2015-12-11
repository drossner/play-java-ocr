/**
 * Created by florian on 29.11.15.
 */
// Row Class
var Job = function(id, initialJob){
    var self = this;
    self.id = id;
    self.job = ko.observable(initialJob);

    self.rotation = ko.observable(0);
}

function Language(id, initialLanguage){
    var self = this;
    self.id = id;
    self.language = ko.observable(initialLanguage);
}


function initModalData(modal) {
    console.log("ressetting");
    resetFilters();
    if(modal.entryData.rotation() !== 0){
         console.log("muss gedreht werden! " + modal.entryData.rotation());
        rotateLeft();
    }
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
        header: "Templating",
        closeLabel: "Cancel",
        primaryLabel: "Save",
        entryData: ko.observable(undefined),
        show: ko.observable(false),
        /* Set to true to show initially */
        onClose: function () {
            self.onModalClose();
        },
        onAction: function () {
            self.onModalAction();
        }
    };

    console.log(ko.isObservable(self.modal.entryData));

    self.showModal = function (jobDataModel) {
        self.modal.entryData = jobDataModel;
        self.modal.show(true);

        initModalData(self.modal);
    };

    self.onModalClose = function () {
        // alert("CLOSE!");
    };
    self.onModalAction = function () {
        // alert("ACTION!");
        self.modal.show(false);

        self.modal.entryData.rotation(90);
    };
}


ko.applyBindings(new JobHistoryViewModel());