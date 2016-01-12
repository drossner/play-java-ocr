/**
 *
 * Created by kurt on 16.12.2015.
 */
function User(){
    var self = this;

    self.language = ko.observable();

    self.cmsAccount = ko.observable($('#cmsAccount').val());

    self.password = ko.observable("");
    self.passwordConfirm = ko.observable("");
}

function UserViewModel(){
    var self = this;

    var initialLanguage = $('#language').text();

    self.user = ko.observable(new User());

    self.languages = ko.observableArray([]);

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            var temp =  result[i].name;
            self.languages.push(temp);

            if(initialLanguage == result[i].name){
                self.user().language(self.languages()[i]);
            }
        }
    });

    /**
     * sending the user to the server packing the data to json format
     */
    self.sendUser = function(){
        console.log(self.user());
        $('#cmsp').css("display", "none");

        $.ajax("/json/saveUser", {
            data: ko.toJSON({ user: self.user }),
            type: "post", contentType: "application/json",
            success: function(result) {
                console.log(result);
                var element = $('#errormsg');
                var cmsAccount = $('#cmsAccount');
                var pw1 = $('#pw1');
                var pw2 = $('#pw2');
                element.removeClass("warning-lachs");
                element.addClass("warning-lila");
                element.text(result.message);

                if(result.nuxeolink != null){
                    //populate link
                    $('#cmslink').prop("href", result.nuxeolink);
                    $('#cmsp').css("display", "block");
                }


                //fix values in UI
                cmsAccount.prop( "disabled", true );
                pw1.prop( "disabled", true );
                self.user().password("");
                self.user().password.valueHasMutated();
                pw2.prop( "disabled", true );
                //pw2.val(null);
                self.user().passwordConfirm("");
                self.user().passwordConfirm.valueHasMutated();
            },
            error: function(result) {
                var element = $('#errormsg');
                element.removeClass("warning-lila");
                element.addClass("warning-lachs");
                var data = JSON.parse(result.responseText);
                element.text(data.message);
            }
        });
    }
}

ko.applyBindings(new UserViewModel());