/**
 * Created by FRudi on 11.12.2015.
 */
function loadData(jobModel){
    $.getJSON("/json/jobType", function(result){
        jobModel.jobTypes = result;
    });

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            console.log(result[i].name);
            jobModel.languages.push(new Language(i + 1, result[i].name));
        }
    });

    $.getJSON("/json/jobHistory", function(result){
        for(var i = 0; i < result.length; i++){
            jobModel.jobs.push(new Job(i + 1, result[i]));
        }
    });
}
