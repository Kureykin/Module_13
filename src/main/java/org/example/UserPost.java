package org.example;

public class UserPost {
    private int userId;
    private int id;
    private String title;
    private String body;

    public UserPost(int userID, int postID, String title, String body) {
        this.userId = userID;
        this.id = postID;
        this.title = title;
        this.body = body;
    }

    public int getId() {
        return id;
    }
}
