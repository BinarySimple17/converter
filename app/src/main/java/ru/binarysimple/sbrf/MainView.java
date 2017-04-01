package ru.binarysimple.sbrf;

import java.util.List;

public interface MainView {

    CurrencyNode getSourceCurrency();

    CurrencyNode getResultCurrency();

    void setSourceCurrencyItems(List<CurrencyNode> items);

    void setResultCurrencyItems(List<CurrencyNode> items);

    void setSourceCurrencyItemSelected(int id);

    void setResultCurrencyItemSelected(int id);

    String getSourceQuantity();

    void setResultQuantity(String quantity);

    void showQuantityError();

    void hideQuantityError();
}
