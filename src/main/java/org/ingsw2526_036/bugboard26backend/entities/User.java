package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USERTYPE",  discriminatorType = DiscriminatorType.STRING)
@Table(name = "users") 
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;
    @Column(unique = true,  nullable = false)
    @NonNull
    private String username;
    @Column(nullable = false)
    @NonNull
    private String password;
    @Column(unique = true, nullable = false)
    @NonNull
    private String email;
    @OneToMany(mappedBy = "creator")
    @ToString.Exclude // Evita loop infiniti
    private List<Issue> issuesCreated;
    @OneToMany(mappedBy = "assignedTo")
    @ToString.Exclude // Evita loop infiniti
    private List<Issue> issuesAssigned;
    @ManyToMany
    @JoinTable(
            name = "User_Project"
            , joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    @ToString.Exclude // Evita loop infiniti
    private List<Project> joinedProjects;
    @OneToMany(mappedBy = "creator")
    @ToString.Exclude // Evita loop infiniti
    private List<Comment> comments;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        RoleEnum role = (this instanceof Administrator) ? RoleEnum.ADMIN : RoleEnum.BASEUSER;
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public @NonNull String getUsername(){
        return this.email;
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
        return true;
    }
}
