package com.inbalance.notifications;

import com.inbalance.inbalance.R;

import java.io.Serializable;
import java.util.Date;

public class Notification  implements Serializable {
    private int id;
    private String name;
    private String category;
    private String message;
    private boolean active;
    private String nextRun;

    //Categories
    public static final String CATEGORY_MIND = "mind";
    public static final String CATEGORY_BODY = "body";
    public static final String CATEGORY_SPIRIT = "spirit";
    public static String[] categoryList = new String[] {
            "none",
            "mind",
            "body",
            "spirit"
    };

    public Notification(int id, String name, String category, String message, int active, String nextRun) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.message = message;
        if (active != 0) {
            this.active = true;
        } else this.active = false;
        this.nextRun = nextRun;
    }

    public int getID() {
        return id;
    }

    public int setID(int id) {
        this.id = id;
        return this.id;
    }

    public String getIDString() {
        return Integer.toString(this.id);
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        return this.name;
    }

    public String getCategory() {return this.category;}

    public int getCategoryImageID() {
        int imageResourceId;
        switch (category) {
            case CATEGORY_MIND:
                imageResourceId = R.drawable.mind_icon;
                break;
            case CATEGORY_BODY:
                imageResourceId = R.drawable.body_icon;
                break;
            case CATEGORY_SPIRIT:
                imageResourceId = R.drawable.spirit_icon;
                break;
            case "":
            default:
                imageResourceId = R.drawable.notification_icon;
        }
        return imageResourceId;
    }

    public String setCategory(String category) {
        this.category = category.toLowerCase();
        return this.category;
    }

    public String getMessage() {
        return message;
    }

    public String setMessage(String message) {
        this.message = message;
        return this.message;
    }

    public boolean getActive() {
        return active;
    }
    public int getActiveInt() {
        return active ? 1 : 0;
    }

    public boolean toggleActive() {
        return this.active = this.active ? false : true;
    }

    public String toString() {
        return  "ID: " + this.id + "\nName: " + this.name + "\nCategory: " + this.category +"\nMessage: " + this.message + "\nActive: " + this.active;
    }
}
