package com.TaskApp.controller;

import com.TaskApp.request.ItemRequest;
import com.TaskApp.response.ItemResponse;
import com.TaskApp.service.ItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/add")
    public ResponseEntity<String> addItem(@RequestBody ItemRequest request) {
        return ResponseEntity.ok(itemService.addNewItem(request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable int id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateItem(@PathVariable int id, @RequestBody ItemRequest request) {
        itemService.updateItem(id,request);
        return ResponseEntity.ok("Item updated");
    }
    @GetMapping("/search/{id}")
    public ResponseEntity<ItemResponse> searchItem(@PathVariable int id) {
        ItemResponse response = itemService.findItem(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getAllTasks")
    public ResponseEntity<List<ItemResponse>> searchItem() {
        return ResponseEntity.ok(itemService.findAllItems());

    }

}
