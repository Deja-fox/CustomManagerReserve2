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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.AgreementListResponseBean;
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

/**
 * 期货商品列表适配器
 */
public class FuturesGoodsListAdapter extends BaseAdapter {

    private PromptDialog promptDialog;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData = new ArrayList<>();

    public FuturesGoodsListAdapter(Context context) {
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
        @Bind(R.id.pb_progressbar)
        ProgressBar pb_progressbar;
        @Bind(R.id.tvSurplus)
        TextView tvSurplus;
        ImageViewAware goodsImageAware;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            goodsImageAware = new ImageViewAware(goodsImage);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.futures_goods_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(convertView);
        final AgreementListResponseBean.AgreementBean.AgreementGoodsBean item = goodsListData.get(position);
        int max = new BigDecimal(item.getGoods_num()).intValue();
        int progress = new BigDecimal(item.getSurplus()).intValue();
        holder.pb_progressbar.setMax(max);
        holder.pb_progressbar.setProgress(progress);
        holder.tvSurplus.setText("" + (max - progress));//剩余数量
        holder.txtItemName.setText(item.getTitle());
        holder.txtPriceAndUnit.setText(item.getGoods_price() + "元/" + item.getGoods_unit());
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
        String imgUrl = item.getCover();//图片
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
        return convertView;
    }


    public void setGoodsListData(ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData) {
        this.goodsListData = goodsListData;
    }


}
