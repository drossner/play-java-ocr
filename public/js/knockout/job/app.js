/**
 * Created by florian on 29.11.15.
 */
// Row Class
function Job(id, initialJob){
    var self = this;
    self.id = id;
    self.job = ko.observable(initialJob);

    self.folderId = ko.observable();

    self.preProcessing = ko.observableArray([]);
    self.areas = ko.observableArray([]);
}

function Language(id, initialLanguage){
    var self = this;
    self.id = id;
    self.language = ko.observable(initialLanguage);
}

var currentJob;

function rotate(value){
    console.log("rotate: " + value);
    rotatePreProcess(value);
}

function brightness(value){
    console.log("brightness: " + value);
    $('#brightness').slider('value', value);
    applyFilters();
    caman.render();
}

function contrast(value){
    console.log("contrast: " + value);
    $('#contrast').slider('value', value);
    applyFilters();
    caman.render();
}

function saveData() {
    console.log("save data: " + currentJob);

    currentJob.preProcessing.removeAll();
    currentJob.areas.removeAll();

    if(rotation != 0){
        currentJob.preProcessing.push(new PreProcessor(rotate, rotation));
        console.log("saving: rotate: " + rotation);
    }

    var sliderBrightnessValue = $('#brightness').slider('value');
    if(sliderBrightnessValue != 0){
        currentJob.preProcessing.push(new PreProcessor(brightness, sliderBrightnessValue));
        console.log("saving: brightness: " + sliderBrightnessValue);
    }

    var sliderContrastValue =  $('#contrast').slider('value');
    if(sliderContrastValue != 0){
        currentJob.preProcessing.push(new PreProcessor(contrast, sliderContrastValue));
        console.log("saving: contrast: " + sliderContrastValue);
    }

    var areas = getAreas();
    console.log(areas);
    for(var i = 0; i < areas.length; i++){
        var area = areas[i];
        console.log(area.type);
        currentJob.areas.push(new SelectArea(area.x, area.x + area.width, area.y, area.y + area.height, area.type));
    }
}

function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    self.languages = ko.observableArray([]);
    self.jobTypes = ko.observableArray([]);

    loadData(self);

    function initModal(job) {
        for(var i = 0; i < job.preProcessing().length; i++){
            job.preProcessing()[i].process();
        }

        for(var i = 0; i < job.areas().length; i++){
            var area = job.areas()[i];
            console.log(area);

            var options = {
                x: area.xStart,
                width: area.xEnd - area.xStart,
                y: area.yStart,
                height: area.yEnd - area.yStart,
                type: area.type
            };

            createArea(options);
        }
    }

    self.showModal = function(job){
        console.log(job);

        resetFilters();
        reset();
        initModal(job);
        currentJob = job;

        $("#modal-sample-1").modal('show');
    }

    self.processJobs = function () {
        console.log(self.jobs());
        console.log(self.jobs()[0].areas());
        //var data = self.jobs();

        //$.getJSON("/json/processJobs", data);

        $.ajax("/json/processJobs", {
            data: ko.toJSON({ jobs: self.jobs }),
            type: "post", contentType: "application/json",
            success: function(result) { alert(result) }
        });
    }
}

ko.applyBindings(new JobHistoryViewModel());