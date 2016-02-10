package com.developer.fabiano.ifprof.model;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

/**
 * Created by fabiano on 12/12/15.
 */
public class Alternativa {
    private EditText editText;
    private TextInputLayout textInputLayout;
    private String Alternava;
    private String texto;

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public void setTextInputLayout(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public String getAlternava() {
        return Alternava;
    }

    public void setAlternava(String alternava) {
        Alternava = alternava;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
