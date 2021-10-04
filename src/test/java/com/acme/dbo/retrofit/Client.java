package com.acme.dbo.retrofit;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;

//@JsonInclude(JsonInclude.Include.NON_NULL)

@Entity
//@Generated("jsonschema2pojo")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    @JsonPropertyDescription("Client id")
    private Integer id;

    @JsonProperty("login")
    @JsonPropertyDescription("Client login")
    private String login;

    @JsonProperty("salt")
    @JsonPropertyDescription("Client salt")
    private String salt;

    @JsonProperty("secret")
    @JsonPropertyDescription("Client secret")
    private String secret;

    @CreationTimestamp
    @JsonProperty("created")
    @Column(name = "created")
    @JsonPropertyDescription("Client created")
    private Date created;


    @JsonProperty("enabled")
    @JsonPropertyDescription("Client enabled")
    private boolean enabled;

   public Client() {
   }

    public Client(@JsonProperty("login") String login, @JsonProperty("salt") String salt, @JsonProperty("secret")String secret, @JsonProperty("enabled") boolean enabled) {
        this.login = login;
        this.salt = salt;
        this.secret = secret;
    }

    @JsonProperty("login")
    public String getLogin() {
        return login;
    }

    @JsonProperty("login")
    public void setLogin(String login) {
        this.login = login;
    }

    @JsonProperty("salt")
    public String getSalt() {
        return salt;
    }

    @JsonProperty("salt")
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @JsonProperty("secret")
    public String getSecret() {
        return secret;
    }

    @JsonProperty("secret")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "Client{" +
                ", id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", salt='" + salt + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}