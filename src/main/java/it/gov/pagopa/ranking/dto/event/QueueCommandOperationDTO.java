package it.gov.pagopa.ranking.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QueueCommandOperationDTO {

    String operationType;
    String operationId;
    LocalDateTime operationTime;

}
