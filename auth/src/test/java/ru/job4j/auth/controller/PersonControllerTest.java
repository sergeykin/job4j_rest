package ru.job4j.auth.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository persons;

    @Test
    public void findAll() throws Exception {
        this.mockMvc.perform(get("/person/"))
                .andExpect(status().isOk());
    }

    @Test
    public void findById() throws Exception {
        Person person = new Person();
        person.setId(1);
        person.setLogin("username");
        person.setPassword("password");
        when(persons.findById(1)).thenReturn(Optional.of(person));
        this.mockMvc.perform(get("/person/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void create() throws Exception {
        Person person = new Person();
        person.setId(1);
        person.setLogin("username");
        person.setPassword("password");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(person);
        this.mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("username"));
    }

    @Test
    public void update() throws Exception {
        Person person = new Person();
        person.setId(1);
        person.setLogin("username");
        person.setPassword("password");
        Person updperson = new Person();
        updperson.setId(1);
        updperson.setLogin("updperson");
        updperson.setPassword("123456");
        when(persons.findById(1)).thenReturn(Optional.of(person));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(updperson);
        this.mockMvc.perform(put("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("updperson"));
    }

    @Test
    public void deleteId() throws Exception {
        Person person = new Person();
        person.setId(1);
        person.setLogin("username");
        person.setPassword("password");
        when(persons.findById(1)).thenReturn(Optional.of(person));
        this.mockMvc.perform(delete("/person/1"))
                .andExpect(status().isOk());
    }
}