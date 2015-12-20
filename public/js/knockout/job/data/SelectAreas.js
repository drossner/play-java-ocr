/**
 * Created by FRudi on 12.12.2015.
 */
function SelectArea (xStart, xEnd, yStart, yEnd, type, canvasHeight, canvasWidth){
    var self = this;

    self.xStart = xStart;
    self.yStart = yStart;

    self.xEnd = xEnd;
    self.yEnd = yEnd;

    self.canvasHeight = canvasHeight;
    self.canvasWidth = canvasWidth

    self.type = type;
}