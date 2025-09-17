package com.TaskApp.service.Impl;

import com.TaskApp.request.ItemRequest;
import com.TaskApp.response.ItemResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemServiceImpl Unit Tests")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    private ItemRequest itemRequest;
    private User mockUser;

    private void initUser() {
        mockUser = new User();
        mockUser.setRole(Role.USER);
        mockUser.setId(123);

        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeEach
    void setUp() {
        initUser();
        itemRequest = ItemRequest.builder()
                .title("Test Item")
                .description("Test Description")
                .status(ItemStatus.IN_PROGRESS)
                .priority(ItemPriority.HIGH)
                .build();
    }

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {
        @Test
        void shouldAddNewItemSuccessfully() {
            when(itemDetailsRepository.save(any(ItemDetails.class))).thenAnswer(inv -> {
                ItemDetails details = inv.getArgument(0);
                details.setId(1);
                return details;
            });
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> {
                Item item = inv.getArgument(0);
                item.setId(1);
                return item;
            });

            String result = itemServiceImpl.addNewItem(itemRequest);

            assertEquals("Item added successfully", result);
            verify(itemDetailsRepository).save(any(ItemDetails.class));
            verify(itemRepository).save(any(Item.class));
        }
    }

    @Nested
    @DisplayName("Delete Item Tests")
    class DeleteItemTests {
        @Test
        void shouldDeleteItemSuccessfully() {
            ItemDetails details = ItemDetails.builder().id(2).build();
            Item item = Item.builder().id(1).userID(mockUser.getId()).itemDetails(details).build();

            when(itemRepository.findByIdAndUserId(1, mockUser.getId())).thenReturn(Optional.of(item));

            itemServiceImpl.deleteItem(1);

            verify(itemRepository).deleteById(1);
            verify(itemDetailsRepository).deleteById(2);
        }

        @Test
        void shouldThrowExceptionIfItemNotFound() {
            when(itemRepository.findByIdAndUserId(1, mockUser.getId())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> itemServiceImpl.deleteItem(1));
        }
    }

    @Nested
    @DisplayName("Update Item Tests")
    class UpdateItemTests {
        @Test
        void shouldUpdateItemSuccessfully() {
            ItemDetails details = ItemDetails.builder().id(2).build();
            Item item = Item.builder().id(1).title("Old Title").userID(mockUser.getId()).itemDetails(details).build();

            when(itemRepository.findByIdAndUserId(1, mockUser.getId())).thenReturn(Optional.of(item));
            when(itemDetailsRepository.findById(2)).thenReturn(Optional.of(details));

            itemServiceImpl.updateItem(1, itemRequest);

            verify(itemDetailsRepository).save(any(ItemDetails.class));
            verify(itemRepository).save(any(Item.class));
        }

        @Test
        void shouldThrowExceptionIfItemDetailsNotFound() {
            ItemDetails details = ItemDetails.builder().id(2).build();
            Item item = Item.builder().id(1).userID(mockUser.getId()).itemDetails(details).build();

            when(itemRepository.findByIdAndUserId(1, mockUser.getId())).thenReturn(Optional.of(item));
            when(itemDetailsRepository.findById(2)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> itemServiceImpl.updateItem(1, itemRequest));
        }
    }

    @Nested
    @DisplayName("Find Item Tests")
    class FindItemTests {
        @Test
        void shouldFindItemSuccessfully() {
            ItemDetails details = ItemDetails.builder()
                    .id(2).description("Desc").status(ItemStatus.IN_PROGRESS).priority(ItemPriority.HIGH).build();
            Item item = Item.builder().id(1).title("Title").userID(mockUser.getId()).itemDetails(details).build();

            when(itemRepository.findByIdAndUserId(1, mockUser.getId())).thenReturn(Optional.of(item));
            when(itemDetailsRepository.findById(2)).thenReturn(Optional.of(details));

            ItemResponse response = itemServiceImpl.findItem(1);

            assertEquals("Title", response.getTitle());
            assertEquals("Desc", response.getDescription());
            verify(itemRepository).findByIdAndUserId(1, mockUser.getId());
        }

        @Test
        void shouldThrowExceptionIfItemNotFound() {
            when(itemRepository.findByIdAndUserId(1, mockUser.getId())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> itemServiceImpl.findItem(1));
        }
    }

    @Nested
    @DisplayName("Find All Items Tests")
    class FindAllItemsTests {
        @Test
        void shouldReturnAllItemsForUser() {
            ItemDetails details = ItemDetails.builder()
                    .id(2).description("Desc").status(ItemStatus.IN_PROGRESS).priority(ItemPriority.HIGH).build();
            Item item = Item.builder().id(1).title("Title").userID(mockUser.getId()).itemDetails(details).build();

            when(itemRepository.findByUserId(mockUser.getId())).thenReturn(List.of(item));

            List<ItemResponse> responses = itemServiceImpl.findAllItems();

            assertEquals(1, responses.size());
            assertEquals("Title", responses.get(0).getTitle());
            verify(itemRepository).findByUserId(mockUser.getId());
        }
    }
}
