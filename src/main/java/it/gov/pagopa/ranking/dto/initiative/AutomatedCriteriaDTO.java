package it.gov.pagopa.ranking.dto.initiative;

import lombok.*;
import org.springframework.data.domain.Sort;

/**
 * AutomatedCriteriaDTO
 */
@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class AutomatedCriteriaDTO   {
    private String authority;
    private String code;
    private String field;
    private Sort.Direction orderDirection;
}
