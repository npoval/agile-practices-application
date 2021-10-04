package com.acme.dbo.retrofit;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RetrofitTestClient {
    ClientService service;

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
    @DisplayName("Проверка возможности получения всех клиентов через GET")
    public void shouldGetClients() throws IOException {
        service.getClients().execute().body().forEach(System.out::println);
    }


    @Test
    @DisplayName("Проверка создания клиента через POST")
    public void shouldPostClient() throws IOException {
        String loginClient = service.createClient(new Client("a8ada3d@ytr.ru", "same-salt", "2131231ffd43400000", true))
                .execute().body().getLogin();
        assertEquals("a8ada3d@ytr.ru", loginClient);
    }
}
