/**
 * Created by Benedikt Linke on 08.12.15.
 */
var areas;

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
    Singleton.getInstance().setAreas(areas);
};

// Display Valuse in parentView
function getValues () {
    return this.areas;
    //window.parent.getValuesOfSelectedArea(areas[id]);
};


function createNewArea(){
    var areaOptions = {
        x: Math.floor((Math.random() * 200)),
        y: Math.floor((Math.random() * 200)),
        width: Math.floor((Math.random() * 100)) + 50,
        height: Math.floor((Math.random() * 100)) + 20,
    };

    $('img#example').selectAreas('add', areaOptions);
};

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

