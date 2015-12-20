/**
 * Created by florian on 20.12.15.
 */
function loadData(model){
    $.getJSON("/json/getProcessedJobs", function(result){
        console.log(result);
    });
}
