package Controller;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import controllers.UploadController;
import controllers.routes;
import modules.upload.UploadHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by daniel on 19.01.16.
 */
public class UploadControllerTest extends WithApplication{

    @Inject
    Application application;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
                bind(UploadHandler.class).to(UploadHandlerTestClass.class);
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);
    }

    @Test
    public void testUpload(){
        Result result = Helpers.route(routes.UploadController.upload("asd"));
        assertEquals(303, result.status());
        //TODO: in den test bindings müsste das Auth-Modul getauscht werden, damit nicht mehr auf login geleitet wird
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

}
