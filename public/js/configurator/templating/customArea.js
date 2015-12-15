/**
 * Created by Benedikt Linke on 08.12.15.
 */
var areas;
var IMG;

$(document).ready(function () {
    $('img#example').selectAreas({
        minSize: [10, 10],
        onChanged: debugQtyAreas,
        width: 500,
        allowSelect: false
    });
});


// Log the quantity of selections
function debugQtyAreas (event, id, areas) {
    //window.parent.myfunction(areas[id]);
    console.log(areas.length + " areas", arguments);

    window.parent.getValuesOfSelectedArea(areas);
}

// Display Valuse in parentView
function getValues () {
    return this.areas;
    //window.parent.getValuesOfSelectedArea(areas[id]);
}


function createNewArea(type){
    var areaOptions = {
        x: Math.floor((Math.random() * 200)),
        y: Math.floor((Math.random() * 200)),
        width: Math.floor((Math.random() * 100)) + 50,
        height: Math.floor((Math.random() * 100)) + 20,
        type: type
    };

    $('img#example').selectAreas('add', areaOptions);
}

function createArea(options){
    $('img#example').selectAreas('add', options);
}

function deleteSelectedArea(){
    $('img#example').selectAreas('destroy');
}

function resetAreas(){
    $('img#example').selectAreas('reset');
}

function displayAreas(areas) {
    var text = "";
    $.each(areas, function (id, area) {
        text += areaToString(area);
    });
    output(text);
}

function areaToString(area) {
    return (typeof area.id === "undefined" ? "" : (area.id + ": ")) + area.x + ':' + area.y + ' ' + area.width + 'x' + area.height + '<br />'
}

function output(text) {
    window.parent.displayAreas(text);

}

function loadImageForSecondStep(imageBase64, canvasHeigth, canvasWidth){

    console.log("load second step");
    $('#example').attr('heigth', canvasHeigth);
    $('#example').attr('width', canvasWidth);
    $('#example').attr('src',  imageBase64);


    //Get width and height from base64
    $("body").append("<img id='hiddenImage' src='"+ imageBase64 +"' />");
    var width = $('#hiddenImage').width();
    var height = $('#hiddenImage').height();

    $('#hiddenImage').remove();


    //Set width and height for the overlay
    $(".select-areas-overlay").css({"height": canvasHeigth, "width": canvasWidth});
    $( "div.image-decorator > div").css({"height": canvasHeigth, "width": canvasWidth});
}