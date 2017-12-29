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
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
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

// 商品搜索列表适配器
public class KGoodsSearchListAdapterV2 extends BaseAdapter implements SectionIndexer {

    private PromptDialog promptDialog;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsListData = new ArrayList<>();

    public KGoodsSearchListAdapterV2(Context context) {
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

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = goodsListData.get(i).getSort_letter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        GoodsListResponseBeanV2.GoodsItem item = goodsListData.get(position);
        return item.getSort_letter().charAt(0);
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
        @Bind(R.id.txtLetter)
        TextView txtLetter;
        @Bind(R.id.Viewline)
        View Viewline;

        ImageViewAware goodsImageAware;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            goodsImageAware = new ImageViewAware(goodsImage);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.k_listview_goods_search_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(convertView);
        final GoodsListResponseBeanV2.GoodsItem item = goodsListData.get(position);
        if (item.getDays() == null || new BigDecimal(item.getDays()).intValue() == 0) {
            holder.JiaoBiaoContainer.setVisibility(View.GONE);
        } else {
            holder.txtBaiTiaoDays.setText(item.getDays() + "天");
            holder.JiaoBiaoContainer.setVisibility(View.VISIBLE);
        }

        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            holder.txtLetter.setVisibility(View.VISIBLE);
            holder.txtLetter.setText(item.getSort_letter());
            holder.Viewline.setVisibility(View.GONE);
        } else {
            holder.txtLetter.setVisibility(View.GONE);
            holder.Viewline.setVisibility(View.VISIBLE);
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


    public void setGoodsListData(ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsListData) {
        this.goodsListData = goodsListData;
    }


}
