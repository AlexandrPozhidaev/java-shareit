package ru.practicum.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.BookingStatus;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"booker", "item", "item.owner"})
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"booker", "item", "item.owner"})
    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :currentDateTime " +
            "AND b.status = :status")
    List<Booking> findByBookerIdAndItemIdAndEndIsBeforeAndStatus(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("status") BookingStatus status);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long userId, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :currentDateTime " +
            "AND b.status = :status " +
            "ORDER BY b.start ASC")
    List<Booking> findByItemOwnerIdAndStartIsAfterAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.end < :currentDateTime " +
            "AND b.status IN :statuses " +
            "ORDER BY b.end DESC")
    List<Booking> findByItemIdAndEndBeforeAndStatusIn(
            @Param("itemId") Long itemId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("statuses") List<BookingStatus> statuses);

    @EntityGraph(attributePaths = {"booker", "item"})
    List<Booking> findByItemIdOrderByStartDesc(@Param("itemId") Long itemId);

    List<Booking> findByItemIdAndStartIsBeforeAndEndIsAfter(Long itemId, LocalDateTime end, LocalDateTime start);

    @EntityGraph(attributePaths = {"booker", "item", "item.owner"})
    Optional<Booking> findById(Long id);
}
