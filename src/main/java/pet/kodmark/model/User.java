package pet.kodmark.model;

import lombok.*;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @ToString.Include
    private String name;

    @Id
    @ToString.Include
    @Column(nullable = false)
    private String login;

    @ToString.Include
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<UserRoles> roles;

    public User(String name, String login, String password){
        this.name = name;
        this.login = login;
        this.password = password;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return login != null && Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
