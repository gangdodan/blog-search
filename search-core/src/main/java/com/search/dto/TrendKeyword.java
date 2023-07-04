package com.search.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Getter
@RequiredArgsConstructor(staticName = "of")
public class TrendKeyword {
    private final String keyword;
    private final Long count;
}
