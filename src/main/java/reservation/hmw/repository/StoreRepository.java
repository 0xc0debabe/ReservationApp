package reservation.hmw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import reservation.hmw.model.entity.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {


    Optional<List<Store>> findAllByKeyword(String keyword);


    Optional<Store> findByStoreName(String storeName);

}
