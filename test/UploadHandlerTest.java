import modules.upload.UploadHandler;
import org.junit.Test;
import play.test.WithApplication;

import javax.inject.Inject;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by daniel on 17.01.16.
 */
public class UploadHandlerTest extends WithApplication{

    @Inject
    private UploadHandler uploadHandler;

    @Test
    public void test(){
        assertTrue(true);
    }

}
