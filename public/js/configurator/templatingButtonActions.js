/**
 * Created by Sebastian Lauterkorn on 15.12.2015.
 */


var areas;

function getValuesOfSelectedArea(areas){
    this.areas = areas;
}

function reset(){
    document.getElementById('templating').contentWindow.resetAreas();
}

function getAreas(){
    return this.areas;
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
    var stuff = saveStuff();
    var heigth = getCanvasHeigth();
    var width = getCanvasWidth();
    var iframe = document.getElementById('templating');
    console.log(iframe);
    iframe.height = heigth;
    console.log(iframe);
    document.getElementById('templating').contentWindow.loadImageForSecondStep(stuff, heigth, width);
});



// Callback Function for multistepmodal
$(':button').click(function (){
    if($(this).attr('data-step') == "complete"){
        saveData();
    }
});
$('#modal-sample-1').modalSteps({
    callbacks: {
        '1': function(){
            console.log("1");
        },
        '2': function(){

        }
    }
});
