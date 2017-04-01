package ru.binarysimple.sbrf;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainPresenterImpl implements MainPresenter {

    private static String link = "http://www.cbr.ru/scripts/XML_daily.asp";
    private static String filename = "data.xml";
    private static String tempFilename = "temp.xml";

    private final MainView mainView;
    private final int sourceSpinnerItemId;
    private final int resultSpinnerItemId;
    private CurrencyNode sourceCurrencyNode;
    private CurrencyNode resultCurrencyNode;

    public MainPresenterImpl(final MainView mainView, int sourceSelectedId, int resultSelectedId) {
        this.mainView = mainView;
        this.sourceSpinnerItemId = sourceSelectedId;
        this.resultSpinnerItemId = resultSelectedId;

        System.out.println(String.valueOf(sourceSelectedId));
        String path = ((Activity) mainView).getFilesDir().toString();
        DownloadFileP downloadFile = new DownloadFileP();
        downloadFile.execute(path);
    }

    private static synchronized List<CurrencyNode> readXML2(String filename) {
        List<CurrencyNode> currencyNodeList = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream inputStream = new FileInputStream(filename);
            Document document = builder.parse(inputStream);
            Element valCurs = document.getDocumentElement();
            NodeList valuteList = valCurs.getChildNodes();
            for (int i = 0; i < valuteList.getLength(); i++) {
                Node valute = valuteList.item(i);
                if (valute instanceof Element) {
                    Element valuteElement = (Element) valute;
                    currencyNodeList.add(getProperty(valuteElement));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currencyNodeList;
    }

    private static CurrencyNode getProperty(Element valuteElement) {
        CurrencyNode currencyNode = new CurrencyNode();
        NodeList valuteElementList = valuteElement.getChildNodes();
        for (int i = 0; i < valuteElementList.getLength(); i++) {
            Node valuteProperty = valuteElementList.item(i);
            if (valuteProperty instanceof Element) {
                Element property = (Element) valuteProperty;
                Text textNode = (Text) property.getFirstChild();
                String text = textNode.getData().trim();
                String localName = property.getTagName();
                switch (localName) {
                    case "NumCode":
                        currencyNode.setNumCode(Integer.valueOf(text));
                        break;
                    case "CharCode":
                        currencyNode.setCharCode(text);
                        break;
                    case "Nominal":
                        currencyNode.setNominal(Integer.valueOf(text));
                        break;
                    case "Name":
                        currencyNode.setName(text);
                        break;
                    case "Value":
                        currencyNode.setValue(Float.valueOf(text.replace(",", ".")));
                        break;
                }
            }
        }
        return currencyNode;
    }

    @Override
    public void convertClick() {
        if (mainView != null) {
            sourceCurrencyNode = mainView.getSourceCurrency();
            resultCurrencyNode = mainView.getResultCurrency();

            mainView.setResultQuantity(convert(sourceCurrencyNode, resultCurrencyNode, mainView.getSourceQuantity()));
        }
    }

    private String convert(@NonNull CurrencyNode source, @NonNull CurrencyNode result, @NonNull String quantitySource) {
        if (quantitySource.isEmpty()) {
            quantitySource = "0";
            mainView.showQuantityError();
        } else {
            mainView.hideQuantityError();
        }
        Float sourceValue = source.getValue();
        Integer sourceNominal = source.getNominal();
        Float quantity = Float.valueOf(quantitySource);
        Float resultValue = result.getValue();
        Integer resultNominal = result.getNominal();
        Float ratio = (sourceValue * resultNominal) / (resultValue * sourceNominal);
        String res = CurrOps.mult(ratio.toString(), quantity.toString());
        res = CurrOps.currRound(res, 2);
        return res;
    }

    private class DownloadFileP extends AsyncTask<String, Void, Boolean> {

        private String path;

        @Override
        protected Boolean doInBackground(String... params) {
            path = params[0];
            return loadXML(path + "/" + tempFilename);
        }

        @Override
        protected void onPostExecute(Boolean loadOK) {
            if (loadOK) {
                try {
                    overwriteDataFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isDataFileExists()) {
                mainView.setSourceCurrencyItems(readXML2(path + "/" + filename));
                mainView.setResultCurrencyItems(readXML2(path + "/" + filename));
                mainView.setSourceCurrencyItemSelected(sourceSpinnerItemId);
                mainView.setResultCurrencyItemSelected(resultSpinnerItemId);
            }
        }

        private Boolean loadXML(String filename) {
            try {
                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.connect();
                FileOutputStream fileOutput = new FileOutputStream(filename);
                InputStream inputStream = urlConnection.getInputStream();

                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                }

                fileOutput.close();
                return downloadedSize != 0;
            } catch (IOException e) {
                Log.e("err", e.getMessage());
                return false;
            }
        }

        private synchronized void overwriteDataFile() throws IOException {
            FileOutputStream output = new FileOutputStream(path + "/" + filename, false);
            FileInputStream input = new FileInputStream(path + "/" + tempFilename);

            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = input.read(buffer)) > 0) {
                output.write(buffer, 0, bufferLength);
            }
            output.flush();
            output.close();
            input.close();
        }

        private boolean isDataFileExists() {
            File file = new File(path + "/" + filename);
            return file.exists();
        }
    }
}
