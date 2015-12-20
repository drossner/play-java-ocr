/**
 * Created by FRudi on 12.12.2015.
 */
function PreProcessor(processorFunction, type, value){
    var self = this;

    self.type = type;
    self.function = processorFunction;
    self.processValue = value;

    self.process = function () {
        self.function(self.processValue);
    }
}