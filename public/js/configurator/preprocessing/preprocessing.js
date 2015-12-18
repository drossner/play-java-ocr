/**
 * Created by Benedikt Linke on 08.12.2015.
 * && awesome flo
 */
function PreProcessing() {
    var self = this;
    self.rotation = 0;

    self.caman;
    self.img;

    self.saveImage = function(){
        //alert("SaveStuff in preprocessing.js wurde aufgerufen");
        self.img = self.caman.toBase64();
        return self.img;
    };

    self.getCanvasHeight = function (){
        return $('#canvas').height();
    };

    self.getCanvasWidth = function(){
        return $('#canvas').width();
    };

    $(function () {
        $('.slider').each(function () {
            var op = $(this).attr('id');

            $('#' + op).slider({
                min: $(this).data('min'),
                max: $(this).data('max'),
                val: $(this).data('val'),
                change: function (e, ui) {
                    $('#v-' + op).html(ui.value);
                    $(this).data('val', ui.value);

                    if (e.originalEvent === undefined) {
                        return;
                    }

                    self.applyFilters();
                    self.caman.render();
                }
            });
        });

        $('#rotate-left').click(function () {
            self.rotateLeft();
        });

        $('#rotate-right').click(function () {
            self.rotateRight();
        });

        $('.preset').click(function () {
            self.resetFilters();
            var preset = $(this).data('preset');
            self.caman.revert(true);
            self.caman[preset]();
            self.caman.render();
        });

        $('#reset').click(function () {
            self.caman.reset();
            self.caman.render();
            self.resetFilters();
        });

        $('#save').click(function () {
            window.open(self.caman.toBase64());
        });

    });

    self.applyFilters = function() {
        self.caman.revert(false);

        $('.slider').each(function () {
            var op = $(this).attr('id');
            var value = $(this).data('val');

            if (value === 0) {
                return;
            }

            self.caman[op](value);
        });

        self.caman.render();
    };

     self.resetFilters = function() {
        $('.slider').each(function () {
            var op = $(this).attr('id');

            $('#' + op).slider('option', 'value', $(this).attr('data-val'));
        });

        self.caman.reset();
        self.caman.render();
    };

    self.rotateRight = function() {
        self.rotation += 90;
        self.caman.rotate(90);
        self.applyFilters();
        self.caman.render();
    };

    self.rotateLeft = function() {
        self.rotation -= 90;
        self.caman.rotate(-90);
        self.applyFilters();
        self.caman.render();
    };

    self.rotatePreProcess = function(rotationAncle) {
        while (rotationAncle != 0) {
            if (rotationAncle < 0) {
                self.rotateLeft();
                rotationAncle += 90;
            } else {
                self.rotateRight();
                rotationAncle -= 90;
            }
        }
    };

    self.setCaman = function(caman){
        self.caman = caman;
        self.img = self.caman.toBase64;
    };

    self.getRotation = function(){
        return self.rotation;
    };
}