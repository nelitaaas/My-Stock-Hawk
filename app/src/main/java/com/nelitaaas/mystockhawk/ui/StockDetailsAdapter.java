package com.nelitaaas.mystockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nelitaaas.mystockhawk.R;
import com.nelitaaas.mystockhawk.data.Contract;
import com.nelitaaas.mystockhawk.data.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockDetailsAdapter extends RecyclerView.Adapter<StockDetailsAdapter.StockViewHolder> {

    private final Context context;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    private Cursor cursor;
    private String mDate;
    private Double mPrice;
    private Double mChange;
    private Double mPercentChange;
    private int mDateCount;
    private JSONArray dataJsonArray;

    StockDetailsAdapter(Context context) {
        this.context = context;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        if (cursor != null) {
            cursor.moveToPosition(0);
            try {
                dataJsonArray = new JSONArray(cursor.getString(Contract.Quote.POSITION_HISTORY));
                Log.d("blabla", dataJsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mDateCount = dataJsonArray.length();
            notifyDataSetChanged();
        }
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        try {
            processStock(dataJsonArray, position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.symbol.setText(mDate);
        holder.price.setText(dollarFormat.format(mPrice));

        float rawAbsoluteChange = mChange.floatValue();
        float percentageChange = mPercentChange.floatValue();
        if (rawAbsoluteChange > 0) {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(context)
                .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            holder.change.setText(change);
        } else {
            holder.change.setText(percentage);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = mDateCount - 1;
        }
        return count;
    }

    private void processStock(JSONArray historicData, int position) throws JSONException {
        mDate = (String) historicData.getJSONArray(position).get(0);
        mPrice = historicData.getJSONArray(position).getDouble(1);
        mChange = mPrice - historicData.getJSONArray(position + 1).getDouble(1);
        mPercentChange = 100 * (( mPrice - historicData.getJSONArray(position + 1).getDouble(1) ) / historicData.getJSONArray(position + 1).getDouble(1));
    }

    class StockViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.change)
        TextView change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
