/**
 * Created by florian on 30.11.15.
 */

function getJsonRequest(url, parameters, successfulCallback, failureCallback) {
    url = url || "";
    parameters = parameters || { };

    $.getJSON(url, parameters, function (result) {
        if (Object.prototype.toString.call(parameters) == "[object Function]") {
            successfulCallback(result);
        }
    }, function (result) {
        if (Object.prototype.toString.call(parameters) == "[object Function]") {
            failureCallback(result);
        }
    });
}

function postJsonRequest(url, parameters, successfulCallback, failureCallback) {
    url = url || "";
    parameters = parameters || { };

    $.postJson(url, parameters, function (result) {
        if (Object.prototype.toString.call(parameters) == "[object Function]") {
            successfulCallback(result);
        }
    }, function (result) {
        if (Object.prototype.toString.call(parameters) == "[object Function]") {
            failureCallback(result);
        }
    });
}