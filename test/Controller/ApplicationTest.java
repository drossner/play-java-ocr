package Controller;


import org.junit.Test;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;

/**
 * Created by daniel on 19.01.16.
 */
public class ApplicationTest extends WithApplication {

    /**
     * Fake Request to home with no sessions. Should render login page.
     */
    @Test
    public void testIndex() {
        Result result = Helpers.route(Helpers.fakeRequest().path("/"));
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
        assertEquals("utf-8", result.charset());
        assertTrue(Helpers.contentAsString(result).contains("login"));
    }

    /**
     * Fake Request to home with fakeSession. Should redirect.
     */
    @Test
    public void testIndex2() {
        Result result = Helpers.route(Helpers.fakeRequest().path("/").session("session", "asession"));
        assertEquals(303, result.status());
    }

    /**
     * Fake Request to verwalten with no sessions. Should redirect.
     */
    @Test
    public void testVerwalten() {
        Result result = Helpers.route(controllers.routes.Application.verwalten(false));
        assertEquals(303, result.status());
    }

    /**
     * Fake Request to ablage with no sessions. Should redirect.
     */
    @Test
    public void testAblage() {
        Result result = Helpers.route(controllers.routes.Application.ablage());
        assertEquals(303, result.status());
    }

    /**
     * Fake Request to hochladen with no sessions. Should redirect.
     */
    @Test
    public void testHochladen() {
        Result result = Helpers.route(controllers.routes.Application.hochladen(0, null));
        assertEquals(303, result.status());
    }

    /**
     * Fake Request to hilfe with no sessions. Should redirect.
     */
    @Test
    public void testHilfe() {
        Result result = Helpers.route(controllers.routes.Application.hilfe());
        assertEquals(303, result.status());

    }

}
