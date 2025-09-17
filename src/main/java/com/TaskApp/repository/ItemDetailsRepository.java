package com.TaskApp.repository;

import com.TaskApp.entity.ItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDetailsRepository extends JpaRepository<ItemDetails, Integer> {

}
