package com.nelitaaas.mystockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nelitaaas.mystockhawk.R;
import com.nelitaaas.mystockhawk.data.Contract;
import com.nelitaaas.mystockhawk.data.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_STOCK = "extra_stock";
    private static final int STOCK_LOADER = 0;

    @BindView(R.id.recycler_stock_fluctuation)
    RecyclerView recyclerStockFluctuation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private StockDetailsAdapter adapter;

    private String mCurrentStockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);

        mCurrentStockSymbol = getIntent().getExtras().getString(EXTRA_STOCK);
        toolbar.setTitle(mCurrentStockSymbol);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    private void initRecyclerView() {
        adapter = new StockDetailsAdapter(this);
        recyclerStockFluctuation.setAdapter(adapter);
        recyclerStockFluctuation.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(mCurrentStockSymbol),
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
    }

    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
        } else {
            item.setIcon(R.drawable.ic_dollar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_change_units:
                PrefUtils.toggleDisplayMode(this);
                setDisplayModeMenuItemIcon(item);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
