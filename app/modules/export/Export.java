package modules.export;

import control.result.ResultFragment;

import java.io.File;

/**
 * Created by Benedikt Linke on 10.12.2015.
 */
public interface Export {

    /**
     * Initialisiert ein Textdokument
     * @param path Speicherort
     * @param fileName Names des Dokumentes
     * @param landscape Orientation des Dokuments
     */
    void initialize(String path, String fileName, boolean landscape);

    /**
     * Setzt den Content in dem Textdokument
     * @param fragment enthält ein Bild oder Text sowie Positionierungsangaben
     */
    void export(ResultFragment fragment);

    /**
     * Seitenumbruch in einen Dokument erzeugen
     */
    void newPage();

    /**
     * Speichert das Dokument in einer Datei ab
     * @return das gespeicherte Dokument
     */
    File finish();
}
