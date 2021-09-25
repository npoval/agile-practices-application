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
import java.time.LocalDateTime;
import java.util.Random;
import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JpaClientTest {
    ClientService service;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeEach
    public void setConnect(){
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

    @Disabled
    @Test
    @DisplayName("Проверка возможности получения всех клиентов через GET")
    public void shouldGetClientById() throws IOException {
        final EntityManager em = entityManagerFactory.createEntityManager();
        final String newLogin = "login" + new Random().nextInt();
        final Client client = new Client(newLogin, "secret","salt", LocalDateTime.now(), true);
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

    @Disabled
    @Test
    @DisplayName("Проверка создания клиента через POST")
    public void shouldPostClient() throws IOException {
        //String loginClient = service.createClient(new Client("a8ada3d@ytr.ru", "same-salt", "2131231ffd43400000"))
                //.execute().body().getLogin();
        //assertEquals("a8ada3d@ytr.ru", loginClient);
    }
}
