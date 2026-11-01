package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA raíz para persistencia relacional de usuarios en MySQL.
 * Utiliza estrategia de herencia JOINED para especializar Compradores y Vendedores.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "identity_document", length = 50, unique = true)
    private String identityDocument;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "role", length = 40)
    private String role;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "auth_token_version", nullable = false)
    private Integer authTokenVersion = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
