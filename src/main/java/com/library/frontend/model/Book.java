package com.library.frontend.model;

public class Book {
    private String uuid;
    private String title;
    private String author;
    private String isbn;
    private String publishedDate;

    public String getUuid() { return uuid; }
    public void setId(Long id) { this.uuid = uuid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
}
