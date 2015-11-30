/**
 * Created by florian on 30.11.15.
 */

function getRequest(url, parameters, dataType, successfulCallback, failureCallback) {
    var self = this;
    self.url = url || "";
    self.parameters = parameters || {};
    self.dataType = dataType || "";

    self.successfulCallback = successfulCallback;
    self.failureCallback = failureCallback;

    $.ajax({
        url: self.url,
        type: "GET",
        dataType: self.dataType,
        success: function (data) {
            if (Object.prototype.toString.call(self.successfulCallback) == "[object Function]") {
                self.successfulCallback(data);
            }
        },
        error: function (data) {
            if (Object.prototype.toString.call(self.failureCallback) == "[object Function]") {
                self.failureCallback(data);
            }
        }
    })
}

function postRequest(url, parameters, dataType, successfulCallback, failureCallback) {
    var self = this;
    self.url = url || "";
    self.parameters = parameters || {};
    self.dataType = dataType || "";

    self.successfulCallback = successfulCallback;
    self.failureCallback = failureCallback;

    $.ajax({
        url: self.url,
        type: "POST",
        dataType: self.dataType,
        success: function (data) {
            if (Object.prototype.toString.call(self.successfulCallback) == "[object Function]") {
                self.successfulCallback(data);
            }
        },
        error: function (data) {
            if (Object.prototype.toString.call(self.failureCallback) == "[object Function]") {
                self.failureCallback(data);
            }
        }
    })
}