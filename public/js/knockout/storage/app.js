/**
 * Daniel
 */
//row class
function StoredJob(id, name, language, type, initFiletype, initialFragments){
    var self = this;
    self.id = id;
    self.selected = ko.observable(false);
    self.filename = ko.observable(name); //selnelf.language = ko.observable(language);
    self.language = ko.observable(language);
    self.type = ko.observable(type);
    self.fileType = ko.observable(initFiletype);

    self.fragments = ko.observableArray(initialFragments);

    self.setTheFileType = function(type){
        self.fileType(type);
    }
}

//

function StoredJobViewModel(){
    var self = this;

    self.storedJobs = ko.observableArray();

    self.filetypes = ko.observableArray([]);

    //loadData(self);

    self.showModal = function(data){
        console.log(data);
        $("#afterWork").modal('show');
    };

    //load data from server
    $.getJSON("/json/getProcessedJobs", function(result){
        console.log(result);
        var filetypes = result.filetypes;
        for(var i = 0; i < filetypes.length; i++){
            self.filetypes.push(filetypes[i]);
        }
        var jobs = result.nodes;
        for(var i = 0; i < jobs.length; i++){
            self.storedJobs.push(StoredJob(i,
                                            jobs[i].name,
                                            jobs[i].language,
                                            jobs[i].type,
                                            self.filetypes()[0]));
        }
    });

    self.showFolderModal = function(data){
        console.log(data);
        $("#folderModal").modal('show');
    };
}

ko.applyBindings(new StoredJobViewModel());