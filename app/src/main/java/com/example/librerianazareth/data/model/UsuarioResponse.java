package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UsuarioResponse {

    @SerializedName("count")
    private int count;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<Usuario> results;

    public int getCount() { return count; }
    public String getNext() { return next; }
    public String getPrevious() { return previous; }
    public List<Usuario> getResults() { return results; }
}
