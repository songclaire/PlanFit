package com.project.PlanFit.food.dto;

import com.project.PlanFit.cmmn.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantDto extends BaseDto {
    private Long restaurantSn;   // 맛집일련번호
    private Long foodTypeSn;     // 음식유형일련번호
    private Long rgnProvcSn;     // 지역도일련번호

    private String addr;         // 주소
    private String rtrtNm;       // 맛집명
    private String zipCode;      // 우편번호
    private String addrRoad;     // 도로명주소
    private String addrDetail;   // 상세주소
    private Long fileId;         // 파일아이디

    private Double latitude;     // 위도 (LATITUDE)
    private Double longitude;    // 경도 (LONGITUDE)
    private String kakaoPlaceId; // 카카오 장소 식별자 (KAKAO_PLACE_ID)

}
