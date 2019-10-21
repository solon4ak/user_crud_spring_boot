package ru.solon4ak.service;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.solon4ak.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class ScribeJavaGoogleService {

    private static Logger logger = LoggerFactory.getLogger(ScribeJavaGoogleService.class);

    private static final String API_KEY = "180388245536-t784b3o4ovdfjs3d7gr6r62082s6h06h.apps.googleusercontent.com";
    private static final String API_SECRET = "y5g6lkq2rU2RuVehqv3opzU7";
    //    private static final String SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
    private static final String CALLBACK = "http://localhost:8080/google/callback";
    private static final String USER_INFO = "https://www.googleapis.com/oauth2/v2/userinfo?alt=json";

    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public OAuth20Service getOAuth20Service() {
        return new ServiceBuilder(API_KEY)
                .apiSecret(API_SECRET)
//                .scope(SCOPE_PROFILE)
                .scope(SCOPE_EMAIL)
                .callback(CALLBACK)
                .build(GoogleApi20.instance());
    }

    public Response getOAuthResponse(String code) throws InterruptedException, ExecutionException, IOException {
        OAuth20Service service = this.getOAuth20Service();
        OAuth2AccessToken accessToken = this.getOAuth20Service().getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, USER_INFO);
        service.signRequest(accessToken, request);
        return service.execute(request);
    }

    public String getEmailFromOAuth2Response(Response response) throws IOException {
        JsonObject jsonObject = (new JsonParser()).parse(response.getBody()).getAsJsonObject();
        return jsonObject.get("email").getAsString();
    }

    private List<GrantedAuthority> getUserAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoles().contains(roleService.getAllRoles().get(0))) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }

    private UserDetails getUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                this.getUserAuthorities(user)
        );
    }

    public boolean userHasAdminRole(User user) {
        return user.getRoles().contains(roleService.getAllRoles().get(0));
    }

    public SecurityContext addUserToSecurityContext(User user) {
        UserDetails userDetails = this.getUserDetails(user);
        List<GrantedAuthority> authorities = this.getUserAuthorities(user);
        Authentication authReq
                = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authReq);
        return sc;
    }
}
