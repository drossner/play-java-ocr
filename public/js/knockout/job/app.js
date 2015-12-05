/**
 * Created by florian on 29.11.15.
 */
// Row Class
function Job(id, initialJob){
    var self = this;
    self.id = id;
    self.job = ko.observable(initialJob);

    function invisible(){

    }
}


function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    $.getJSON("/json/jobType", function(result){
        self.jobTypes = result;
    });

    $.getJSON("/json/jobLanguage", function(result){
        self.languages = result;
    });

    $.getJSON("/json/jobHistory", function(result){
        for(var i = 0; i < result.length; i++){
            self.jobs.push(new Job(i + 1, result[i]));
        }
    });
}

ko.applyBindings(new JobHistoryViewModel());