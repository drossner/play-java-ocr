/**
 * Created by FRudi on 14.12.2015.
 */
var Singleton = (function () {
    var instance;

    function createInstance() {
        var object = new Object("");

        object.areas = undefined;

        return object;
    }

    return {
        getInstance: function () {
            if (!instance) {
                instance = createInstance();
            }
            return instance;
        },
        setAreas: function (areas) {
            instance.areas = areas;
        },
        getAreas: function () {
            return instance.areas;
        }
    };
})();