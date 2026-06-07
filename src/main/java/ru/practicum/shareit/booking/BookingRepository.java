package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBooker(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(Long ownerId, LocalDateTime now);

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

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus bookingStatus);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :currentDateTime " +
            "AND b.status = :status " +
            "ORDER BY b.end DESC")
    List<Booking> findByItemOwnerIdAndEndIsBeforeAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :currentDateTime " +
            "AND b.status = :status " +
            "ORDER BY b.start ASC")
    List<Booking> findByItemOwnerIdAndStartIsAfterAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("status") BookingStatus status);
}
