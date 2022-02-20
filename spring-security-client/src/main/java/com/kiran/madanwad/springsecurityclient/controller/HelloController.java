package com.kiran.madanwad.springsecurityclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;

@RestController
public class HelloController {

    @Autowired
    private WebClient webClient;

    @GetMapping("/api/hello")
    public String hello(Principal principal) {
        return "Hello " + principal.getName() + "! Welcome to Kiran's client";
    }

    @GetMapping("/api/users")
    public String[] users(@RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
                                  OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        return this.webClient.get().uri("http://127.0.0.1:8090/api/users")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(oAuth2AuthorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }
}