package modules.export.impl;

import modules.export.Export;
import modules.export.Fragment;

import java.io.File;

/**
 * Created by FRudi on 10.12.2015.
 */
public class PdfExport implements Export {
    @Override
    public void initialize(String path, String fileName) {

    }

    @Override
    public void export(Fragment fragment) {

    }

    @Override
    public void newPage() {

    }

    @Override
    public File finish() {
        return null;
    }
}
