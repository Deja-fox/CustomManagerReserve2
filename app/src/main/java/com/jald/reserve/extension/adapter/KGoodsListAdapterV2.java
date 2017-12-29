package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.GoodsListResponseBeanV2;
import com.jald.reserve.extension.ui.fragment.KGoodsSelectFragmentV2;
import com.jald.reserve.util.KeyboardUtil;
import com.jald.reserve.widget.PromptDialog;
import com.open.imageloader.core.DisplayImageOptions;
import com.open.imageloader.core.imageaware.ImageViewAware;

import org.simple.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KGoodsListAdapterV2 extends BaseAdapter {

    private PromptDialog promptDialog;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsListData = new ArrayList<>();

    public KGoodsListAdapterV2(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    static class ViewHolder {
        @Bind(R.id.goodsImage)
        RoundedImageView goodsImage;
        @Bind(R.id.txtItemName)
        TextView txtItemName;
        @Bind(R.id.txtItemId)
        TextView txtItemId;
        @Bind(R.id.txtSpecification)
        TextView txtSpecification;
        @Bind(R.id.txtPriceAndUnit)
        TextView txtPriceAndUnit;
        @Bind(R.id.btnCountDecrease)
        ImageView btnCountDecrease;
        @Bind(R.id.btnCountIncrease)
        ImageView btnCountIncrease;
        @Bind(R.id.edtPurchaseCount)
        EditText edtPurchaseCount;
        @Bind(R.id.txtBaiTiaoDays)
        TextView txtBaiTiaoDays;
        @Bind(R.id.JiaoBiaoContainer)
        RelativeLayout JiaoBiaoContainer;
        @Bind(R.id.zhangqi_ln)
        LinearLayout zhangqiLinear;
        @Bind(R.id.zhangqi_tv)
        TextView zhangqiTv;
        ImageViewAware goodsImageAware;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            goodsImageAware = new ImageViewAware(goodsImage);
        }
    }

    private int touchedEditIndex = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.k_listview_goods_item, parent, false);
        final ViewHolder holder = new ViewHolder(convertView);
        final GoodsListResponseBeanV2.GoodsItem item = goodsListData.get(position);
        //判断账期及天数
        if (item.getDays() == null || new BigDecimal(item.getDays()).intValue() == 0) {
            //非账期商品
            holder.zhangqiLinear.setVisibility(View.GONE);
        } else {
            //账期商品
            holder.zhangqiLinear.setVisibility(View.VISIBLE);
            holder.zhangqiTv.setText("支持账期：" + item.getDays() + "天");
        }
        //白条标识暂时失效
        if (item.getDays() == null || new BigDecimal(item.getDays()).intValue() == 0) {
            holder.JiaoBiaoContainer.setVisibility(View.GONE);
        } else {
            holder.txtBaiTiaoDays.setText(item.getDays() + "天");
            holder.JiaoBiaoContainer.setVisibility(View.VISIBLE);
        }
        holder.txtItemName.setText(item.getItem_name());
        holder.txtPriceAndUnit.setText(item.getPrice() + "元/" + item.getUm_name());
        if (item.getSpecification() == null || item.getSpecification().trim().equals("")) {
            holder.txtSpecification.setText("暂无");
        } else {
            holder.txtSpecification.setText(item.getSpecification());
        }
        if (item.getItem_id() == null || item.getItem_id().trim().equals("")) {
            holder.txtItemId.setText("暂无");
        } else {
            holder.txtItemId.setText(item.getItem_id());
        }
        String imgUrl = "http://www.youmkt.com/static/upload/" + item.getImg_url() + "middle_" + item.getImage_name();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        KBaseApplication.getInstance().getImageLoader().displayImage(imgUrl, holder.goodsImageAware, options);

        holder.edtPurchaseCount.setText(item.getPurchaseCount() + "");
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String count = holder.edtPurchaseCount.getText().toString().trim();
                if (!count.equals("")) {
                    item.setPurchaseCount(Integer.parseInt(count));
                } else {
                    item.setPurchaseCount(0);
                }
                EventBus.getDefault().post(new KGoodsSelectFragmentV2.PurchaseGoodsChangeEvent());
            }
        };
        holder.edtPurchaseCount.addTextChangedListener(textWatcher);
        holder.edtPurchaseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promptDialog != null) {
                    promptDialog.dismiss();
                }
                promptDialog = new PromptDialog(context, "确定", new PromptDialog.OnOkClickListener() {
                    @Override
                    public void onOkClicked(String number) {
                        if (number.equals("")) {
                            holder.edtPurchaseCount.setText(0);
                        } else {
                            holder.edtPurchaseCount.setText(number);
                        }
                        KeyboardUtil.hide(context);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                KeyboardUtil.hide(context);
                            }
                        }, 50);
                        promptDialog.dismiss();
                    }
                }, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KeyboardUtil.hide(context);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                KeyboardUtil.hide(context);
                            }
                        }, 50);
                        promptDialog.dismiss();
                    }
                });
                promptDialog.show(holder.edtPurchaseCount.getText().toString());
            }
        });


/*        holder.edtPurchaseCount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchedEditIndex = position;
                }
                return false;
            }
        });*/
        holder.btnCountDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.edtPurchaseCount.removeTextChangedListener(textWatcher);
                int count = item.getPurchaseCount();
                item.setPurchaseCount(--count < 0 ? 0 : count);
                EventBus.getDefault().post(new KGoodsSelectFragmentV2.PurchaseGoodsChangeEvent());
                holder.edtPurchaseCount.setText(item.getPurchaseCount() + "");
                holder.edtPurchaseCount.addTextChangedListener(textWatcher);
            }
        });
        holder.btnCountIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.edtPurchaseCount.removeTextChangedListener(textWatcher);
                item.setPurchaseCount(item.getPurchaseCount() + 1);
                EventBus.getDefault().post(new KGoodsSelectFragmentV2.PurchaseGoodsChangeEvent());
                holder.edtPurchaseCount.setText(item.getPurchaseCount() + "");
                holder.edtPurchaseCount.addTextChangedListener(textWatcher);
            }
        });
        if (touchedEditIndex != -1 && touchedEditIndex == position) {
            // holder.edtPurchaseCount.requestFocus();
        }
        return convertView;
    }


    public void setGoodsListData(ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsListData) {
        this.goodsListData = goodsListData;
    }


}
