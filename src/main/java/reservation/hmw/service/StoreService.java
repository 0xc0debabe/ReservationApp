package reservation.hmw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.Partner;
import reservation.hmw.model.entity.Store;
import reservation.hmw.model.entity.dto.StoreDetailDto;
import reservation.hmw.model.entity.dto.StoreInfoDto;
import reservation.hmw.model.entity.dto.StoreRegisterForm;
import reservation.hmw.repository.PartnerRepository;
import reservation.hmw.repository.StoreRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 매장을 위한 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final PartnerRepository partnerRepository;
    private final StoreRepository storeRepository;

    /**
     * 매장을 등록하는 메서드입니다.
     *
     * @param form 매장 등록 정보가 포함된 요청 폼
     * @param partnerId 등록할 매장의 파트너 ID
     * @return 등록된 매장 정보
     */
    @Transactional
    public Store registerStore(StoreRegisterForm form, Long partnerId) {
        Partner findPartner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTNER_ACCESS_ONLY));

        Store store = Store.builder()
                .storeName(form.getStoreName())
                .location(form.getLocation())
                .storeDescription(form.getStoreDescription())
                .keyword(form.getKeyword())
                .partner(findPartner)
                .build();

        findPartner.getStoreList().add(store);

        return storeRepository.save(store);
    }

    /**
     * 키워드로 매장을 검색하는 메서드입니다.
     *
     * @param keyword 검색할 키워드
     * @return 검색된 매장 정보 리스트
     */
    public List<StoreInfoDto> searchStoreByKeyword(String keyword) {
        List<Store> stores = storeRepository.findAllByKeyword(keyword)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));

        return stores.stream()
                .map(StoreInfoDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 매장 이름으로 매장을 검색하는 메서드입니다.
     *
     * @param storeName 검색할 매장 이름
     * @return 검색된 매장 정보
     */
    public StoreInfoDto searchStoreByName(String storeName) {
        Store store = storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));

        return StoreInfoDto.fromEntity(store);
    }

    /**
     * 주어진 매장 ID에 대한 상세 정보를 조회하는 메서드입니다.
     *
     * @param storeId 조회할 매장 ID입니다.
     * @return StoreDetailDto 매장 상세 정보를 포함한 DTO를 반환합니다.
     * @throws CustomException 매장을 찾을 수 없는 경우 예외를 발생시킵니다.
     */
    public StoreDetailDto detailStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));


        return new StoreDetailDto(store.getStoreDescription(), store.getReviewList());
    }

    @Transactional
    public Store updateStore(Long storeId, StoreRegisterForm form, Long partnerId) {
        Store findStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));

        if (!Objects.equals(findStore.getPartner().getId(), partnerId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        findStore.setStoreName(form.getStoreName());
        findStore.setStoreDescription(form.getStoreDescription());
        findStore.setKeyword(form.getKeyword());
        findStore.setLocation(form.getLocation());

        return storeRepository.save(findStore);
    }

    @Transactional
    public void deleteStore(Long storeId, Long partnerId) {
        Store findStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STORE));

        if (!Objects.equals(findStore.getPartner().getId(), partnerId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        storeRepository.delete(findStore);
    }

}
