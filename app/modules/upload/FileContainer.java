package modules.upload;

import java.io.File;

/**
 * Created by daniel on 30.11.15.
 * Encapsulation of a file and some meta data.
 */
public class FileContainer {

    private String contentType;
    private String fileName;
    private File file;

    private FileContainer(){}

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Builder class for easy initialization of a FileContainer
     * @author daniel
     */
    public static class Builder{
        private FileContainer fc;

        public Builder(){
            fc = new FileContainer();
        }

        public FileContainer build(){
            return fc;
        }

        public Builder setContentType(String contentType){
            fc.setContentType(contentType);
            return this;
        }

        public Builder setFile(File f){
            fc.setFile(f);
            return this;
        }

        public Builder setFileName(String fileName){
            fc.setFileName(fileName);
            return this;
        }
    }

}
