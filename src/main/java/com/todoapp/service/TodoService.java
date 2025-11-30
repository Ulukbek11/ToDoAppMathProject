package com.todoapp.service;

import com.todoapp.entity.Todo;
import com.todoapp.entity.User;
import com.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> findAllByUser(User user) {
        return todoRepository.findByUser(user);
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }

    public Todo toggleComplete(Long id, User user) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        todo.setCompleted(!todo.getCompleted());
        return todoRepository.save(todo);
    }

    public boolean belongsToUser(Long todoId, User user) {
        return todoRepository.findById(todoId)
                .map(todo -> todo.getUser().getId().equals(user.getId()))
                .orElse(false);
    }
}

