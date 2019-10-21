package ru.solon4ak.controller;

import com.github.scribejava.core.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.solon4ak.exceptions.RecordNotFoundException;
import ru.solon4ak.model.User;
import ru.solon4ak.service.ScribeJavaGoogleService;
import ru.solon4ak.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class OAuthController {

    private static Logger logger = LoggerFactory.getLogger(OAuthController.class);

    private ScribeJavaGoogleService scribeJavaGoogleService;

    @Autowired
    public void setScribeJavaGoogleService(ScribeJavaGoogleService scribeJavaGoogleService) {
        this.scribeJavaGoogleService = scribeJavaGoogleService;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth/google")
    public void auth(HttpServletResponse response) throws IOException {
        String authUrl = scribeJavaGoogleService.getOAuth20Service().getAuthorizationUrl();
        response.setStatus(302);
        response.sendRedirect(authUrl);
    }

    @GetMapping("/google/callback")
    public String google(
            @RequestParam String code,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        try {
            Response response = scribeJavaGoogleService.getOAuthResponse(code);
            String email = scribeJavaGoogleService.getEmailFromOAuth2Response(response);
            User user = userService.findByEmail(email);
            SecurityContext sc = scribeJavaGoogleService.addUserToSecurityContext(user);

            HttpSession session = servletRequest.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

            if (scribeJavaGoogleService.userHasAdminRole(user)) {
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
