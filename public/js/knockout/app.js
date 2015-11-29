/**
 * Created by florian on 29.11.15.
 */
// Row Class
function Job(name, initialJob){
    var self = this;
    self.name = name;
    self.job = ko.observable(initialJob);
}


function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    $.getJSON("/json/jobHistory", function(result){
        for(var i = 0; i < result.length; i++){
            self.jobs.push(new Job(result[i].name, result[i]));
        }
    });

    $.getJSON("/json/jobType", function(result){
        self.jobTypes = result;
    });

    $.getJSON("/json/jobLanguage", function(result){
        self.languages = result;
    });
}

ko.applyBindings(new JobHistoryViewModel());