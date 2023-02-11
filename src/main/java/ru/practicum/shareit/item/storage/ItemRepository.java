package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    Page<Item> findByOwner_Id(long id, Pageable pageable);

    @Query("select i from Item i where i.available = true " +
            "and (lower(i.name) like lower(concat('%', :text ,'%')) " +
            "or lower(i.description) like lower(concat('%', :text ,'%')))")
    Page<Item> search(@Param("text") String text, Pageable pageable);
}
