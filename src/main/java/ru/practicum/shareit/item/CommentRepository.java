package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);

    @Query("SELECT MAX(c.created) FROM Comment c WHERE c.item.id = :itemId")
    Optional<LocalDateTime> findLastCommentDateByItemId(@Param("itemId") Long itemId);
}
