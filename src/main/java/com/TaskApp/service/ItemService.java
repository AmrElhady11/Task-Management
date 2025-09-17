package com.TaskApp.service;

import com.TaskApp.request.ItemRequest;
import com.TaskApp.response.ItemResponse;

import java.util.List;


public interface ItemService {
    String addNewItem(ItemRequest itemRequest);
    void deleteItem(int itemId);
    void updateItem(int id ,ItemRequest itemRequest);
    ItemResponse findItem(int id);
    List<ItemResponse> findAllItems();

}
