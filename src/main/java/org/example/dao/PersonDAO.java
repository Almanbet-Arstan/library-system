package org.example.dao;

import org.example.models.Book;
import org.example.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> getAllPeople() {
        return jdbcTemplate.query("select * from person", new BeanPropertyRowMapper<>(Person.class));
    }

    public Person getPersonById(int id){
        return jdbcTemplate.query("select * from person where id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("insert into person(full_name, year_of_birth) values (?, ?)", person.getFullName(),
                person.getYearOfBirth());
    }

    public void update(int id, Person updatedPerson){
        jdbcTemplate.update("update person set full_name = ?, year_of_birth = ? where id = ?", updatedPerson.getFullName(),
                updatedPerson.getYearOfBirth(), id);
    }

    public void delete (int id) {
        jdbcTemplate.update("delete from person where id = ?", id);
    }

    // для валидации уникальности ФИО
    public Optional<Person> getPersonByFullName(String fullName){
        return jdbcTemplate.query("select * from person where full_name = ?", new Object[]{fullName},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }

    // здесь join не нужен. И так уже получили человека с помощью отдельного метода
    public List<Book> getBooksByPersonId(int id){
        return jdbcTemplate.query("select * from book where person_id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Book.class));
    }
}
