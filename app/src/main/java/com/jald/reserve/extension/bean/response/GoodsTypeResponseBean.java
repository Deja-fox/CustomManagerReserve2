package com.jald.reserve.extension.bean.response;

import java.io.Serializable;
import java.util.ArrayList;

public class GoodsTypeResponseBean implements Serializable {
    private int total;
    private ArrayList<GoodsTypeBean> list = new ArrayList<>();

    public static class GoodsTypeBean implements Serializable {
        private String id;
        private String name;

        // 额外
        private transient boolean isSelected;

        public GoodsTypeBean() {

        }

        public GoodsTypeBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<GoodsTypeBean> getList() {
        return list;
    }

    public void setList(ArrayList<GoodsTypeBean> list) {
        this.list = list;
    }
}
