package org.finance.data.model.paynet.service;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/** @noinspection ALL*/
public class Fields {
    @SerializedName("name")
    private String name;
    @SerializedName("title_uz")
    private String titleUz;
    @SerializedName("title_ru")
    private String titleRu;
    @SerializedName("required")
    private Boolean required;
    @SerializedName("read_only")
    private Boolean readOnly;
    @SerializedName("field_type")
    private String fieldType;
    @SerializedName("field_control")
    private String fieldControl;
    @SerializedName("field_values")
    private List<FieldValues> fieldValues;
    @SerializedName("id")
    private Integer id;

    public Fields() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleUz() {
        return titleUz;
    }

    public void setTitleUz(String titleUz) {
        this.titleUz = titleUz;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldControl() {
        return fieldControl;
    }

    public void setFieldControl(String fieldControl) {
        this.fieldControl = fieldControl;
    }

    public List<FieldValues> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<FieldValues> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
