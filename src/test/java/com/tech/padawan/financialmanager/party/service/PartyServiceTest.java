package com.tech.padawan.financialmanager.party.service;

import com.tech.padawan.financialmanager.champion.model.Champion;
import com.tech.padawan.financialmanager.champion.service.ChampionService;
import com.tech.padawan.financialmanager.party.dto.ChangeChampionInPartyDTO;
import com.tech.padawan.financialmanager.party.dto.CreateUpdatePartyDTO;
import com.tech.padawan.financialmanager.party.dto.SearchedPartyDTO;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.model.PartyLevel;
import com.tech.padawan.financialmanager.party.repository.PartyLevelRepository;
import com.tech.padawan.financialmanager.party.repository.PartyRepository;
import com.tech.padawan.financialmanager.party.service.exception.ChampionAlreadyInThePartyException;
import com.tech.padawan.financialmanager.party.service.exception.PartyNotFoundException;
// Import the User model
import com.tech.padawan.financialmanager.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private ChampionService championService;

    @Mock
    private PartyLevelRepository partyLevelRepository;

    @InjectMocks
    private PartyService partyService;

    private User user;
    private Champion champion;
    private Party party;
    private List<PartyLevel> partyLevels;

    @BeforeEach
    void setUp() {
        // Since the service's constructor fetches the levels, we need to mock this call.
        partyLevels = List.of(new PartyLevel(1L, "Basic", LocalDateTime.now(), 0, 10000));
        when(partyLevelRepository.findAll()).thenReturn(partyLevels);

        // Re-initialize the service to ensure the levels list is loaded from the mock
        partyService = new PartyService(partyRepository, championService, partyLevelRepository);

        // --- UPDATED SECTION ---
        // 1. Create a User instance first
        user = User.builder()
                .id(10L)
                .name("Test User")
                .email("test@example.com")
                .birthdate(LocalDate.now())
                .build();

        // 2. Create the Champion and associate the User with it
        champion = Champion.builder()
                .id(1L)
                .nickname("Ashe")
                .user(user) // Correctly setting the User object
                .build();

        // 3. (Optional but good practice) Set the champion back on the user for bidirectional consistency
        user.setChampion(champion);

        // 4. Create the Party using the fully initialized Champion
        party = Party.builder()
                .id(1L)
                .name("Frost Archers")
                .champions(new ArrayList<>(List.of(champion))) // Use ArrayList to allow modifications
                .balance(BigDecimal.ZERO)
                .points(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should find and return all parties for a user in a paginated way")
    void findAllByUserId_shouldReturnPagedParties() {
        // Arrange
        Long userId = 10L; // This ID now matches our created user's ID
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "name");
        Page<Party> partyPage = new PageImpl<>(List.of(party));

        when(championService.getByUserId(userId)).thenReturn(champion);
        when(partyRepository.findAllByChampionsIdAndNameContainingIgnoreCase(pageRequest, champion.getId(), "")).thenReturn(partyPage);

        // Act
        Page<SearchedPartyDTO> result = partyService.findAllByUserId(userId, "", 1, 10, "name", "ASC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Frost Archers", result.getContent().get(0).name());
        verify(championService).getByUserId(userId);
        verify(partyRepository).findAllByChampionsIdAndNameContainingIgnoreCase(any(PageRequest.class), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should return a party by its ID")
    void getById_shouldReturnParty_whenIdExists() {
        // Arrange
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        // Act
        Party foundParty = partyService.getById(1L);

        // Assert
        assertNotNull(foundParty);
        assertEquals(party.getId(), foundParty.getId());
        verify(partyRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw PartyNotFoundException when ID does not exist")
    void getById_shouldThrowException_whenIdDoesNotExist() {
        // Arrange
        when(partyRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PartyNotFoundException.class, () -> partyService.getById(99L));
        verify(partyRepository).findById(99L);
    }

    @Test
    @DisplayName("Should return a formatted party (DTO) by its ID")
    void getByIdFormatted_shouldReturnDTO_whenIdExists() {
        // Arrange
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        // Act
        SearchedPartyDTO result = partyService.getByIdFormatted(1L);

        // Assert
        assertNotNull(result);
        assertEquals(party.getName(), result.name());
        verify(partyRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create a new party successfully")
    void create_shouldCreateAndReturnParty() {
        // Arrange
        CreateUpdatePartyDTO dto = new CreateUpdatePartyDTO("New Party");
        when(championService.getById(1L)).thenReturn(champion);
        when(partyRepository.save(any(Party.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Party createdParty = partyService.create(1L, dto);

        // Assert
        assertNotNull(createdParty);
        assertEquals("New Party", createdParty.getName());
        assertTrue(createdParty.getChampions().contains(champion));
        assertEquals(BigDecimal.ZERO, createdParty.getBalance());
        verify(championService).getById(1L);
        verify(partyRepository).save(any(Party.class));
    }

    @Test
    @DisplayName("Should update the name of an existing party")
    void update_shouldUpdatePartyName() {
        // Arrange
        CreateUpdatePartyDTO dto = new CreateUpdatePartyDTO("Updated Frost Archers");
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(partyRepository.save(any(Party.class))).thenReturn(party);

        // Act
        SearchedPartyDTO updatedPartyDTO = partyService.update(1L, dto);

        // Assert
        assertNotNull(updatedPartyDTO);
        assertEquals("Updated Frost Archers", updatedPartyDTO.name());
        verify(partyRepository).findById(1L);
        verify(partyRepository).save(party);
    }

    @Test
    @DisplayName("Should add a new champion to a party")
    void addChampion_shouldAddChampionToParty() {
        // Arrange
        User anotherUser = User.builder().id(11L).name("Another User").build();
        Champion newChampion = Champion.builder().id(2L).nickname("Jinx").user(anotherUser).build();
        ChangeChampionInPartyDTO dto = new ChangeChampionInPartyDTO("Jinx");

        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(championService.getByNickname("Jinx")).thenReturn(newChampion);
        when(partyRepository.save(any(Party.class))).thenReturn(party);

        // Act
        SearchedPartyDTO result = partyService.addChampion(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.champions().size());
        assertTrue(party.getChampions().contains(newChampion));
        verify(partyRepository).findById(1L);
        verify(championService).getByNickname("Jinx");
        verify(partyRepository).save(party);
    }

    @Test
    @DisplayName("Should throw ChampionAlreadyInThePartyException when trying to add a champion that is already in the party")
    void addChampion_shouldThrowException_whenChampionIsAlreadyInParty() {
        // Arrange
        ChangeChampionInPartyDTO dto = new ChangeChampionInPartyDTO("Ashe");
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(championService.getByNickname("Ashe")).thenReturn(champion);

        // Act & Assert
        assertThrows(ChampionAlreadyInThePartyException.class, () -> partyService.addChampion(1L, dto));
        verify(partyRepository, never()).save(any(Party.class));
    }

    @Test
    @DisplayName("Should remove a champion from a party")
    void removeChampion_shouldRemoveChampionFromParty() {
        // Arrange
        Champion championToRemove = champion;
        ChangeChampionInPartyDTO dto = new ChangeChampionInPartyDTO("Ashe");

        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(championService.getByNickname("Ashe")).thenReturn(championToRemove);
        when(partyRepository.save(any(Party.class))).thenReturn(party);

        assertTrue(party.getChampions().contains(championToRemove));

        // Act
        SearchedPartyDTO result = partyService.removeChampion(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.champions().size());
        assertFalse(party.getChampions().contains(championToRemove));
        verify(partyRepository).findById(1L);
        verify(championService).getByNickname("Ashe");
        verify(partyRepository).save(party);
    }

    @Test
    @DisplayName("Should delete a party successfully")
    void delete_shouldDeletePartyAndReturnMessage() {
        // Arrange
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        doNothing().when(partyRepository).deleteById(1L);

        // Act
        String resultMessage = partyService.delete(1L);

        // Assert
        assertEquals("Party deleted", resultMessage);
        verify(partyRepository).findById(1L);
        verify(partyRepository).deleteById(1L);
    }
}