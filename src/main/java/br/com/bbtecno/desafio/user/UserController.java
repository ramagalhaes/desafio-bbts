package br.com.bbtecno.desafio.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class UserController {

    @GetMapping("oauth2/client")
    public Object getAuthorizedClient(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient;
    }

    @GetMapping("oauth2/user")
    public Object getUser(@AuthenticationPrincipal OAuth2User user) {
        return user;
    }

    @GetMapping("/login/oauth2/code/github")
    public String teste() {
        return "teste";
    }
}
