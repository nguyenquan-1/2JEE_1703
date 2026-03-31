package com.example.quanlysach2.controller;

import com.example.quanlysach2.entity.Book;
import com.example.quanlysach2.entity.CartItem;
import com.example.quanlysach2.repository.BookRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooks(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "sort", defaultValue = "") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        int pageSize = 5;

        Sort sorting = Sort.unsorted();
        if ("priceAsc".equals(sort)) {
            sorting = Sort.by("price").ascending();
        } else if ("priceDesc".equals(sort)) {
            sorting = Sort.by("price").descending();
        }

        Pageable pageable = PageRequest.of(page, pageSize, sorting);
        Page<Book> bookPage;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        if (hasKeyword && hasCategory) {
            bookPage = bookRepository.findByTitleContainingIgnoreCaseAndCategory(keyword, category, pageable);
        } else if (hasKeyword) {
            bookPage = bookRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if (hasCategory) {
            bookPage = bookRepository.findByCategory(category, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        List<String> categories = bookRepository.findDistinctByOrderByCategoryAsc()
                .stream()
                .map(Book::getCategory)
                .distinct()
                .toList();

        model.addAttribute("bookPage", bookPage);
        model.addAttribute("listBooks", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", categories);

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

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Integer id,
                            @RequestParam(name = "quantity", defaultValue = "1") Integer quantity,
                            HttpSession session) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách có id: " + id));

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getBookId().equals(id)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(new CartItem(book.getId(), book.getTitle(), book.getPrice(), quantity));
        }

        session.setAttribute("cart", cart);
        return "redirect:/books";
    }
}