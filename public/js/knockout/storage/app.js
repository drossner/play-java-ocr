/**
 * Daniel
 */

//public js vars
var folderID;
var currJob;

//knockout
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

function StoredJobViewModel(){
    var self = this;

    self.storedJobs = ko.observableArray();

    self.filetypes = ko.observableArray([]);

    self.currentJob = ko.observable(new StoredJob(-1, "dummy", "dummy", "dummy", "dummy", ["dummy1", "dummy2"]));
    self.currentJob.subscribe(function(newValue) {
        currJob = newValue;
    });

    self.showModal = function(data){
        self.currentJob(data);
        //currentJob.valueHasMutated();
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
        self.currentJob(job);
        console.log(self.currentJob());
        $("#folderModal").modal('show');
    };

    self.setCurrentJob = function(job){
        self.currentJob(job);
    }

    self.deleteMarked = function(){
        ko.utils.arrayForEach(self.storedJobs(), function(job) {
            if(job.selected()){
                $.ajax({
                    url: '/json/deleteJob?id='+job.id,
                    type: 'DELETE',
                    success: function(result) {
                        self.storedJobs.remove(job);
                    }
                });
            }

        });
    }

    self.downloadMarked = function(){
        console.log("get donwloadlinks");
        ko.utils.arrayForEach(self.storedJobs(), function(job) {
            if(job.selected()){
                $.getJSON( "/json/getDownloadlink?id="+job.id+"&ext="+job.fileType(),
                    function( json ) {
                        window.location = json.url;
                });

            }
        });
    }

    self.isChecked = function(onlyOne){
        var count = 0;
        ko.utils.arrayForEach(self.storedJobs(), function(job) {
            if(job.selected()){
                count++;
            }
        });

        return (onlyOne && count === 1) || (count > 0 && !onlyOne);
    }
}

ko.applyBindings(new StoredJobViewModel());
