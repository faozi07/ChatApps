package com.htp.chat.modal;

import java.util.Date;

public class modChat {

    private String userId = "";
    private Date postDate;
    private String name = "";
    private String phone = "";
    private String image = "";
    private String descriptionChat = "";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescriptionChat() {
        return descriptionChat;
    }

    public void setDescriptionChat(String descriptionChat) {
        this.descriptionChat = descriptionChat;
    }
}
