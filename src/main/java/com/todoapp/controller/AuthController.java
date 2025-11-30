package com.todoapp.controller;

import com.todoapp.entity.User;
import com.todoapp.service.EmailService;
import com.todoapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            try {
                userService.createPasswordResetTokenForUser(user);
                emailService.sendPasswordResetEmail(user.getEmail(), user.getResetToken());
                redirectAttributes.addFlashAttribute("success", 
                    "Password reset link has been sent to your email");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", 
                    "Failed to send email: " + e.getMessage() + ". Please check email configuration.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "Email not found");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        Optional<User> userOpt = userService.findUserByResetToken(token);
        if (userOpt.isPresent() && userService.isResetTokenValid(userOpt.get())) {
            model.addAttribute("token", token);
            return "reset-password";
        }
        model.addAttribute("error", "Invalid or expired reset token");
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/reset-password?token=" + token;
        }

        Optional<User> userOpt = userService.findUserByResetToken(token);
        if (userOpt.isPresent() && userService.isResetTokenValid(userOpt.get())) {
            userService.updatePassword(userOpt.get(), password);
            redirectAttributes.addFlashAttribute("success", "Password reset successful! Please login.");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid or expired reset token");
        return "redirect:/reset-password?token=" + token;
    }
}

