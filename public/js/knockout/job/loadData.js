/**
 * Created by FRudi on 12.12.2015.
 */
function loadData(jobViewModel){
    $.getJSON("/json/jobType", function(result){
        jobViewModel.jobTypes = result;
    });

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            jobViewModel.languages.push(new Language(i + 1, result[i].name));
        }
    });

    $.getJSON("/json/jobHistory", function(result){
        for(var i = 0; i < result.length; i++){
            jobViewModel.jobs.push(new Job(i + 1, result[i]));
        }
    });
}