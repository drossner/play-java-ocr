package modules.export;

import control.result.ResultFragment;

import java.io.File;

/**
 * Created by Benedikt Linke on 10.12.2015.
 */
public interface Export {

    void initialize(String path, String fileName, boolean landscape);

    void export(ResultFragment fragment);

    void newPage();

    File finish();
}
