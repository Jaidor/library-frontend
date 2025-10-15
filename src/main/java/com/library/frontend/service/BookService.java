package com.library.frontend.service;

import com.library.frontend.model.Book;
import com.library.frontend.util.ApiResponseWrapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class BookService {

    private static final String BASE_URL = "http://localhost:8099/api/books";
    private static final String API_KEY = "sk_test_D0B7363B3C13480BBB1E12B0398B174D";

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public ApiResponseWrapper.Data<Book> addBook(Book book) {
        HttpEntity<Book> entity = new HttpEntity<>(book, getHeaders());
        ResponseEntity<ApiResponseWrapper> response = restTemplate.exchange(
                BASE_URL, HttpMethod.POST, entity, ApiResponseWrapper.class
        );

        if (response.getBody() != null) {
            return response.getBody().getData();
        }
        return null;
    }

    public ApiResponseWrapper.Data<Book> updateBook(Long id, Book book) {
        HttpEntity<Book> entity = new HttpEntity<>(book, getHeaders());
        ResponseEntity<ApiResponseWrapper> response = restTemplate.exchange(
                BASE_URL + "/" + id, HttpMethod.PUT, entity, ApiResponseWrapper.class
        );
        if(response.getBody() != null){
            return response.getBody().getData();
        }
        return null;
    }

    public ApiResponseWrapper.Data<Void> deleteBook(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<ApiResponseWrapper> response = restTemplate.exchange(
                BASE_URL + "/" + id, HttpMethod.DELETE, entity, ApiResponseWrapper.class
        );
        if(response.getBody() != null){
            return response.getBody().getData();
        }
        return null;
    }

    public List<Book> fetchBooks() {
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<ApiResponseWrapper<Book>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, entity,
                new ParameterizedTypeReference<ApiResponseWrapper<Book>>() {}
        );

        ApiResponseWrapper.Data<Book> body = response.getBody().getData();
        if (body != null && body.getMeta() != null) {
            return body.getMeta().getBooks();
        }
        return List.of();
    }
}