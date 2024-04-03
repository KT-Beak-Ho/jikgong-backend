package jikgong.domain.notification.repository;

import jikgong.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * find by id and member
     */
    @Query("select n from Notification n where n.receiver.id = :memberId and n.id = :notificationId")
    Optional<Notification> findByIdAndMember(@Param("memberId") Long memberId, @Param("notificationId") Long notificationId);


    @Query("select n from Notification n where n.receiver.id = :memberId")
    List<Notification> findByMember(@Param("memberId") Long memberId);

    @Query("select count(n) from Notification n where n.receiver.id = :memberId and n.isRead = false")
    int countUnreadNotification(@Param("memberId") Long memberId);
}
