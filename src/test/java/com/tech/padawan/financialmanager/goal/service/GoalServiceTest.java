package com.tech.padawan.financialmanager.goal.service;

import com.tech.padawan.financialmanager.goal.dto.CreateGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.SearchedGoalDTO;
import com.tech.padawan.financialmanager.goal.dto.UpdateGoalDTO;
import com.tech.padawan.financialmanager.goal.model.Goal;
import com.tech.padawan.financialmanager.goal.repository.GoalRepository;
import com.tech.padawan.financialmanager.goal.service.exception.GoalNotFoundException;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoalServiceTest {

    @Mock
    private GoalRepository repository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private GoalService service;

    private User mockUser;
    private Goal mockGoal;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        mockUser = User.builder().id(1L).name("User Test").build();

        mockGoal = Goal.builder()
                .id(1L)
                .name("Buy a Car")
                .targetAmount(10000.0)
                .savedAmount(2500.0)
                .reason("Mobility")
                .deadline(new Date())
                .user(mockUser)
                .build();
    }

    @Test
    @DisplayName("Should return all goals paginated")
    void findAll() {
        Page<Goal> page = new PageImpl<>(List.of(mockGoal));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SearchedGoalDTO> result = service.findAll(0, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should return goal by ID")
    void getById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));

        SearchedGoalDTO dto = service.getById(1L);

        assertEquals(mockGoal.getName(), dto.name());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when goal not found")
    void getById_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> service.getById(2L));
    }

    @Test
    @DisplayName("Should create a goal")
    void create() {
        CreateGoalDTO dto = new CreateGoalDTO(
                "Buy a Car", 10000.0, 2500.0, "Mobility", new Date(), 1L
        );

        when(userService.getUserEntityById(1L)).thenReturn(mockUser);
        when(repository.save(any(Goal.class))).thenReturn(mockGoal);

        Goal created = service.create(dto);

        assertEquals(dto.name(), created.getName());
        verify(userService).getUserEntityById(1L);
        verify(repository).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should update a goal")
    void update() {
        UpdateGoalDTO dto = new UpdateGoalDTO(
                "Buy House", 200000.0, 10000.0, "Investment", new Date()
        );

        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(repository.save(any(Goal.class))).thenReturn(mockGoal);

        SearchedGoalDTO result = service.update(1L, dto);

        assertEquals(dto.name(), result.name());
        verify(repository).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should return goals by user ID paginated")
    void findAllByUserId() {
        Page<Goal> page = new PageImpl<>(List.of(mockGoal));
        when(repository.findAllByUserId(any(Pageable.class), eq(1L))).thenReturn(page);

        Page<SearchedGoalDTO> result = service.findAllByUserId(1L, 0, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(repository).findAllByUserId(any(Pageable.class), eq(1L));
    }

    @Test
    @DisplayName("Should delete goal by ID")
    void delete() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));

        String result = service.delete(1L);

        assertEquals("Goal deleted", result);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should update only saved amount")
    void updateSaveAmount() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(repository.save(any(Goal.class))).thenReturn(mockGoal);

        SearchedGoalDTO dto = service.updateSaveAmount(1L, 5000.0);

        assertEquals(5000.0, dto.savedAmount());
        verify(repository).save(any(Goal.class));
    }
}
