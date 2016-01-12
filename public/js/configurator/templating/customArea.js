/**
 * Created by Benedikt Linke on 08.12.15.
 */
/*
$(document).ready(function () {
    console.log("document ready");
    $('img#example').selectAreas({
        minSize: [10, 10],
        onChanged: debugQtyAreas,
        allowSelect: false
    });

});*/

// Log the quantity of selections
function debugQtyAreas (event, id, areas) {
    console.log(areas.length + " areas", arguments);

    // Übergibt die areas den ParentFrame (aus dem Iframe an das Modal)
    window.parent.getValuesForInput(areas[id]);
    window.parent.getValuesOfSelectedArea(areas);
}

// Change the Size of the Selected Area

//Does not work maby latter
/*function setSizeHeight (id, height) {

    var area = $('img#example').selectAreas('areas')[id];

    var areaOptions = {
        x:10,
        y: area.y,
        width: area.width,
        height: 29,
        type: area.type
    }

    var area = $('img#example').selectAreas('remove')[id];
    createArea(areaOptions);

    return $('img#example').selectAreas('areas')[0]



};*/

/**
 * Erstellt ein Auswahlfeld, wobei die Größe zufällig ist. Der Übergabeparameter definiert die Art eines Feldes (Meta, Bild oder Text)
 * @param type Art des selektieren Feldes
 */
function createNewArea(type){
    var areaOptions = {
        x: Math.floor((Math.random() * 200)),
        y: Math.floor((Math.random() * 200)),
        width: Math.floor((Math.random() * 100)) + 50,
        height: Math.floor((Math.random() * 100)) + 20,
        type: type
       // name: "" Does not work because customArea.js does not update it's selected areas
    };

    createArea(areaOptions);
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

/**
 * Errechnet die Größe des Canvas im Step 2
 * @param imageBase64 Bild aus Step 1
 * @param canvasHeight Höhe des Bildes aus Step 1
 * @param canvasWidth Höhe des Bildes aus Step 1
 */
function loadImageForSecondStep(imageBase64, canvasHeight, canvasWidth){
    this.canvasHeight = canvasHeight;
    this.canvasWidth = canvasWidth;

    var exampleImage = $('img#example');
    exampleImage.attr('heigth', canvasHeight);
    exampleImage.attr('width', canvasWidth);
    exampleImage.attr('src',  imageBase64);

    //Set width and height for the overlay
    $(".select-areas-overlay").css({"height": canvasHeight, "width": canvasWidth});
    $("div.image-decorator > div").css({"height": canvasHeight, "width": canvasWidth});

    var areas = exampleImage.selectAreas("areas");

    exampleImage.selectAreas("destroy");
    exampleImage.selectAreas({
        minSize: [10, 10],
        onChanged: debugQtyAreas,
        allowSelect: false
    });

    $.each(areas, function(index, value) {
        if (value.x < 1 && value.y < 1 && value.width < 1 && value.height < 1) {
            console.log("erstelle area mit canvas dimensionen");
            console.log(value);
            var options = {
                x: value.x * canvasWidth,
                width: value.width * canvasWidth,
                y: value.y * canvasHeight,
                height: value.height * canvasHeight,
                type: value.type
            };
            createArea(options);
        }else {
            exampleImage.selectAreas("add", value);
        }
    });

}
/*Does not work because customArea.js does not update it's selected areas
function setTypename(area, name)
{
    console.log(name);
    var area = $('img#example').selectAreas('areas')[area.id];

    area.name =  name;
    area.height = 12;
   // $('img#example').selectAreas('refresh');


    //var areaOptions = {
    //    x:area.x,
 //    y: area.y,
 //      width: area.width,
 //     height: area.height,
 //     type: area.type,
 //     name: name
 // }

    $('img#example').selectAreas('remove',[area.id]);
    createArea(areaOptions);


}*/