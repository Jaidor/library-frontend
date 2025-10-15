package com.library.frontend.util;

import java.util.List;
import java.util.Map;

public class ApiResponseWrapper<T> {
    private Data<T> data;

    public Data<T> getData() { return data; }
    public void setData(Data<T> data) { this.data = data; }

    public static class Data<T> {
        private boolean status;
        private String message;
        private String statusMessage;
        private int statusCode;
        private Meta<T> meta;

        public boolean isStatus() { return status; }
        public String getMessage() { return message; }
        public String getStatusMessage() { return statusMessage; }
        public int getStatusCode() { return statusCode; }
        public Meta<T> getMeta() { return meta; }  // must exist
    }

    public static class Meta<T> {
        private List<T> books;
        private Map<String, Object> pagination;

        public List<T> getBooks() { return books; }
        public Map<String, Object> getPagination() { return pagination; }
    }
}