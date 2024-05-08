package com.inexture.ecommerce.config;

import com.inexture.ecommerce.constant.Constants;
import com.inexture.ecommerce.dto.UserDTO;
import com.inexture.ecommerce.repository.UserRepository;
import com.inexture.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = null;
        if(authentication.getPrincipal() instanceof DefaultOAuth2User userDetails) {
            String email = userDetails.getAttribute(Constants.EMAIL);
            if(userRepo.findByEmail(email) == null) {
                UserDTO user = new UserDTO();
                user.setEmail(email);
                user.setFirstName(userDetails.getAttribute(Constants.GIVEN_NAME));
                user.setLastName(userDetails.getAttribute(Constants.FAMILY_NAME));
                user.setUsername(userDetails.getAttribute(Constants.NAME));
                user.setProvider(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
                userService.addUser(user);
            }
        }  redirectUrl = "/index";
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

}
