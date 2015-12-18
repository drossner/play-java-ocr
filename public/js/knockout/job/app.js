/**
 * Created by florian on 29.11.15.
 */
// Row Class
function Job(id, initialJob){
    var self = this;
    self.id = id;
    self.job = ko.observable(initialJob);

    self.folderId = ko.observable();
    self.image = ko.observable("");

    self.preProcessing = ko.observableArray([]);
    self.areas = ko.observableArray([]);

    var path = "/json/getImageFromJobID/" + self.job().id;
    self.image(path);

    self.dragging = ko.observable(false);
    self.isSelected = ko.observable(false);

    /*
    $.get(path,
        function(data){
            self.image(data);
    });*/
}

function Language(id, initialLanguage){
    var self = this;
    self.id = id;
    self.language = ko.observable(initialLanguage);
}

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
}

function contrast(value){
    console.log("contrast: " + value);
    $('#contrast').slider('value', value);
    preProcessing.applyFilters();
}

function saveData() {
    console.log("save data: " + currentJob);

    currentJob.preProcessing.removeAll();
    currentJob.areas.removeAll();

    if(preProcessing.getRotation() != 0){
        currentJob.preProcessing.push(new PreProcessor(rotate, preProcessing.getRotation()));
        console.log("saving: rotate: " + preProcessing.getRotation());
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
    for(var i = 0; areas != undefined && i < areas.length; i++){
        var area = areas[i];
        console.log(area.type);
        currentJob.areas.push(new SelectArea(area.x, area.x + area.width, area.y, area.y + area.height, area.type));
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
            x: area.xStart,
            width: area.xEnd - area.xStart,
            y: area.yStart,
            height: area.yEnd - area.yStart,
            type: area.type
        };

        createArea(options);
    }
}

function JobHistoryViewModel(){
    var self = this;

    self.jobs = ko.observableArray([]);

    self.languages = ko.observableArray([]);
    self.jobTypes = ko.observableArray([]);

    loadData(self);

    self.showModal = function(job){
        preProcessing = new PreProcessing();

        console.log(job);
        setImageSource(job.image(), initModal, job);
        //$("#canvas").src= job.image;

        currentJob = job;

        $("#modal-sample-1").modal('show');
    };

    self.delete = function(job){
        console.log("delete: " + job);
        self.jobs.remove(job);

        var path = "/json/delete/" + job.id;
        $.get(path, function(result){
            console.log(result);
        });
    };

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