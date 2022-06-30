package pet.kodmark.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Entity
@Table(name = "user_roles")
public class UserRoles {

    @EmbeddedId
    private Key id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name="user_login", insertable = false, updatable = false, nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name="role_id", insertable = false, updatable = false, nullable = false)
    private Role role;

}
