package com.acme.dbo.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import org.junit.jupiter.api.*;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.hasProperty;
import static org.testcontainers.shaded.org.hamcrest.Matchers.is;


public class JpaClientTest {
    ClientService service;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeEach
    public void setConnect() {
        entityManagerFactory = Persistence.createEntityManagerFactory("dbo");
    }

    @AfterEach
    public void closeConnect() {
        entityManagerFactory.close();
    }

    @BeforeEach
    public void init() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://localhost:8080/dbo/api/")
                .client(httpClient.build())
                .build();
        service = retrofit.create(ClientService.class);
    }

    @Test
    @DisplayName("Проверка возможности получения клиента по Id")
    public void shouldGetClientById() throws IOException {
        final EntityManager em = entityManagerFactory.createEntityManager();
        final String newLogin = "login" + new Random().nextInt();
        final Client client = new Client(newLogin, "secret", "salt", true);
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();

        assertEquals(newLogin, service.getClient(client.getId()).execute().body().getLogin());

        em.getTransaction().begin();
        final Client clientSaved = em.find(Client.class, client.getId());
        em.remove(clientSaved);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("Проверка создания клиента через POST")
    public void shouldPostClient() throws IOException {
        String loginClient = service.createClient(new Client("11a8ada3d@ytr.ru", "same-salt", "12131231ffd43400000", true))
                .execute().body().getLogin();
        assertEquals("a8ada3d@ytr.ru", loginClient);
        final EntityManager em = entityManagerFactory.createEntityManager();
        final Client client = em.createQuery("Select c FROM Client c WHERE c.login=:login", Client.class)
                .setParameter("login", "11a8ada3d@ytr.ru")
                .getSingleResult();

        assertThat(client, hasProperty("secret", is("12131231ffd43400000")));

        em.getTransaction().begin();
        final Client createClient = em.find(Client.class, client.getId());
        em.remove(createClient);
        em.getTransaction().commit();
        em.close();
    }
}
