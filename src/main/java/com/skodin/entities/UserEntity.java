package com.skodin.entities;

import com.skodin.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String email;

    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    @Column(name = "activation_code")
    String activationCode;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<TaskEntity> authoredTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.REMOVE)
    private List<TaskEntity> assignedTasks = new ArrayList<>();

    public UserEntity(Long id, String email, String password, Role role, String activationCode) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.activationCode = activationCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return activationCode.equals("enable");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
