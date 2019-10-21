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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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

    @Value("${google.api.key}")
    private String API_KEY;
    @Value("${google.api.secret}")
    private String API_SECRET;
//    @Value("${google.user.info.scope.profile}")
    //    private final String SCOPE_PROFILE;
    @Value("${google.user.info.scope.email}")
    private String SCOPE_EMAIL;
    @Value("${google.callback}")
    private String CALLBACK;
    @Value("${google.user.info.link}")
    private String USER_INFO;

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
