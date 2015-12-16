/**
 * Created by Sebastian Lauterkorn on 15.12.2015.
 */
var areas;

function setImageSource(data, callback, job){
    var image = $('#canvas');
    image.remove();

    $("#image-area").html('<img id="canvas" src="" style="width:100%;" />');

    $('#canvas').attr("src", data);

    preProcessing.setCaman(Caman('#canvas', function () {
        this.render();

        callback(job);
    }));

    /*
     $('#canvas').attr("src", data);

     preProcessing.caman.reset();
     */

    /*
     var ctx = $('#canvas')[0].getContext("2d");
     var img = new Image();
     img.src = data;
     img.onload = function() {
     ctx.drawImage(img, 0, 0);
     };*/
    /*
     $.get(data,
     function(rs){
     var ctx = $('#canvas').getContext("2d");
     ctx.drawImage(rs, 0, 0);
     });*/
}

function getValuesOfSelectedArea(areas){
    this.areas = areas;
}

function reset(){
    document.getElementById('templating').contentWindow.resetAreas();
}

function getAreas(){
    return document.getElementById('templating').contentWindow.getValues();
    //return this.areas;
}

function createArea(options){
    document.getElementById('templating').contentWindow.createArea(options);
}

$('#deleteAreas').click(function(){
    reset();
})

$('#metadata').click(function(){
    document.getElementById('templating').contentWindow.createNewArea('meta');
});

// Load preprocessed Image in second step
$('#next').click(function(){
    console.log("next method");
    var image = preProcessing.saveImage();
    var height = preProcessing.getCanvasHeight();
    var width = preProcessing.getCanvasWidth();
    var iframe = document.getElementById('templating');
    console.log(iframe);
    iframe.height = height;
    console.log(iframe);
    document.getElementById('templating').contentWindow.loadImageForSecondStep(image, height, width);
});



$(':button').click(function (){
    if($(this).attr('data-step') == "complete"){
        saveData();
    }
});

// Callback Function for multistepmodal
$('#modal-sample-1').modalSteps({
    callbacks: {
        '1': function(){
        },
        '2': function(){
        }
    }
});
