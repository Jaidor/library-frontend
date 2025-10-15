package com.library.frontend.controller;

import com.library.frontend.model.Book;
import com.library.frontend.service.BookService;
import com.library.frontend.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class BookController {

    @FXML private TableView<Book> tableView;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> publishedDateColumn;
    @FXML private TextField titleField, authorField, isbnField, publishedDateField, searchField;
    @FXML private Pagination pagination;

    private final BookService bookService = new BookService();
    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10;

    @FXML
    public void initialize() {
        // Set up table columns
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIsbn()));
        publishedDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPublishedDate()));

        // Add selection listener to populate fields when a row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if(newSel != null){
                titleField.setText(newSel.getTitle());
                authorField.setText(newSel.getAuthor());
                isbnField.setText(newSel.getIsbn());
                publishedDateField.setText(newSel.getPublishedDate());
            }
        });

        // Load books and setup pagination
        loadBooks();
        setupPagination();
    }

    @FXML
    public void loadBooks() {
        try {
            List<Book> list = bookService.fetchBooks();
            books.setAll(list);
            pagination.setPageCount((int) Math.ceil((double) books.size() / ROWS_PER_PAGE));
            updateTable(0);
        } catch (Exception e) {
            AlertUtil.showError("Failed to load books: " + e.getMessage());
        }
    }

    private void updateTable(int index) {
        int start = index * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, books.size());
        tableView.setItems(FXCollections.observableArrayList(books.subList(start, end)));
    }

    private void setupPagination() {
        pagination.setPageFactory(pageIndex -> {
            updateTable(pageIndex);
            return tableView;
        });
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();
        List<Book> filtered = books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query)
                        || b.getAuthor().toLowerCase().contains(query))
                .collect(Collectors.toList());
        pagination.setPageCount((int) Math.ceil((double) filtered.size() / ROWS_PER_PAGE));
        pagination.setPageFactory(pageIndex -> {
            int start = pageIndex * ROWS_PER_PAGE;
            int end = Math.min(start + ROWS_PER_PAGE, filtered.size());
            tableView.setItems(FXCollections.observableArrayList(filtered.subList(start, end)));
            return tableView;
        });
        if (!filtered.isEmpty()) pagination.setCurrentPageIndex(0);
    }

    @FXML
    public void handleAdd() {
        try {
            Book book = new Book();
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            book.setPublishedDate(publishedDateField.getText());
            var response = bookService.addBook(book);
            if (response != null && response.isStatus()) {
                AlertUtil.showInfo(response.getMessage()); // Show backend message
                loadBooks();
            } else {
                AlertUtil.showError(response != null ? response.getMessage() : "Failed to add book.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Failed to add book: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdate() {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Select a book to update.");
            return;
        }
        selected.setTitle(titleField.getText());
        selected.setAuthor(authorField.getText());
        selected.setIsbn(isbnField.getText());
        selected.setPublishedDate(publishedDateField.getText());
        var response = bookService.updateBook(selected.getId(), selected);
        if (response != null && response.isStatus()) {
            AlertUtil.showInfo(response.getMessage());
            loadBooks();
        } else {
            AlertUtil.showError("Failed to update book.");
        }
    }

    @FXML
    public void handleDelete() {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Select a book to delete.");
            return;
        }
        var response = bookService.deleteBook(selected.getId());
        if (response != null && response.isStatus()) {
            AlertUtil.showInfo(response.getMessage());
            loadBooks();
        } else {
            AlertUtil.showError("Failed to delete book.");
        }
    }
}