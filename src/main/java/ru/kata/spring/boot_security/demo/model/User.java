package ru.kata.spring.boot_security.demo.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 3, max = 30, message = "Minimum 3 characters")
    @NotBlank
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Size(min = 3, max = 30, message = "Minimum 3 characters")
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    @Size(min = 3, max = 30, message = "Minimum 3 characters")
    @NotBlank
    @Column(name = "surname", nullable = false)
    private String surname;

    @Max(value = 180)
    @Min(value = 1, message = "Age begins at 1 year")
    @Column(name = "age", nullable = false)
    private int age;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return username;
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

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}
