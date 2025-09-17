package com.TaskApp.service.Impl;

import com.TaskApp.entity.Item;
import com.TaskApp.entity.ItemDetails;
import com.TaskApp.entity.User;
import com.TaskApp.exception.NotFoundException;
import com.TaskApp.repository.ItemDetailsRepository;
import com.TaskApp.repository.ItemRepository;
import com.TaskApp.request.ItemRequest;
import com.TaskApp.response.ItemResponse;
import com.TaskApp.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemDetailsRepository itemDetailsRepository;
    @Override
    @Transactional
    public String addNewItem(ItemRequest request) {
      ItemDetails itemDetails = ItemDetails.builder()
                        .description(request.getDescription())
                        .createdAt(LocalDateTime.now())
                        .status(request.getStatus())
                        .priority(request.getPriority())
                        .build();
     itemDetailsRepository.save(itemDetails);

     Item item=   Item.builder()
                .title(request.getTitle())
                .userID(getUserId())
                .itemDetails(itemDetails)
                .build();
     itemRepository.save(item);
     return "Item added successfully";

    }

    @Override
    @Transactional
    public void deleteItem(int itemId) {
        Item item =  getItemByIdAndUserId(itemId);
        itemRepository.deleteById(itemId);
        itemDetailsRepository.deleteById(item.getItemDetails().getId());


    }

    @Override
    @Transactional
    public void updateItem(int itemId ,ItemRequest request) {
        Item item =getItemByIdAndUserId(itemId);

        ItemDetails itemDetails = itemDetailsRepository.findById(item.getItemDetails().getId()).orElseThrow(()-> new NotFoundException(String.format("Item with ID %d not found", item.getItemDetails().getId())));

        itemDetails.setDescription(request.getDescription());
        itemDetails.setStatus(request.getStatus());
        itemDetails.setPriority(request.getPriority());
        itemDetailsRepository.save(itemDetails);
        item.setTitle(request.getTitle());
        item.setItemDetails(itemDetails);
        itemRepository.save(item);
    }

    @Override
    public ItemResponse findItem(int itemId) {
        Item item = getItemByIdAndUserId(itemId);
        ItemDetails itemDetails = itemDetailsRepository.findById(item.getItemDetails().getId()).orElseThrow(()-> new NotFoundException(String.format("Item with ID %d not found", item.getItemDetails().getId())));
       ItemResponse response = ItemResponse.builder()
               .itemId(item.getId())
               .title(item.getTitle())
               .description(itemDetails.getDescription())
               .status(itemDetails.getStatus())
               .priority(itemDetails.getPriority())
               .build();
        return response;
    }

    @Override
    public List<ItemResponse> findAllItems() {
        List<Item> items = itemRepository.findByUserID(getUserId());
        List<ItemResponse> itemResponses = new ArrayList<>();
            items.forEach((item) -> {
                ItemResponse response = ItemResponse.builder()
                        .itemId(item.getId())
                        .title(item.getTitle())
                        .description(item.getItemDetails().getDescription())
                        .status(item.getItemDetails().getStatus())
                        .priority(item.getItemDetails().getPriority())
                        .build();
                itemResponses.add(response);
            });

        return itemResponses;
    }

    private int getUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         User user =  (User) auth.getPrincipal();
        return user.getId();
    }
    private Item getItemByIdAndUserId(int itemId) {
        return itemRepository.findByIdAndUserID(itemId, getUserId()).orElseThrow(()-> new NotFoundException(String.format("Item with ID %d not found", itemId)));
    }
}
