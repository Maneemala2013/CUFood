/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.cuhk.csci3310.cufood.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Model POJO for a rating.
 */
public class Menu {

    public static final String FIELD_MENU_NAME = "menu_name";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_PHOTO = "photo";
    public static final String FIELD_DESC = "description";
    public static final String FIELD_CATEGORY = "categoryId";
    public static final String FIELD_DETAILS = "details";

    private String menuName;
    private double price;
    private String photo;
    private String details;
    private boolean availableStatus;
    private String categoryId;

    public Menu() {}

    public Menu(String menuName, double price, String photo, String details, boolean availableStatus, String categoryId) {
        this.menuName = menuName;
        this.price = price;
        this.photo = photo;
        this.details = details;
        this.availableStatus = availableStatus;
        this.categoryId = categoryId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCategory() { return categoryId; }

    public void setCategory(String categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return details; }

    public void setDescription(String details) { this.details = details; }

    public boolean getAvailableStatus() { return availableStatus; }

    public void setAvailableStatus(boolean availableStatus) { this.availableStatus = availableStatus; }
}
