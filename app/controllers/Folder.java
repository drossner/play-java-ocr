package controllers;

public class Folder{

    private String id;
    private String title;
    private String parentId;


    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }
}
