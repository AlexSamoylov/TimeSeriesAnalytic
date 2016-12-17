package org.dnu.samoylov.controller.sub;

public final class Result {
    float qantil;
    float value;
    String result;

    public Result() {
    }

    public Result(float value, float qantil,  String result) {
        this.qantil = qantil;
        this.value = value;
        this.result = result;
    }

    public float getQantil() {
        return qantil;
    }

    public void setQantil(float qantil) {
        this.qantil = qantil;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
