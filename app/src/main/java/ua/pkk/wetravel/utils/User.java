package ua.pkk.wetravel.utils;

public class User {
    private static User user;
    private String id;
    private String name;
    private String imgUri;

    private String info;
    private String status;

    private User() {
        name = "User";
        info = "Empty";
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static synchronized User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void cleanData() {
        this.name = "User";
        this.info = "Empty";
        this.id = null;
    }

}