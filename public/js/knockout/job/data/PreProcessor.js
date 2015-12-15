/**
 * Created by FRudi on 12.12.2015.
 */
function PreProcessor(processorFunction, value){
    var self = this;

    self.function = processorFunction;
    self.processValue = value;

    self.process = function () {
        self.function(self.processValue);
    }
}