package Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class User implements DBEntity {

    @Id
    @Column(name = "user_name")
    @NonNull
    @JsonProperty("userName")
    private String userName;

    @NonNull
    @JsonProperty("password")
    private String password;

    @JsonProperty("eMail")
    @Column(name = "e_mail")
    private String eMail;

    @ElementCollection
    @CollectionTable(name = "user_computers", joinColumns = {@JoinColumn(name = "user_name")})
    private List<Computer> computer;

}
