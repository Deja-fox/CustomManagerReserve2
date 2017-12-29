package com.jald.reserve.extension.bean;

import java.io.Serializable;

//商品类型Bean
public class GoodsTypeBean implements Serializable {

    private String type_id;
    private String type_name;
    // 额外
    private transient boolean isSelected;

    public GoodsTypeBean(String type_id, String type_name) {
        this.type_id = type_id;
        this.type_name = type_name;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof GoodsTypeBean) {
            GoodsTypeBean input = (GoodsTypeBean) o;
            if (this.getType_id().equals(input.getType_id()) && this.getType_name().equals(input.getType_name())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return type_id.hashCode();
    }
}
