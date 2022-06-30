package pet.kodmark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Key implements Serializable {

    @Column(name = "user_login", insertable = false, updatable = false)
    private String userLogin;

    @Column(name = "role_id", insertable = false, updatable = false)
    private int roleId;
}
