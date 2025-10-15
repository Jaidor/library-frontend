package com.library.frontend.controller;

import com.library.frontend.model.Book;
import com.library.frontend.service.BookService;
import com.library.frontend.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;

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
    private int currentPage = 0;
    private int totalPages = 1;
    private String currentQuery = "";

    /**
     * Initialize
     */
    @FXML
    public void initialize() {
        // TableView column bindings
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIsbn()));
        publishedDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPublishedDate()));

        // Populate form fields when selecting a row
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                titleField.setText(newSel.getTitle());
                authorField.setText(newSel.getAuthor());
                isbnField.setText(newSel.getIsbn());
                publishedDateField.setText(newSel.getPublishedDate());
            }
        });

        // Setup server-side pagination
        pagination.setPageFactory(this::loadPage);

        // Load first page
        loadBooks(0);
    }

    /**
     * Load books
     *
     * @param page int
     */
    private void loadBooks(int page) {
        var data = bookService.fetchBooks(page); // fetch page with optional search query
        if (data != null && data.getMeta() != null) {
            tableView.setItems(FXCollections.observableArrayList(data.getMeta().getBooks()));

            Map<String, Object> paginationInfo = data.getMeta().getPagination();
            currentPage = (int) paginationInfo.getOrDefault("currentPage", 0);
            totalPages = (int) paginationInfo.getOrDefault("totalPages", 1);

            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(currentPage);
        } else {
            tableView.setItems(FXCollections.observableArrayList());
            pagination.setPageCount(1);
            pagination.setCurrentPageIndex(0);
        }
    }

    private TableView<Book> loadPage(int pageIndex) {
        loadBooks(pageIndex);
        return tableView;
    }

    /**
     * Search handler
     */
    @FXML
    private void handleSearch() {
        currentQuery = searchField.getText().trim();
        loadBooks(0); // reset to first page
    }

    /**
     * Handle refresh
     */
    @FXML
    private void handleRefresh() {
        searchField.clear();
        currentQuery = "";
        loadBooks(0);
    }

    /**
     * Setup Pagination
     */
    private void setupPagination() {
        pagination.setPageFactory(pageIndex -> {
            loadBooks(pageIndex);
            return tableView; // TableView will be updated inside loadBooks
        });
    }

    /**
     * Handle Add Books
     */
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
                AlertUtil.showInfo(response.getMessage());
                loadBooks(currentPage);
            } else {
                AlertUtil.showError(response != null ? response.getMessage() : "Failed to add book.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Failed to add book: " + e.getMessage());
        }
    }

    /**
     * Handle Update
     */
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

        var response = bookService.updateBook(selected.getUuid(), selected);
        if (response != null && response.isStatus()) {
            AlertUtil.showInfo(response.getMessage());
            loadBooks(currentPage);
        } else {
            AlertUtil.showError("Failed to update book.");
        }
    }

    /**
     * Handle Delete
     */
    @FXML
    public void handleDelete() {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Select a book to delete.");
            return;
        }
        var response = bookService.deleteBook(selected.getUuid());
        if (response != null && response.isStatus()) {
            AlertUtil.showInfo(response.getMessage());
            loadBooks(currentPage);
        } else {
            AlertUtil.showError("Failed to delete book.");
        }
    }
}