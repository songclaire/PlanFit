package com.project.PlanFit.file.util;

// 파일창고 소장님
// FileProps에게 규칙을 받아서 사용함
// 프론트가 파일을 던지면, 이 소장님은
// 1. 확장자, 용량을 검사하고
// 2. 날짜별 폴더를 만들고
// 3. 파일이름을 UUID.ext로 갈아끼워 저장하고
// 4. 나중에 다운로드 할 때 안전하게 꺼내줌

import com.project.PlanFit.file.config.FileProps;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageUtil {

    private final FileProps props;

    /** 저장 결과(어디에, 어떤 이름으로, 어떤 확장자, 사이즈 얼마) */
    public record Saved(String dir, String storedName, String ext, long size) {}

    /** MultipartFile 바로 저장 (가장 자주 쓰이는 입구) */
    public Saved save(MultipartFile mf) throws IOException {
        String original = Objects.requireNonNull(mf.getOriginalFilename(), "filename required");
        try (InputStream in = mf.getInputStream()) {
            return save(original, in, mf.getSize());
        }
    }

    /** 파일명/스트림/사이즈로 저장 (테스트나 특수 케이스에 유용) */
    public Saved save(String originalFilename, InputStream in, long size) throws IOException {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("filename required");
        }

        // 1) 검증
        String ext = extOf(originalFilename);
        validate(ext, size);

        // 2) 경로 생성 : yyyy/mm/dd
        // 오늘 날짜 기준으로 2025/08/20 같은 하위 경로를 만든 뒤, 실제 폴더 생성
        // 포인트: normalize()로 경로를 정규화해서 .. 같은 이상치를 미리 제거
        String dir = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now());
        Path saveDir = Path.of(props.getBaseDir(), dir).normalize();
        Files.createDirectories(saveDir);

        // 3) 저장 파일명 : UUID + 확장자
        // 원본명은 DB 메타로만 보관하고, 실제 저장은 UUID.ext로
        //  -> 이름 충돌 방지, 한글/공백 인코딩 이슈 방지, 브라우저 다운로드 버그 예방
        // 다시 normalize()—경로는 의심하고 또 의심하는 게 보안의 기본자세
        String stored = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
        Path target = saveDir.resolve(stored).normalize();

        // 4) 역참조 방지 : baseDir 밖으로 못 나가게
        // 최종 타깃 경로가 baseDir 바깥으로 벗어나면 즉시 차단
        // ../../etc/passwd 같은 디렉터리 트래버설 공격을 막는 필수 안전장치
        Path base = Path.of(props.getBaseDir()).toAbsolutePath().normalize();
        if (!target.toAbsolutePath().startsWith(base)) {
            throw new SecurityException("Invalid storage path traversal");
        }

        // 5) 실제 복사 (있으면 교체)
        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

        return new Saved(dir, stored, ext, size);
    }

    /** 다운로드용 리소스 꺼내기 */
    // 저장된 파일을 읽기 전용 Resource로 꺼내줌
    // 컨트롤러에서 ResponseEntity<Resource>로 바로 내려보내기 쉬움. 스트림을 직접 열고 닫는 번거로움이 없음.
    // 경로는 또 normalize(). 보안은 과해도 모자라지 않다.
    public Resource asResource(String dir, String storedName) throws MalformedURLException {
        Path p = Path.of(props.getBaseDir(), dir, storedName).normalize();
        return new UrlResource(p.toUri());
    }






    /* ================= 내부 헬퍼들 ================= */
    /** 확장자 추출(소문자). 점 없으면 "" */
    private String extOf(String name) {
        int idx = name.lastIndexOf('.');
        return (idx > -1 ? name.substring(idx + 1) : "").toLowerCase(Locale.ROOT);
    }

    /** 서버 측 2차 검문(확장자/용량) */
    private void validate(String ext, long size) {
        // 확장자
        List<String> allow = props.getAllowedExt();
        if (allow != null && !allow.isEmpty()) {
            // 빈 확장자를 허용하고 싶다면 allowed-ext에 ""를 넣어두면 된다.
            if (!allow.contains(ext)) {
                throw new IllegalArgumentException("EXT_NOT_ALLOWED: " + ext);
            }
        }
        // 용량
        long max = props.getMaxSize();
        if (max > 0 && size > max) {
            throw new IllegalArgumentException("FILE_TOO_LARGE: " + humanSize(size) + " > " + humanSize(max));
        }
    }

    private String humanSize(long bytes) {
        return DataSize.ofBytes(bytes).toString();
    }
}
