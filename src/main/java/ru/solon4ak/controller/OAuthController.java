package ru.solon4ak.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.RoleService;
import ru.solon4ak.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class OAuthController {

    private static Logger logger = LoggerFactory.getLogger(OAuthController.class);

    private static final String API_KEY = "180388245536-t784b3o4ovdfjs3d7gr6r62082s6h06h.apps.googleusercontent.com";
    private static final String API_SECRET = "y5g6lkq2rU2RuVehqv3opzU7";
    //    private static final String SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
    private static final String CALLBACK = "http://localhost:8080/google/callback";
    private static final String USER_INFO = "https://www.googleapis.com/oauth2/v2/userinfo?alt=json";

    private UserService userService;
    private RoleService roleService;

    private PasswordEncoder encoder;

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth/google")
    public void auth(HttpServletResponse response) throws IOException {
        OAuth20Service service = new ServiceBuilder(API_KEY)
                .apiSecret(API_SECRET)
//                .scope(SCOPE_PROFILE)
                .scope(SCOPE_EMAIL)
                .callback(CALLBACK)
                .build(GoogleApi20.instance());
        String authUrl = service.getAuthorizationUrl();
        response.setStatus(302);
        response.sendRedirect(authUrl);
    }

    @GetMapping("/google/callback")
    public String google(
            @RequestParam String code,
            Map<String, Object> model,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {

        OAuth20Service service = new ServiceBuilder(API_KEY)
                .apiSecret(API_SECRET)
//                .scope(SCOPE_PROFILE)
                .scope(SCOPE_EMAIL)
                .callback(CALLBACK)
                .build(GoogleApi20.instance());

        try {
            OAuth2AccessToken accessToken = service.getAccessToken(code);
            OAuthRequest request = new OAuthRequest(Verb.GET, USER_INFO);
            service.signRequest(accessToken, request);
            Response response = service.execute(request);

            JsonObject jsonObject = (new JsonParser()).parse(response.getBody()).getAsJsonObject();
            String email = jsonObject.get("email").getAsString();

            User user = userService.findByEmail(email);

            boolean isAdmin = false;

            List<GrantedAuthority> authorities = new ArrayList<>();
            if (user.getRoles().contains(roleService.getAllRoles().get(0))) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                isAdmin = true;
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            UserDetails aUser =
                    new org.springframework.security.core.userdetails.User(email, user.getPassword(), authorities);
            Authentication authReq
                    = new UsernamePasswordAuthenticationToken(aUser, null, authorities);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authReq);
            HttpSession session = servletRequest.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

            if (isAdmin) {
                return "redirect:/admin/list";
            } else {
                return "redirect:/user/view";
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.info("/google/callback", e);
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (RecordNotFoundException rnfe) {
            logger.info("/google/callback", rnfe);
            servletResponse.setStatus((HttpServletResponse.SC_FORBIDDEN));
        }
        return null;
    }
}
