package com.plugin.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class pluginsServiceImpl implements pluginsService {


    @Override
    public ResponseEntity<String> pluginManifest(HttpServletRequest request) {
        return resourcesReadFileContent(".well-known/ai-plugin.json", MediaType.APPLICATION_JSON);
    }

    @Override
    public ResponseEntity<String> openapiSpec(HttpServletRequest request) {
        return resourcesReadFileContent(".well-known/openapi.yaml", MediaType.TEXT_PLAIN);
    }

    @Override
    public ResponseEntity<String> addTodo(String username, String todo) {
        TodosStorage.addUserTodo(username, todo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTodos(String username) {
        List<String> userTodos = TodosStorage.getUserTodos(username);
        return new ResponseEntity<>(userTodos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteTodo(String username, int todoIdx) {
        TodosStorage.removeUserTodo(username, todoIdx);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Override
    public ResponseEntity<byte[]> pluginLogo() {
        {
            String filePath = "logo.png";
            Path path = Paths.get(filePath);
            try {
                byte[] fileContent = Files.readAllBytes(path);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(fileContent);
            } catch (IOException e) {
                System.err.println("Failed to read file: " + filePath);
                e.printStackTrace();
                return ResponseEntity.status(500)
                        .body(null);
            }
        }
    }

    public static class TodosStorage {
        private static final ConcurrentHashMap<String, List<String>> todos = new ConcurrentHashMap<>();

        public static List<String> getUserTodos(String username) {
            return todos.computeIfAbsent(username, k -> new ArrayList<>());
        }

        public static void addUserTodo(String username, String todo) {
            getUserTodos(username).add(todo);
        }

        public static void removeUserTodo(String username, int todoIdx) {
            List<String> userTodos = getUserTodos(username);
            if (0 <= todoIdx && todoIdx < userTodos.size()) {
                userTodos.remove(todoIdx);
            }
        }
    }

    private ResponseEntity<String> resourcesReadFileContent(String filePath, MediaType mediaType) {
        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] fileContent = FileCopyUtils.copyToByteArray(inputStream);
            String content = new String(fileContent, StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(content);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + filePath);
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Failed to read file: " + filePath);
        }
    }

}
