package modules.export;

import java.io.File;

/**
 * Created by FRudi on 10.12.2015.
 */
public interface Export {

    void initialize(String path, String fileName);

    File export();

}
