package reservation.hmw.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservation.hmw.model.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByStoreId(Long storeId, Pageable pageable);

}
