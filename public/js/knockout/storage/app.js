/**
 * Created by Sebastian Lauterkorn on 19.12.2015.
 * && awesome flo && awesome daniel
 */
//row class
function StoredJob(name, language, type, initFiletype){
    var self = this;
    self.name = ko.observable(name);
    self.language = ko.observable(language);
    self.type = ko.observable(type);
    self.fileType = ko.observable(initFiletype);
}

//

function StoredJobViewModel(){
    var self = this;

    self.storedJobs = ko.observableArray();

    self.filetypes = ko.observableArray([]);

    //loadData(self);

    self.showModal = function(job){
        $("#afterWork").modal('show');
    }

    //load data from server
    $.getJSON("/json/getProcessedJobs", function(result){
        console.log(result);
        var filetypes = result.filetypes;
        for(var i = 0; i < filetypes.length; i++){
            self.filetypes.push(filetypes[i]);
        }
        var jobs = result.nodes;
        for(var i = 0; i < jobs.length; i++){
            self.storedJobs.push(StoredJob(jobs[i].name,
                                            jobs[i].language,
                                            jobs[i].type,
                                            jobs[i].filetypes[0]));
        }
    });
}

ko.applyBindings(new StoredJobViewModel());