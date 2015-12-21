/**
 * Created by FRudi on 12.12.2015.
 */
function loadData(jobViewModel){
    $.getJSON("/json/jobType", function(result){
        console.log(result);
        jobViewModel.jobtypes.push("");
        for(var i = 0; i < result.length; i++){
            console.log("adding: " + result[i]);
            jobViewModel.jobtypes.push(result[i].name);
        }
    });

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            jobViewModel.languages.push(result[i].name);
        }
    });

    $.getJSON("/json/jobHistory", function(result){
        for(var i = 0; i < result.length; i++){
            console.log(result[i]);
            var job = new Job(i + 1, result[i], jobViewModel.languages()[0], jobViewModel.jobtypes()[0]);

            jobViewModel.jobs.push(job);
        }
    });
}