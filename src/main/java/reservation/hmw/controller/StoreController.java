package reservation.hmw.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reservation.hmw.Validation;
import reservation.hmw.exception.CustomException;
import reservation.hmw.exception.ErrorCode;
import reservation.hmw.model.entity.dto.StoreDetailDto;
import reservation.hmw.model.entity.dto.StoreRegisterForm;
import reservation.hmw.model.entity.session.SessionConst;
import reservation.hmw.service.StoreService;

/**
 * 매장 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("store")
public class StoreController {

    private final StoreService storeService;

    /**
     * 매장 등록을 처리하는 메서드입니다.
     *
     * @param form 매장 등록에 필요한 정보가 포함된 폼입니다.
     * @param bindingResult 유효성 검사 결과입니다.
     * @param request 세션 확인을 위한 요청 파라미터입니다.
     * @return 등록된 매장 정보를 포함한 HTTP 200 OK 응답을 반환합니다.
     * @throws CustomException 파트너가 아닌 경우 접근을 제한하는 예외입니다.
     */
    @PostMapping("register")
    public ResponseEntity<?> registerStore(@Valid @RequestBody StoreRegisterForm form,
                                           BindingResult bindingResult,
                                           HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return Validation.getErrorResponse(bindingResult);
        }

        Long partnerId = (Long) request.getSession(false).getAttribute(SessionConst.LOGIN_PARTNER);
        if (partnerId == null) {
            throw new CustomException(ErrorCode.PARTNER_ONLY_ACCESS);
        }

        return ResponseEntity.ok(storeService.registerStore(form, partnerId));
    }

    /**
     * 매장을 이름이나 키워드로 검색하는 메서드입니다.
     *
     * @param storeName 검색할 매장 이름입니다.
     * @param keyword 검색할 키워드입니다.
     * @return 검색된 매장 정보를 포함하고 HTTP 200 OK 응답 또는 잘못된 요청에 대한 에러 응답을 반환합니다.
     */
    @GetMapping("search")
    public ResponseEntity<?> searchStoreByKeyword(@RequestParam(name = "storeName", required = false) String storeName,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {

        if (storeName != null && !storeName.isEmpty()) {
            return ResponseEntity.ok(storeService.searchStoreByName(storeName));
        } else if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(storeService.searchStoreByKeyword(keyword));
        } else {
            return ResponseEntity.badRequest().body(ErrorCode.MISSING_STORENAME_OR_KEYWORD);
        }

    }

    /**
     * 주어진 매장 ID에 대한 상세 정보를 조회하는 메서드입니다.
     *
     * @param storeId 조회할 매장 ID입니다.
     * @return 매장 상세 정보를 포함한 DTO를 반환하며, HTTP 200 OK 응답을 반환합니다.
     * @throws CustomException 매장을 찾을 수 없는 경우 예외를 발생시킵니다.
     */
    @GetMapping("detail/{storeId}")
    public ResponseEntity<?> detailStore(@PathVariable(name = "storeId") Long storeId) {
        StoreDetailDto detailDto = storeService.detailStore(storeId);
        return ResponseEntity.ok(detailDto);
    }

    /**
     * 테스트 용도로 사용되는 메서드입니다.
     *
     * @return 테스트 결과를 포함한 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("somePoint") // Test용도
    public ResponseEntity<?> somePoint() {
        return ResponseEntity.ok("asfd");
    }

}
