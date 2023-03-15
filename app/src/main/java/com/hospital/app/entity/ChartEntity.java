package com.hospital.app.entity;

public class ChartEntity {

    private float value;
    private String text;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
        public ChartEntity(String text, float value){
        this.text = text ;
        this.value = value ;
    }

}

