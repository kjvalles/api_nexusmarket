package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDto {
    private String id;
    private String actorId;
    private String actorRole;
    private String action;
    private String entityName;
    private String entityId;
    private String details;
    private LocalDateTime timestamp;
}
