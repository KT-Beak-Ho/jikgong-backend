package jikgong.domain.searchlog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SearchLogDeleteRequest {
    private String name;
    private String createdAt;
}
