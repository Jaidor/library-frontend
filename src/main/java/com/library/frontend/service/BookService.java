package com.library.frontend.service;

import com.library.frontend.model.Book;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class BookService {
    private static final String BASE_URL = "http://localhost:8099/api/books";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Book> fetchBooks() {
        Book[] books = restTemplate.getForObject(BASE_URL, Book[].class);
        return Arrays.asList(books);
    }

    public void addBook(Book book) {
        restTemplate.postForObject(BASE_URL, book, Book.class);
    }

    public void updateBook(Long id, Book book) {
        restTemplate.put(BASE_URL + "/" + id, book);
    }

    public void deleteBook(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
