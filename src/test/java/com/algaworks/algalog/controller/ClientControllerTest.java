package com.algaworks.algalog.controller;

import com.algaworks.algalog.domain.dto.ClientRequestDto;
import com.algaworks.algalog.domain.dto.ClientResponseDto;
import com.algaworks.algalog.domain.enums.DataForBusinessException;
import com.algaworks.algalog.domain.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static com.algaworks.algalog.DataForTests.INVALID_CLIENT_ID;
import static com.algaworks.algalog.DataForTests.INVALID_EMAIL;
import static com.algaworks.algalog.DataForTests.INVALID_NAME_WITH_FOUR_EQUALS_CHARACTERS_IN_SEQUENCE;
import static com.algaworks.algalog.DataForTests.INVALID_NAME_WITH_SPECIAL_CHARACTERS;
import static com.algaworks.algalog.DataForTests.INVALID_SIZE_NAME;
import static com.algaworks.algalog.DataForTests.INVALID_TELEPHONE;
import static com.algaworks.algalog.DataForTests.VALID_CLIENT_ID;
import static com.algaworks.algalog.DataForTests.VALID_EMAIL;
import static com.algaworks.algalog.DataForTests.VALID_NAME;
import static com.algaworks.algalog.DataForTests.VALID_TELEPHONE;
import static com.algaworks.algalog.DataForTests.clientRequestDtoValid;
import static com.algaworks.algalog.DataForTests.clientResponseDtoValid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClientControllerTest extends BaseIntegrationTest {

    private final static String URI = "/clients";

    @MockBean
    private ClientService clientService;

    @Test
    void shouldReturnAllClients() throws Exception {

        ClientResponseDto clientResponseDto = clientResponseDtoValid();

        when(clientService.findAll()).thenReturn(Collections.singletonList(clientResponseDto));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(clientResponseDto.getId()));

    }

    @Test
    void shouldReturnClientResponseDto_whenClientIdFound() throws Exception {

        ClientResponseDto clientResponseDto = clientResponseDtoValid();

        when(clientService.findById(clientResponseDto.getId())).thenReturn(clientResponseDto);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI + "/{clientId}", clientResponseDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(clientResponseDto.getId()));

    }

    @Test
    void shouldThrowBusinessException_whenClientIdNotFound() throws Exception {

        when(clientService.findById(INVALID_CLIENT_ID))
                .thenThrow(DataForBusinessException.CLIENT_NOT_FOUND.asBusinessExceptionWithDescriptionFormatted(Long.toString(INVALID_CLIENT_ID)));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URI + "/{clientId}", INVALID_CLIENT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(DataForBusinessException.CLIENT_NOT_FOUND.getMessage()));

    }

    @Test
    void shouldSaveClientAndReturnClientResponse_whenClientRequestDtoValidWasPassed() throws Exception {

        ClientResponseDto clientResponseDto = clientResponseDtoValid();

        ClientRequestDto clientRequestDto = clientRequestDtoValid();

        when(clientService.create(any(ClientRequestDto.class))).thenReturn(clientResponseDto);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(clientResponseDto.getId()));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameNullWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome não pode ser nulo]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameWithSizeInvalidWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(INVALID_SIZE_NAME)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome não deve ter menos que 3 ou mais que 100 caracteres]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameWithFourEqualsCharactersInSequenceWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(INVALID_NAME_WITH_FOUR_EQUALS_CHARACTERS_IN_SEQUENCE)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome='" + INVALID_NAME_WITH_FOUR_EQUALS_CHARACTERS_IN_SEQUENCE + "' não pode conter caracteres especiais ou números, ou conter 4 caracteres iguais em sequencia]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameWithSpecialCharactersWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(INVALID_NAME_WITH_SPECIAL_CHARACTERS)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome='" + INVALID_NAME_WITH_SPECIAL_CHARACTERS + "' não pode conter caracteres especiais ou números, ou conter 4 caracteres iguais em sequencia]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithEmailNullWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .name(VALID_NAME)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[email:O email não pode ser nulo]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithEmailInvalidWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(INVALID_EMAIL)
                .name(VALID_NAME)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[email:O email='" + INVALID_EMAIL + "' é inválido]"));
    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithTelephoneNullWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .name(VALID_NAME)
                .email(VALID_EMAIL)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[telephone:O telefone não pode ser nulo]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithTelephoneInvalidWasPassedAndCreateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .telephone(INVALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[telephone:O telefone='" + INVALID_TELEPHONE + "' é inválido]"));
    }

    @Test
    void shouldUpdateClientAndReturnClientResponse_whenClientRequestDtoValidWasPassed() throws Exception {

        ClientResponseDto clientResponseDto = clientResponseDtoValid();

        ClientRequestDto clientRequestDto = clientRequestDtoValid();

        when(clientService.update(anyLong(), any(ClientRequestDto.class))).thenReturn(clientResponseDto);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", clientResponseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(clientResponseDto.getId()));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoValidAndClientIdNotFoundWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = clientRequestDtoValid();

        when(clientService.update(anyLong(), any(ClientRequestDto.class)))
                .thenThrow(DataForBusinessException.CLIENT_NOT_FOUND.asBusinessExceptionWithDescriptionFormatted(Long.toString(INVALID_CLIENT_ID)));

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", INVALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.CLIENT_NOT_FOUND.getMessage()));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameNullWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome não pode ser nulo]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameWithSizeInvalidWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(INVALID_SIZE_NAME)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome não deve ter menos que 3 ou mais que 100 caracteres]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameWithFourEqualsCharactersInSequenceWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(INVALID_NAME_WITH_FOUR_EQUALS_CHARACTERS_IN_SEQUENCE)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome='" + INVALID_NAME_WITH_FOUR_EQUALS_CHARACTERS_IN_SEQUENCE + "' não pode conter caracteres especiais ou números, ou conter 4 caracteres iguais em sequencia]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithNameWithSpecialCharactersWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(INVALID_NAME_WITH_SPECIAL_CHARACTERS)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[name:O nome='" + INVALID_NAME_WITH_SPECIAL_CHARACTERS + "' não pode conter caracteres especiais ou números, ou conter 4 caracteres iguais em sequencia]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithEmailNullWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .name(VALID_NAME)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[email:O email não pode ser nulo]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithEmailInvalidWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(INVALID_EMAIL)
                .name(VALID_NAME)
                .telephone(VALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[email:O email='" + INVALID_EMAIL + "' é inválido]"));
    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithTelephoneNullWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .name(VALID_NAME)
                .email(VALID_EMAIL)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[telephone:O telefone não pode ser nulo]"));

    }

    @Test
    void shouldThrowBusinessException_whenClientRequestDtoWithTelephoneInvalidWasPassedAndUpdateWasCalled() throws Exception {

        ClientRequestDto clientRequestDto = ClientRequestDto.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .telephone(INVALID_TELEPHONE)
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clientRequestDto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.INVALID_INPUT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("[telephone:O telefone='" + INVALID_TELEPHONE + "' é inválido]"));
    }

    @Test
    void shouldDeleteClient_whenClientIdWasFound() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URI + "/{clientId}", VALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNoContent());

    }

    @Test
    void shouldThrowBusinessException_whenClientIdNotFoundWasPassedAndDeleteWasCalled() throws Exception {

        doThrow(DataForBusinessException.CLIENT_NOT_FOUND.asBusinessExceptionWithDescriptionFormatted(Long.toString(INVALID_CLIENT_ID))).when(clientService).delete(anyLong());

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URI + "/{clientId}", INVALID_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DataForBusinessException.CLIENT_NOT_FOUND.getMessage()));

    }

}