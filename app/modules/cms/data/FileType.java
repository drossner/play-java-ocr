package modules.cms.data;

/**
 * Created by florian on 15.12.15.
 */
public enum FileType {

    FILE("File"), IMG_PNG("image/png");

    private final String type;

    FileType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
