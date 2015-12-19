/**
 * Created by Sebastian Lauterkorn on 19.12.2015.
 * && awesome flo
 */
function StoredJob(){

}

function StoredJobViewModel(){
    var self = this;

    self.storedJobs = ko.observableArray;

    self.showModal = function(job){
        $("#afterWork").modal('show');
    }
}

ko.applyBindings(new StoredJobViewModel());