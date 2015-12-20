package modules.export.impl;

import control.result.ResultFragment;
import modules.export.Export;
import modules.export.Fragment;

import java.io.File;

/**
 * Created by FRudi on 10.12.2015.
 */
public class OdtExport implements Export {
    @Override
    public void initialize(String path, String fileName, boolean landscape) {

    }

    @Override
    public void export(ResultFragment fragment) {

    }

    @Override
    public void newPage() {

    }

    @Override
    public File finish() {
        return null;
    }
}
