/**
 * Created by florian on 29.11.15.
 */
// Row Class
function Job(id, initialJob, language, jobType){
    var self = this;

    self.id = id;

    self.language = ko.observable(language);
    self.jobType = ko.observable(jobType);
    self.job = ko.observable(initialJob);

    self.setTheLanguage = function(lang){
        self.language(lang);
    };

    self.setTheJobType = function(type){
        self.jobType(type);
    };

    self.folderId = ko.observable("");

    self.image = ko.observable("");

    self.preProcessing = ko.observableArray([]);
    self.areas = ko.observableArray([]);

    var path = "/json/getImageFromJobID/" + self.job().id;
    self.image(path);

    self.dragging = ko.observable(false);
    self.isSelected = ko.observable(false);
}

/*
function Language(id, initialLanguage){
    var self = this;
    self.id = id;

    self.language = initialLanguage;
    console.log(self.language);
}
*/

var currentJob;
var preProcessing;

function toDraggables(values) {
    return ko.utils.arrayMap(values, function (value) {
        return {
            value: value,
            dragging: ko.observable(false),
            isSelected: ko.observable(false),
            startsWithVowel: function () {
                return !!this.value.match(/^(a|e|i|o|u|y)/i);
            }
        };
    });
}

function rotate(value){
    console.log("rotate: " + value);
    preProcessing.rotatePreProcess(value);
}

function brightness(value){
    console.log("brightness: " + value);
    $('#brightness').slider('value', value);
    preProcessing.applyFilters();
    preProcessing.caman.render();
}

function contrast(value){
    console.log("contrast: " + value);
    $('#contrast').slider('value', value);
    preProcessing.applyFilters();
    preProcessing.caman.render();
}

function saveData() {
    console.log("save data: " + currentJob);

    currentJob.preProcessing.removeAll();
    currentJob.areas.removeAll();

    if(preProcessing.getRotation() != 0){
        currentJob.preProcessing.push(new PreProcessor(rotate, "rotate", preProcessing.getRotation()));
        console.log("saving: rotate: " + preProcessing.getRotation());
    }

    var sliderBrightnessValue = $('#brightness').slider('value');
    if(sliderBrightnessValue != 0){
        currentJob.preProcessing.push(new PreProcessor(brightness, "brightness", sliderBrightnessValue));
        console.log("saving: brightness: " + sliderBrightnessValue);
    }

    var sliderContrastValue =  $('#contrast').slider('value');
    if(sliderContrastValue != 0){
        currentJob.preProcessing.push(new PreProcessor(contrast, "contrast", sliderContrastValue));
        console.log("saving: contrast: " + sliderContrastValue);
    }

    var areas = getAreas();
    var canvasWidth = $('#templating').width();
    var canvasHeight = $('#templating').height();
    console.log(areas);
    for(var i = 0; areas != undefined && i < areas.length; i++){
        var area = areas[i];
        console.log(area.type);
        currentJob.areas.push(new SelectArea(area.x/canvasWidth, (area.x + area.width)/canvasWidth, area.y/canvasHeight, (area.y + area.height)/canvasHeight, area.type, canvasHeight, canvasWidth));
    }
}

function initModal(job) {
    preProcessing.resetFilters();
    reset();

    for(var i = 0; i < job.preProcessing().length; i++){
        job.preProcessing()[i].process();
    }

    for(var i = 0; i < job.areas().length; i++){
        var area = job.areas()[i];
        console.log(area);

        var options = {
            x: area.xStart * area.canvasWidth,
            width: (area.xEnd - area.xStart) * area.canvasWidth,
            y: area.yStart * area.canvasHeight,
            height: (area.yEnd - area.yStart) * area.canvasHeight,
            type: area.type
        };

        createArea(options);
    }
}

function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    self.combined = ko.observable(false);

    self.languages = ko.observableArray([]);
    self.jobtypes = ko.observableArray([]);

    loadData(self);

    self.showModal = function(job){
        preProcessing = new PreProcessing();

        console.log(job);
        setImageSource(job.image(), initModal, job);
        //$("#canvas").src= job.image;

        currentJob = job;

        $("#modal-sample-1").modal('show');
    };

    self.showFolderModal = function(job){
        currentJob = job;

        $("#folderModal").modal('show');
    };

    self.delete = function(job){
        console.log("delete: " + job);

        var path = "/json/deleteJob/" + job.job().id;
        $.get(path, function(result){
            self.jobs.remove(job);
        });
    };

    self.processJobs = function () {
        console.log(self.jobs());
        console.log(self.jobs()[0].areas());
        console.log(self.combined());

        $.ajax("/json/processJobs", {
            data: ko.toJSON({ jobs: self.jobs, combined: self.combined() }),
            type: "post", contentType: "application/json",
            success: function(result) { console.log(result) }
        });
    }

    self.dragStart = function (item) {
        item.dragging(true);
    };

    self.dragEnd = function (item) {
        item.dragging(false);
    };

    self.reorder = function (event, dragData, zoneData) {
        if (dragData !== zoneData.item) {
            var zoneDataIndex = zoneData.items.indexOf(zoneData.item);
            zoneData.items.remove(dragData);
            zoneData.items.splice(zoneDataIndex, 0, dragData);
        }
    };
}

ko.applyBindings(new JobHistoryViewModel());