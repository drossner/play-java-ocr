/**
 * Created by FRudi on 16.12.2015.
 */
function User(){
    var self = this;

    self.language = ko.observable($('#language').text());
    console.log(self.language());

    self.cmsAccount = ko.observable($('#cmsAccount').text());

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
        $('#errormsg').text('');

        $.ajax("/json/saveUser", {
            data: ko.toJSON({ user: self.user }),
            type: "post", contentType: "application/json",
            success: function(result) { alert(result) },
            error: function(result) { $('#errormsg').text('Passwörter stimmen nicht überein!')}
        });
    }
}

ko.applyBindings(new UserViewModel());