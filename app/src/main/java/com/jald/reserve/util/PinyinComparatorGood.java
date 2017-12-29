package com.jald.reserve.util;

import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.GoodsListResponseBeanV2;

import java.util.Comparator;

public class PinyinComparatorGood implements Comparator<GoodsListResponseBeanV2.GoodsItem> {

    public int compare(GoodsListResponseBeanV2.GoodsItem o1, GoodsListResponseBeanV2.GoodsItem o2) {
        if (o1.getSort_letter().equals("@") || o2.getSort_letter().equals("#")) {
            return -1;
        } else if (o1.getSort_letter().equals("#") || o2.getSort_letter().equals("@")) {
            return 1;
        } else {
            return o1.getSort_letter().compareTo(o2.getSort_letter());
        }
    }

}
