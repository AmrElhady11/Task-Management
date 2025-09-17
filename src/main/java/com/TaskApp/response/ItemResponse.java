package com.TaskApp.response;

import com.TaskApp.enums.ItemPriority;
import com.TaskApp.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponse {
    private int itemId;
    private String title ;
    private String description ;
    private ItemPriority priority;
    private ItemStatus status;
}
