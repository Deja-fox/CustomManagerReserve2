package com.jald.reserve.extension.bean.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 超级无敌期货列表返回Bean
 */
public class FuturesResponseBean implements Serializable {
    private String ret_code;
    private String ret_msg;
    private ArrayList<FuturesBean> data;

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public ArrayList<FuturesBean> getData() {
        return data;
    }

    public void setData(ArrayList<FuturesBean> data) {
        this.data = data;
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public static class FuturesBean implements Serializable {
        private String id;//订单id
        private String feature_id;
        private String client_manager_tp_id;
        private String user_tp_id;
        private ArrayList<FuturesBean.CompleteGoods> feature_completed_goods;
        private String feature_price;//总额度
        private String feature_completed_price;//已完成
        private String type;//状态10未确认 20已确认
        private String status;
        private String add_time;//推送时间
        private String agree_time;//确认时间
        private String update_time;
        private AgreementBean market;

        public AgreementBean getMarket() {
            return market;
        }

        public void setMarket(AgreementBean market) {
            this.market = market;
        }

        public String getAgree_time() {
            return agree_time;
        }

        public void setAgree_time(String agree_time) {
            this.agree_time = agree_time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFeature_id() {
            return feature_id;
        }

        public void setFeature_id(String feature_id) {
            this.feature_id = feature_id;
        }

        public String getClient_manager_tp_id() {
            return client_manager_tp_id;
        }

        public void setClient_manager_tp_id(String client_manager_tp_id) {
            this.client_manager_tp_id = client_manager_tp_id;
        }

        public String getUser_tp_id() {
            return user_tp_id;
        }

        public void setUser_tp_id(String user_tp_id) {
            this.user_tp_id = user_tp_id;
        }

        public ArrayList<CompleteGoods> getFeature_completed_goods() {
            return feature_completed_goods;
        }

        public void setFeature_completed_goods(ArrayList<CompleteGoods> feature_completed_goods) {
            this.feature_completed_goods = feature_completed_goods;
        }

        public String getFeature_price() {
            return feature_price;
        }

        public void setFeature_price(String feature_price) {
            this.feature_price = feature_price;
        }

        public String getFeature_completed_price() {
            return feature_completed_price;
        }

        public void setFeature_completed_price(String feature_completed_price) {
            this.feature_completed_price = feature_completed_price;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }


        public static class CompleteGoods implements Serializable {
            private String id;
            private String market_id;
            private String goods_id;
            private String store_id;
            private String goods_num;
            private String max_num;
            private String gift_num;
            private String sell_num;
            private String goods_price;
            private FinGoods original;
            private FinGoods used;

            public FinGoods getUsed() {
                return used;
            }

            public void setUsed(FinGoods used) {
                this.used = used;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMarket_id() {
                return market_id;
            }

            public void setMarket_id(String market_id) {
                this.market_id = market_id;
            }

            public String getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(String goods_id) {
                this.goods_id = goods_id;
            }

            public String getStore_id() {
                return store_id;
            }

            public void setStore_id(String store_id) {
                this.store_id = store_id;
            }

            public String getGoods_num() {
                return goods_num;
            }

            public void setGoods_num(String goods_num) {
                this.goods_num = goods_num;
            }

            public String getMax_num() {
                return max_num;
            }

            public void setMax_num(String max_num) {
                this.max_num = max_num;
            }

            public String getGift_num() {
                return gift_num;
            }

            public void setGift_num(String gift_num) {
                this.gift_num = gift_num;
            }

            public String getSell_num() {
                return sell_num;
            }

            public void setSell_num(String sell_num) {
                this.sell_num = sell_num;
            }

            public String getGoods_price() {
                return goods_price;
            }

            public void setGoods_price(String goods_price) {
                this.goods_price = goods_price;
            }

            public FinGoods getOriginal() {
                return original;
            }

            public void setOriginal(FinGoods original) {
                this.original = original;
            }


            public static class FinGoods implements Serializable {
                private String id;
                private String market_id;
                private String goods_id;
                private String store_id;
                private String goods_num;
                private String max_num;
                private String gift_num;
                private String sell_num;
                private String goods_price;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getMarket_id() {
                    return market_id;
                }

                public void setMarket_id(String market_id) {
                    this.market_id = market_id;
                }

                public String getGoods_id() {
                    return goods_id;
                }

                public void setGoods_id(String goods_id) {
                    this.goods_id = goods_id;
                }

                public String getStore_id() {
                    return store_id;
                }

                public void setStore_id(String store_id) {
                    this.store_id = store_id;
                }

                public String getGoods_num() {
                    return goods_num;
                }

                public void setGoods_num(String goods_num) {
                    this.goods_num = goods_num;
                }

                public String getMax_num() {
                    return max_num;
                }

                public void setMax_num(String max_num) {
                    this.max_num = max_num;
                }

                public String getGift_num() {
                    return gift_num;
                }

                public void setGift_num(String gift_num) {
                    this.gift_num = gift_num;
                }

                public String getSell_num() {
                    return sell_num;
                }

                public void setSell_num(String sell_num) {
                    this.sell_num = sell_num;
                }

                public String getGoods_price() {
                    return goods_price;
                }

                public void setGoods_price(String goods_price) {
                    this.goods_price = goods_price;
                }
            }
        }

        public static class AgreementBean implements Serializable {
            private String id;//协议id
            private String store_id;
            private String title;//名称
            private String content;// 描述
            private String goods_content;
            private String start_time;
            private String end_time;
            private String payment;
            private String tactics_id;
            private String c_type;//期货标识
            private String min_price;
            private String max_price;//总额
            private String c_content;//月数
            private String zqfk_code;
            private String privacy;
            private String status;
            private String is_front_show;
            private String view_order;
            private String add_time;
            private String update_time;
            private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> feature_goods;

            public ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> getFeature_goods() {
                return feature_goods;
            }

            public void setFeature_goods(ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> feature_goods) {
                this.feature_goods = feature_goods;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getStore_id() {
                return store_id;
            }

            public void setStore_id(String store_id) {
                this.store_id = store_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getGoods_content() {
                return goods_content;
            }

            public void setGoods_content(String goods_content) {
                this.goods_content = goods_content;
            }

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public String getPayment() {
                return payment;
            }

            public void setPayment(String payment) {
                this.payment = payment;
            }

            public String getTactics_id() {
                return tactics_id;
            }

            public void setTactics_id(String tactics_id) {
                this.tactics_id = tactics_id;
            }

            public String getC_type() {
                return c_type;
            }

            public void setC_type(String c_type) {
                this.c_type = c_type;
            }

            public String getMin_price() {
                return min_price;
            }

            public void setMin_price(String min_price) {
                this.min_price = min_price;
            }

            public String getMax_price() {
                return max_price;
            }

            public void setMax_price(String max_price) {
                this.max_price = max_price;
            }

            public String getC_content() {
                return c_content;
            }

            public void setC_content(String c_content) {
                this.c_content = c_content;
            }

            public String getZqfk_code() {
                return zqfk_code;
            }

            public void setZqfk_code(String zqfk_code) {
                this.zqfk_code = zqfk_code;
            }

            public String getPrivacy() {
                return privacy;
            }

            public void setPrivacy(String privacy) {
                this.privacy = privacy;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getIs_front_show() {
                return is_front_show;
            }

            public void setIs_front_show(String is_front_show) {
                this.is_front_show = is_front_show;
            }

            public String getView_order() {
                return view_order;
            }

            public void setView_order(String view_order) {
                this.view_order = view_order;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }


        }

    }

}
