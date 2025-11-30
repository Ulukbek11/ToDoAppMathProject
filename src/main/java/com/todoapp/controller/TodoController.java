package com.todoapp.controller;

import com.todoapp.entity.Todo;
import com.todoapp.entity.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listTodos(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Todo> todos = todoService.findAllByUser(user);
        model.addAttribute("todos", todos);
        model.addAttribute("todo", new Todo());
        return "todos";
    }

    @PostMapping
    public String createTodo(@Valid @ModelAttribute("todo") Todo todo,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Title is required");
            return "redirect:/todos";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        todo.setUser(user);
        todoService.save(todo);
        redirectAttributes.addFlashAttribute("success", "Todo created successfully");
        return "redirect:/todos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleComplete(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            todoService.toggleComplete(id, user);
            redirectAttributes.addFlashAttribute("success", "Todo updated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/todos";
    }

    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (todoService.belongsToUser(id, user)) {
            todoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Todo deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
        }
        return "redirect:/todos";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo todo = todoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            return "redirect:/todos";
        }

        model.addAttribute("todo", todo);
        return "edit-todo";
    }

    @PostMapping("/{id}/edit")
    public String updateTodo(@PathVariable Long id,
                            @Valid @ModelAttribute("todo") Todo todo,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit-todo";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo existingTodo = todoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!existingTodo.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/todos";
        }

        existingTodo.setTitle(todo.getTitle());
        existingTodo.setDescription(todo.getDescription());
        todoService.save(existingTodo);
        redirectAttributes.addFlashAttribute("success", "Todo updated successfully");
        return "redirect:/todos";
    }
}

