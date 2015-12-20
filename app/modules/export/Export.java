package modules.export;

import java.io.File;

/**
 * Created by Benedikt Linke on 10.12.2015.
 */
public interface Export {

    void initialize(String path, String fileName, boolean landscape);

    void export(Fragment fragment);

    void newPage();

    File finish();
}
