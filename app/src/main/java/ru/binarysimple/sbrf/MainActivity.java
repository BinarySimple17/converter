package ru.binarysimple.sbrf;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {

    private Spinner sourceCurrencySpinner;
    private Spinner resultCurrencySpinner;
    private EditText sourceQuantityEt;
    private TextView resultQuantityTv;
    private Button convertButton;
    private TextInputLayout sourceQuantityLayout;
    private int sourceSpinnerItemId = -1;
    private int resultSpinnerItemId = -1;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        System.out.println(getFilesDir().toString());

        sourceCurrencySpinner = (Spinner) findViewById(R.id.source_currency_spinner);
        resultCurrencySpinner = (Spinner) findViewById(R.id.result_currency_spinner);
        sourceQuantityEt = (EditText) findViewById(R.id.source_quantity_et);
        resultQuantityTv = (TextView) findViewById(R.id.result_quantity_tv);
        convertButton = (Button) findViewById(R.id.convert_button);
        sourceQuantityLayout = (TextInputLayout) findViewById(R.id.source_quantity_til);
        sourceQuantityLayout.clearFocus();

        if (savedInstanceState != null) {
            sourceSpinnerItemId = savedInstanceState.getInt("sourceCurrencySpinner", 0);
            resultSpinnerItemId = savedInstanceState.getInt("resultCurrencySpinner", 0);
            resultQuantityTv.setText(savedInstanceState.getString("resultConvert"));
        }

        sourceCurrencySpinner.setAdapter(new CurrencyAdapter(this));
        resultCurrencySpinner.setAdapter(new CurrencyAdapter(this));

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter != null) {
                    presenter.convertClick();
                }
            }
        });

        presenter = new MainPresenterImpl(this, sourceSpinnerItemId, resultSpinnerItemId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sourceCurrencySpinner", sourceCurrencySpinner.getSelectedItemPosition());
        outState.putInt("resultCurrencySpinner", resultCurrencySpinner.getSelectedItemPosition());
        outState.putString("resultConvert", resultQuantityTv.getText().toString());
    }

    @Override
    public CurrencyNode getSourceCurrency() {
        return (CurrencyNode) sourceCurrencySpinner.getSelectedItem();
    }

    @Override
    public CurrencyNode getResultCurrency() {
        return (CurrencyNode) resultCurrencySpinner.getSelectedItem();
    }

    @Override
    public String getSourceQuantity() {
        return sourceQuantityEt.getText().toString();
    }

    @Override
    public void setResultQuantity(String quantity) {
        resultQuantityTv.setText(quantity);
        hideKeyboard();
        sourceQuantityEt.clearFocus();
    }

    @Override
    public void setSourceCurrencyItems(List<CurrencyNode> items) {
        ((CurrencyAdapter) sourceCurrencySpinner.getAdapter()).setItems(items);
    }

    @Override
    public void setResultCurrencyItems(List<CurrencyNode> items) {
        ((CurrencyAdapter) resultCurrencySpinner.getAdapter()).setItems(items);
    }

    @Override
    public void showQuantityError() {
        sourceQuantityLayout.setError(getString(R.string.enter_quantity));
    }

    @Override
    public void hideQuantityError() {
        sourceQuantityLayout.setError("");
    }

    @Override
    public void setSourceCurrencyItemSelected(int id) {
        if (id > 0) {
            sourceCurrencySpinner.setSelection(id);
        }
    }

    @Override
    public void setResultCurrencyItemSelected(int id) {
        if (id > 0) {
            resultCurrencySpinner.setSelection(id);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
