package ru.binarysimple.sbrf;

public class CurrencyNode {
    private String id;
    private Integer numCode;
    private String charCode;
    private Integer nominal;
    private String name;
    private Float value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumCode() {
        return numCode;
    }

    public void setNumCode(Integer numCode) {
        this.numCode = numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public Integer getNominal() {
        return nominal;
    }

    public void setNominal(Integer nominal) {
        this.nominal = nominal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
