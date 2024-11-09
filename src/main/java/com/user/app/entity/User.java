package com.user.app.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false, name = "name")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, name = "email")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,12}$", message = "Mobile number must be valid with 10-12 digits and optional '+' prefix")
    @NotBlank(message = "Mobile is mandatory")
//    @Size(min = 10, max = 12, message = "Mobile number must be 10 to 12 numeric characters")
    @Column(nullable = false, name = "mobile")
    private String mobile;

    @NotBlank(message = "Address is mandatory")
    @Column(nullable = false, name = "address")
    private String address;

}
