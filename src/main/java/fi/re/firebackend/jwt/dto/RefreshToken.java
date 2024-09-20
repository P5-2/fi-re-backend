package fi.re.firebackend.jwt.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id; // 필요한 경우 수동으로 관리
    private String refreshToken;
    private String keyUserId;

    // 필요하다면 추가적인 메서드나 로직을 여기에 작성
}
