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
public class BookDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getAllBooks() {
        return jdbcTemplate.query("select * from Book", new BeanPropertyRowMapper<>(Book.class));
    }

    public Book getBookById(int id){
        return jdbcTemplate.query("select * from Book where id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Book.class)).stream().findAny().orElse(null);
    }

    public void save(Book book) {
        jdbcTemplate.update("insert into book (title, author, year) values (?, ?, ?)", book.getTitle(),
                book.getAuthor(), book.getYear());
    }

    public void update(int id, Book updatedBook) {
        jdbcTemplate.update("update book set title = ?, author = ?, year = ? where id = ?", updatedBook.getTitle(),
                updatedBook.getAuthor(), updatedBook.getYear(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("delete from book where id = ?", id);
    }
    // join'им таблицы book и person и получаем человека, которому принадлежит книга с указанным id
    public Optional<Person> getBookOwner(int id) {
        // выбираем все колонки таблицы person из объединенной таблицы
        return jdbcTemplate.query("select Person.* from book join person on Book.person_id = Person.id where Book.id = ?",
                new Object[]{id}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }

    //Освобождает книгу (этот метод вызывается, когда человек возвращает книгу в библиотеку)
    public void release(int id) {
        jdbcTemplate.update("update book set person_id = null where id = ?", id);
    }

    // Назначает книгу человеку (этот метод вызывается, когда человек забирает кингу из библиотеки)
    public void assign(int id, Person selectedPerson){
        jdbcTemplate.update("update book set person_id = ? where id = ?", selectedPerson.getId(), id);
    }
}
