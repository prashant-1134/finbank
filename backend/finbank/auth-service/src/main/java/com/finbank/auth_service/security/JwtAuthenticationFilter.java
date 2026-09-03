//package com.finbank.auth_service.security;
//
//import com.finbank.auth_service.service.impl.UserServiceImpl;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    public JwtAuthenticationFilter(
//            JwtService jwtService,
//            UserDetailsService userDetailsService
//    ){
//        this.jwtService = jwtService;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//        HttpServletResponse response,
//        FilterChain filterChain
//                )
//    throws ServletException, IOException{
//            String authHeader = request.getHeader("Authorization");
//
//            /*No Authentication header*/
//            if(authHeader == null || !authHeader.startsWith("Bearer ")){
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            /*remove 'BEARER prefix'*/
//            String token = authHeader.substring(7);
//
//            String username;
//
//            try{
//                username = jwtService.extractUsername(token);
//            }
//            catch(Exception exception){
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            try{
//                if(username != null && SecurityContextHolder.getContext()
//                        .getAuthentication() == null){
//                    UserDetails userDetails =
//                            userDetailsService.loadUserByUsername(username);
//
//                    if(jwtService.isTokenValid(
//                            token,
//                            userDetails.getUsername()
//                    )){
//                        UsernamePasswordAuthenticationToken authentication =
//                                new UsernamePasswordAuthenticationToken(
//                                        userDetails,
//                                        null,
//                                        userDetails.getAuthorities()
//                                );
//
//                        authentication.setDetails(
//                                new WebAuthenticationDetailsSource()
//                                        .buildDetails(request)
//                        );
//
//                        SecurityContextHolder
//                                .getContext()
//                                .setAuthentication(authentication);
//                    }
//                }
//            }
//            catch(Exception e){
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//
//                response.getWriter().write("""
//                        {
//                            "status" : 401,
//                            "error" : "unauthorized",
//                            "message" : "Invalid or expire token"
//                        }""");
//                return;
//            }
//
//
//            filterChain.doFilter(request, response);
//
//
//        }
//
//}

package com.finbank.auth_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /*
         * No Authorization header
         * OR not a Bearer token
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer " prefix
         */
        String token = authHeader.substring(7);

        try {

            String username = jwtService.extractUsername(token);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                /*
                 * Token exists but is not valid
                 */
                if (!jwtService.isTokenValid(
                        token,
                        userDetails.getUsername()
                )) {

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );

                    response.setContentType("application/json");

                    response.getWriter().write("""
                            {
                                "status": 401,
                                "error": "unauthorized",
                                "message": "Invalid or expired token"
                            }
                            """);

                    return;
                }

                /*
                 * Token is valid → authenticate user
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception exception) {

            /*
             * Malformed / modified / expired / incorrectly signed JWT
             */
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "unauthorized",
                        "message": "Invalid or expired token"
                    }
                    """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}
