package com.jald.reserve.util;

import com.jald.reserve.extension.bean.response.CustomListResponseBean;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CustomListResponseBean.KCustomBean> {

    public int compare(CustomListResponseBean.KCustomBean o1, CustomListResponseBean.KCustomBean o2) {
        if (o1.getSort_letter().equals("@") || o2.getSort_letter().equals("#")) {
            return -1;
        } else if (o1.getSort_letter().equals("#") || o2.getSort_letter().equals("@")) {
            return 1;
        } else {
            return o1.getSort_letter().compareTo(o2.getSort_letter());
        }
    }

}
