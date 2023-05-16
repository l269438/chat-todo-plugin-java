package com.plugin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface pluginsService {

    @GetMapping(value = "/.well-known/ai-plugin.json")
    ResponseEntity<String> pluginManifest(HttpServletRequest request);

    @GetMapping("/openapi.yaml")
    ResponseEntity<String> openapiSpec(HttpServletRequest request);

    @PostMapping("/todos/{username}")
    ResponseEntity<String> addTodo(@PathVariable String username, @RequestBody String todo);

    @GetMapping("/todos/{username}")
    ResponseEntity<List<String>> getTodos(@PathVariable String username);

    @DeleteMapping("/todos/{username}")
    ResponseEntity<String> deleteTodo(@PathVariable String username, @RequestBody int todoIdx);


    @GetMapping("/logo.png")
    ResponseEntity<byte[]> pluginLogo();

}
