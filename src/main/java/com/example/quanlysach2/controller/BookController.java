package com.example.quanlysach2.controller;

import com.example.quanlysach2.entity.Book;
import com.example.quanlysach2.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("listBooks", bookRepository.findAll());
        return "books";
    }

    @GetMapping("/books/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("pageTitle", "Thêm sách mới");
        return "book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute("book") Book book,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle",
                    book.getId() == null ? "Thêm sách mới" : "Cập nhật sách");
            return "book-form";
        }

        bookRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách có id: " + id));

        model.addAttribute("book", book);
        model.addAttribute("pageTitle", "Cập nhật sách");
        return "book-form";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy sách có id: " + id);
        }

        bookRepository.deleteById(id);
        return "redirect:/books";
    }
}