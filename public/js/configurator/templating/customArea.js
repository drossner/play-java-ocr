/**
 * Created by Benedikt Linke on 08.12.15.
 */

$(document).ready(function () {
    $('img#example').selectAreas({
        minSize: [10, 10],
        onChanged: debugQtyAreas,
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
    return $('img#example').selectAreas.areas;
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

function loadImageForSecondStep(imageBase64, canvasHeight, canvasWidth){
    var exampleImage = $('img#example');
    exampleImage.attr('heigth', canvasHeight);
    exampleImage.attr('width', canvasWidth);
    exampleImage.attr('src',  imageBase64);

    //Set width and height for the overlay
    $(".select-areas-overlay").css({"height": canvasHeight, "width": canvasWidth});
    $("div.image-decorator > div").css({"height": canvasHeight, "width": canvasWidth});
}