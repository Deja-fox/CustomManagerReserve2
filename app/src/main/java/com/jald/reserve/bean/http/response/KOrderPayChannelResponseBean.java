package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.List;

public class KOrderPayChannelResponseBean implements Serializable {

    private static final long serialVersionUID = -718511168849961080L;

    private int total;
    private List<OrderPayChannel> list;

    public static class OrderPayChannel implements Serializable {
        private static final long serialVersionUID = 1L;

        private String code;
        private String name;

        public OrderPayChannel() {
        }

        public OrderPayChannel(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<OrderPayChannel> getList() {
        return list;
    }

    public void setList(List<OrderPayChannel> list) {
        this.list = list;
    }

}
