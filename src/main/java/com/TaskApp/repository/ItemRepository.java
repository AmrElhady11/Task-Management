package com.TaskApp.repository;

import com.TaskApp.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findById(int id);
    Optional<Item> findByIdAndUserID(int id, int userId);
    void deleteById(int id);
    List<Item> findByUserID(int userId);

}
