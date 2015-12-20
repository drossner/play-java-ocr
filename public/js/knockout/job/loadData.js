/**
 * Created by FRudi on 12.12.2015.
 */
function loadData(jobViewModel){
    $.getJSON("/json/jobType", function(result){
        console.log(result);
        for(var i = 0; i < result.length; i++){
            console.log("adding: " + result[i]);
            jobViewModel.jobtypes.push(result[i]);
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
            var job = new Job(i + 1, result[i]);

            job.language(jobViewModel.languages()[0]);
            console.log(job.language());

            job.jobType(jobViewModel.jobtypes()[0]);
            console.log(job.jobType());

            jobViewModel.jobs.push(job);
        }
    });
}