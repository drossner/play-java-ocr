/**
 * loading initial data
 * Created by FRudi on 12.12.2015.
 */
function loadData(jobViewModel){
    $.getJSON("/json/jobType", function(result){

        //pushing first entry
        jobViewModel.jobtypes.push("");
        jobViewModel.jobTypeAreas.push(new TypeArea());

        for(var i = 0; i < result.length; i++){
            jobViewModel.jobtypes.push(result[i].config.name);

            var fragments = ko.observableArray([]);
            for(var k = 0; result[i].fragments != null && k < result[i].fragments.length; k++){
                var fragment = result[i].fragments[k];
                var type;
                if(fragment.type == "IMAGE"){
                    type = "img";
                }else{
                    type = "text";
                }

                fragments.push(new SelectArea(fragment.xStart, fragment.xEnd, fragment.yStart, fragment.yEnd, type));
            }

            jobViewModel.jobTypeAreas.push(new TypeArea(result[i].config.name, fragments));
        }
        console.log(jobViewModel.jobTypeAreas());
    });

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            jobViewModel.languages.push(result[i].name);
        }
    });

    console.log("uploadid: " + uploadID);
    $.getJSON("/json/jobHistory?id=" + uploadID, function(result){
        for(var i = 0; i < result.length; i++){
            console.log(result[i]);
            var job = new Job(i + 1, result[i], jobViewModel.languages()[0], jobViewModel.jobtypes()[0]);

            jobViewModel.jobs.push(job);
        }
    });
}