package edu.cuhk.csci3310.cufood.model;

public class MenuCategory {
    public static final String FIELD_MENU_NAME = "category_name";
//    public static final String FIELD_CATEGORY = "categoryId";
    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_AVAILABLE = "available_status";

    private String categoryName;
//    private String categoryId;
    private String photo;
    private boolean availableStatus;

    public MenuCategory() {}

    public MenuCategory(String categoryName, String photo, boolean availableStatus) {
        this.categoryName = categoryName;
//        this.categoryId = categoryId;
        this.photo = photo;
        this.availableStatus = availableStatus;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

//    public String getCategoryId() { return categoryId; }
//
//    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean getAvailableStatus() {
        return availableStatus;
    }

    public void setAvailableStatus(boolean availableStatus) {
        this.availableStatus = availableStatus;
    }
}
