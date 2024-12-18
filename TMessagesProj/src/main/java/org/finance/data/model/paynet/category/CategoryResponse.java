package org.finance.data.model.paynet.category;

import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;

public class CategoryResponse {

    @SerializedName("categories")
    private ArrayList<CategoryModel> categories;

    public ArrayList<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryModel> categories) {
        this.categories = categories;
    }
}

