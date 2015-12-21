/**
 * Daniel
 */
function Fragment(init){
    var self = this;
    self.fragment = ko.observable(init);
}

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
    self.testf = ko.observableArray();
    for(var i = 0; i < initialFragments.length; i++){
        self.testf.push(new Fragment(initialFragments[i]));
    }

    self.setTheFileType = function(type){
        self.fileType(type);
    }

    self.saveFragments = function(){
        var x = ko.toJSON({ id: self.id, fragments: self.testf() });
        console.log(x);
        $.ajax({
            type: "POST",
            url: "/json/saveFragments",
            data: x,
            success: function(result){

            },
            contentType: "application/json"
        });
    }
}

//

function StoredJobViewModel(){
    var self = this;

    self.storedJobs = ko.observableArray();

    self.filetypes = ko.observableArray([]);

    self.currentJob = ko.observable(new StoredJob(-1, "dummy", "dummy", "dummy", "dummy", ["dummy1", "dummy2"]));

    self.showModal = function(data){
        self.currentJob(data);
        //self.currentJob.valueHasMutated();
        console.log(data);
        console.log(self.currentJob());
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
            self.storedJobs.push(new StoredJob(jobs[i].id,
                                            jobs[i].name,
                                            jobs[i].language,
                                            jobs[i].type,
                                            self.filetypes()[0],
                                            jobs[i].fragments))
        }
    });

    self.showFolderModal = function(job){
        $("#folderModal").modal('show');
    };

    self.setCurrentJob = function(job){
        self.currentJob(job);
    }
}

ko.applyBindings(new StoredJobViewModel());