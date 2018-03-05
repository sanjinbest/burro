package com.sanjinbest.burro.provider.db.model;

public class CfgValues {
    private Integer id;

    private Integer propertiesId;

    private String key;

    private String value;

    private Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPropertiesId() {
        return propertiesId;
    }

    public void setPropertiesId(Integer propertiesId) {
        this.propertiesId = propertiesId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}