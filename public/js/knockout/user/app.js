/**
 * Created by FRudi on 16.12.2015.
 */
function User(){
    var self = this;

    self.language = ko.observable($('#language').text());
    console.log(self.language());

    self.cmsAccount = ko.observable($('#cmsAccount').val());
    console.log(self.cmsAccount());

    self.password = ko.observable("");
    self.passwordConfirm = ko.observable("");
}


function Language(id, initialLanguage){
    var self = this;
    self.id = id;
    self.language = ko.observable(initialLanguage);
}

function UserViewModel(){
    var self = this;

    self.user = ko.observable(new User());

    self.languages = ko.observableArray([]);

    $.getJSON("/json/jobLanguage", function(result){
        for(var i = 0; i < result.length; i++){
            self.languages.push(new Language(i + 1, result[i].name));
        }
    });

    self.sendUser = function(){
        console.log(self.user());
        //$('#errormsg').text('');

        $.ajax("/json/saveUser", {
            data: ko.toJSON({ user: self.user }),
            type: "post", contentType: "application/json",
            success: function(result) {
                var element = $('#errormsg');
                element.removeClass("warning-lachs");
                element.addClass("warning-lila");
                element.text(result.message);
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