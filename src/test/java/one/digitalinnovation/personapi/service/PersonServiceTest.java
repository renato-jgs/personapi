package one.digitalinnovation.personapi.service;

import lombok.var;

import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.dto.response.MessageResponseDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.PersonNotFoundException;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import one.digitalinnovation.personapi.repository.PersonRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static one.digitalinnovation.personapi.utils.PersonUtils.createFakeDTO;
import static one.digitalinnovation.personapi.utils.PersonUtils.createFakeEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    @Test
    void testGivenPersonDTOThenReturnSavedMessage() {
        PersonDTO personDTO = createFakeDTO();
        Person expectedSavedPerson = createFakeEntity();

        when(personRepository.save(any(Person.class))).thenReturn(expectedSavedPerson);

        MessageResponseDTO expectedSuccessMessage = createExpectedMessageResponse(expectedSavedPerson.getId());
        MessageResponseDTO successMessage = personService.createPerson(personDTO);

        assertEquals(expectedSuccessMessage, successMessage);
    }

//    @Test
//    void testGivenValidPersonIdThenReturnThisPerson() throws PersonNotFoundException {
//        PersonDTO expectedPersonDTO = createFakeDTO();
//        Person expectedSavedPerson = createFakeEntity();
//        expectedPersonDTO.setId(expectedSavedPerson.getId());
//
//        when(personRepository.findById(expectedSavedPerson.getId())).thenReturn(Optional.of(expectedSavedPerson));
//        when(personMapper.toDTO(expectedSavedPerson)).thenReturn(expectedPersonDTO);
//
//        PersonDTO personDTO = personService.findById(expectedSavedPerson.getId());
//
//        assertEquals(expectedPersonDTO, personDTO);
//        assertEquals(expectedSavedPerson.getId(), personDTO.getId());
//        assertEquals(expectedSavedPerson.getFirstName(), personDTO.getFirstName());
//    }

    @Test
    void testGivenInvalidPersonIdThenThrowException() {
        var invalidPersonId = 1L;

        when(personRepository.findById(invalidPersonId)).thenReturn(Optional.ofNullable(any(Person.class)));
        assertThrows(PersonNotFoundException.class, () -> personService.findById(invalidPersonId));
    }

//    @Test
//    void testGivenNoDataThenReturnAllPeopleRegistered() {
//        List<Person> expectedRegisteredPeople = Collections.singletonList(createFakeEntity());
//        PersonDTO personDTO = createFakeDTO();
//
//        when(personRepository.findAll()).thenReturn(expectedRegisteredPeople);
//        when(personMapper.toDTO(any(Person.class))).thenReturn(personDTO);
//
//        List<PersonDTO> expectedPeopleDTOList = personService.listAll();
//
//        assertFalse(expectedPeopleDTOList.isEmpty());
//        assertEquals(expectedPeopleDTOList.get(0).getId(), personDTO.getId());
//    }

    @Test
    void testGivenValidPersonIdAndUpdateInfoThenReturnSuccessOnUpdate() throws PersonNotFoundException {
        var updatedPersonId = 2L;

        PersonDTO updatePersonDTORequest = createFakeDTO();
        updatePersonDTORequest.setId(updatedPersonId);
        updatePersonDTORequest.setLastName("Santana updated");

        Person expectedPersonToUpdate = createFakeEntity();
        expectedPersonToUpdate.setId(updatedPersonId);

        Person expectedPersonUpdated = createFakeEntity();
        expectedPersonUpdated.setId(updatedPersonId);
        expectedPersonToUpdate.setLastName(updatePersonDTORequest.getLastName());

        when(personRepository.findById(updatedPersonId)).thenReturn(Optional.of(expectedPersonUpdated));
        when(personRepository.save(any(Person.class))).thenReturn(expectedPersonUpdated);

        MessageResponseDTO successMessage = personService.update(updatedPersonId, updatePersonDTORequest);

        assertEquals("Person successfully updated with ID 2", successMessage.getMessage());
    }

    @Test
    void testGivenInvalidPersonIdAndUpdateInfoThenThrowExceptionOnUpdate() throws PersonNotFoundException {
        var invalidPersonId = 1L;

        PersonDTO updatePersonDTORequest = createFakeDTO();
        updatePersonDTORequest.setId(invalidPersonId);
        updatePersonDTORequest.setLastName("Santana updated");

        when(personRepository.findById(invalidPersonId))
                .thenReturn(Optional.ofNullable(any(Person.class)));

        assertThrows(PersonNotFoundException.class, () -> personService.update(invalidPersonId, updatePersonDTORequest));
    }

    @Test
    void testGivenValidPersonIdThenReturnSuccessOnDelete() throws PersonNotFoundException {
        var deletedPersonId = 1L;
        Person expectedPersonToDelete = createFakeEntity();

        when(personRepository.findById(deletedPersonId)).thenReturn(Optional.of(expectedPersonToDelete));
        personService.delete(deletedPersonId);

        verify(personRepository, times(1)).deleteById(deletedPersonId);
    }

    @Test
    void testGivenInvalidPersonIdThenReturnSuccessOnDelete() throws PersonNotFoundException {
        var invalidPersonId = 1L;

        when(personRepository.findById(invalidPersonId))
                .thenReturn(Optional.ofNullable(any(Person.class)));

        assertThrows(PersonNotFoundException.class, () -> personService.delete(invalidPersonId));
    }

    private MessageResponseDTO createExpectedMessageResponse(Long id) {
        return MessageResponseDTO
                .builder()
                .message("Create person with ID: " + id)
                .build();
    }
}
