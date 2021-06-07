package com.freetonleague.core.services;

import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.UserRepository;
import com.freetonleague.core.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;

import static com.freetonleague.core.utils.MockDataGenerator.generateUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Component
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final User EXISTED = generateUser();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userRepository.findByLeagueId(EXISTED.getLeagueId())).thenReturn(EXISTED);
        when(userRepository.findByUsername(any())).thenReturn(EXISTED);
    }

    @Test
    public void get() {
        assertEquals(userService.findByLeagueId(EXISTED.getLeagueId()), EXISTED);
        verify(userRepository, times(1)).findByLeagueId(EXISTED.getLeagueId());
    }
}
