/**
 * Created by Sebastian Lauterkorn on 15.12.2015.
 */
var areas;
var currArea;

function setImageSource(data, callback, job) {
    var image = $('#canvas');
    image.remove();

    //Disable all ui-elements except the cancel button
    $(':button').prop("disabled", true);



    $('#cancel').prop("disabled", false);
    $('.slider').each(function () {
        $( this ).slider( "option", "disabled", true );


    });



    $("#image-area").html('<img id="canvas" src="" style="width:100%;" />');

    $('#canvas').attr("src", data);


    //Detects when the img is loaded and enables all ui buttons except the previous Button
    $('#canvas').on('load', function(){
        $(':button').prop("disabled", false);
        $('#prev').prop("disabled", true);

        $('.slider').each(function () {
            $( this ).slider( "option", "disabled", false);
        });
    });

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


function getValuesOfSelectedArea(areas) {
    this.areas = areas;
}


function getValuesForInput(area) {


    if (area != null) {
        //set Area gloabal
        currArea = area;

        // $("#editMetaDataType").prop('disabled', true);

        var xHeight = parseInt(area.height);
        var yWidth = parseInt(area.width);
        var type = area.type;
        //   var areaName = area.name;

        $('#editHeigth').val(xHeight);
        $('#editWidth').val(yWidth);


        if(type == 'img')
        {
            $('#type').val("Bild");
        }
        else
        {
            $('#type').val("Textblock");
        }

        // console.log("AREA NAME: " + areaName);
        /* if((type == "meta"))
         {

         $("#editMetaDataType").prop('disabled', false);
         $("#editMetaDataType").val(areaName);
         }
         else
         {
         $("#editMetaDataType").val("");
         }*/
    }

}

function reset() {
    document.getElementById('templating').contentWindow.resetAreas();
}

function getAreas() {
    //return document.getElementById('templating').contentWindow.getValues();
    return this.areas;
}

function createArea(options) {
    document.getElementById('templating').contentWindow.createArea(options);
}

$('#deleteAreas').click(function () {
    reset();
    $('#type').val("");
    $('#editHeigth').val("");
    $('#editWidth').val("");
})


$('#img').click(function () {
    document.getElementById('templating').contentWindow.createNewArea('img');
});
$('#text').click(function () {
    document.getElementById('templating').contentWindow.createNewArea('text');
});

// Load preprocessed Image in second step
$('#next').click(function () {
    console.log("data-step: " + $(this).attr('data-step'));
    if ($(this).attr('data-step') != "complete") {
        console.log("next method");
        var image = preProcessing.saveImage();
        var height = preProcessing.getCanvasHeight();
        var width = preProcessing.getCanvasWidth();
        var iframe = document.getElementById('templating');
        iframe.height = height;
        document.getElementById('templating').contentWindow.loadImageForSecondStep(image, height, width);
    }
});


$(':button').click(function () {
    if ($(this).attr('data-step') == "complete") {

        var templateName = $('#templateName').val();

        console.log("Template Name: " + templateName);

        //todo save Name to Template

        saveData();
    }
});

// Callback Function for multistepmodal
$('#modal-sample-1').modalSteps({

    btnCancelHtml: 'Abbrechen',
    btnPreviousHtml: 'Zurück',
    btnNextHtml: 'Weiter',
    btnLastStepHtml: 'Fertig',

    callbacks: {
        '1': function () {
        },
        '2': function () {

            /* console.log("Vor der Abfrage im Callback");
             var temp = $('#templateName').val();
             if(!temp)
             {
             console.log("temp = null: " + temp);
             $('#next').attr("disabled", true);
             }
             else
             {
             console.log("temp != null: " + temp);
             $('#next').attr("disabled", false);
             }


             // If Input is empty. User can't complete the Modal
             $('#templateName').change(function(){

             var temp = $(this).val();
             if(!temp)
             {
             console.log("ONE Change Function == null: " + temp);
             //Setting the complete button not klickable
             $('#next').attr("disabled", true);

             }

             })
             ;*/
        }
    }
});


/* Does not work because customArea.js does not update it's selected areas
 //Get metaDataType for some stuff !--
 $('#editMetaDataType').change(function() {

 var metatype =  $( this ).val();
 document.getElementById('templating').contentWindow.setTypename(currArea, metatype);
 console.log("Dein Metatyp: " + metatype);


 });*/


/* Does not work because customArea.js does not update it's selected areas
 $('#metadata').click(function(){
 document.getElementById('templating').contentWindow.createNewArea('meta');
 });
 */


//If user types one char the complete button is clickable
/*$('#templateName').keyup(function(){
 var name =  $( this ).val();
 // console.log("String Length: " + name.length);

 if(name.length > 0)
 {
 $('#next').attr("disabled", false);
 }else{
 $('#next').attr("disabled", true);
 }


 });*/



// Unused but works
/*$('#editHeigth').keypress(validateNumber);
 //$('#editWidth').keypress(validateNumber);



 function validateNumber(event) {
 var $id = $(event.target).next("span");
 //if the letter is not digit then display error and don't type anything
 if (event.which != 8 && event.which != 0 && (event.which < 48 || event.which > 57)) {
 //display error message
 $id.html("Bitte nur Zahlen eingeben").show().fadeOut("slow");
 return false;
 }

 }*/

// Does not work yet maby later
/*$('#editHeigth').change(function() {

 var height =  $( this ).val();
 console.log("ich change");
 currArea =  document.getElementById('templating').contentWindow.setSizeHeight(currId, height);
 currId = 0;

 });*/


// With the element initially shown, we can hide it slowly:
/*
 $( "#metadata" ).click(function() {
 $( "#meta" ).hide( "slow", function() {
 alert( "Animation complete." );
 });
 });
 */

