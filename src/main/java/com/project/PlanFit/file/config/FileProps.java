package com.project.PlanFit.file.config;

// application.yml의 file:* 설정을 타입 안전하게 데려오는 “환경설정 집사”
// 스토리지 로직(파일 저장/다운로드)이 이 집사를 불러 “기본 디렉터리 어디야? 허용 확장자는 뭐야? 최대 크기는?” 물어보고 착착 일함
// 집 주소와 규칙
// 파일 저장 경로/용량/확장자를 프로젝트 전체에서 한 곳에서 관리하기 위함

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "file")   // file:* 아래 값을 이 클래스에 바인딩
@Validated
@Getter @Setter
public class FileProps {

    /** 파일 저장 루트 디렉토리 (ex. /data/uploads) */
    @NotBlank(message = "file.base-dir 설정은 필수입니다.")
    private String baseDir = "/data/uploads";

    /** 허용되는 확장자 리스트 */
    @NotEmpty(message = "file.allowed-ext는 최소 1개 이상이어야 합니다.")
    private List<String> allowedExt = List.of(
            "jpg","jpeg","png","gif","webp","pdf","ppt","pptx","doc","docx","xls","xlsx","hwp","hwpx","zip"
    );

    /** 최대 파일 크기. 0이면 제한 없음으로 해석 가능 */
    @Min(value = 0, message = "file.max-size는 0 이상이어야 합니다.")
    private long maxSize = 10 * 1024 * 1024L;

}
