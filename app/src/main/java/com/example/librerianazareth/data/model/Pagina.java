package com.example.librerianazareth.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Envoltorio de respuesta paginada del backend (PageNumberPagination de Django REST).
 * El backend devuelve: { "count": n, "next": "...", "previous": "...", "results": [...] }
 */
public class Pagina<T> {

    @SerializedName("count")
    private int count;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<T> results;

    public int getCount() { return count; }
    public String getNext() { return next; }
    public String getPrevious() { return previous; }

    public List<T> getResults() {
        return results;
    }
}
