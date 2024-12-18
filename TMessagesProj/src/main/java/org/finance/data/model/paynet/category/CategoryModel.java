package org.finance.data.model.paynet.category;

import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class CategoryModel {
    @SerializedName("id")
    private int id;
    @SerializedName("title_ru")
    private String title_ru;
    @SerializedName("title_uz")
    private String title_uz;
    @SerializedName("is_subcategory")
    private boolean is_subcategory;
    @SerializedName("category_id")
    private int category_id;
    @SerializedName("is_active")
   private boolean is_active;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitleRu() { return title_ru; }
    public String getTitleUz() { return title_uz; }
    public int getCategoryId() { return category_id; }

    public boolean getIsActive(){return is_active;}

}
