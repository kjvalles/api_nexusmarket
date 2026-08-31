package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.valueobjects.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class User {

    private String id;
    private String identityDocument;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
