package com.example.application.data.service;

import com.example.application.data.entity.User;
import com.example.application.data.repository.UserRepository;
import com.example.application.data.utils.Role;
import com.example.application.views.*;
import com.example.application.views.AuthViews.LogoutView;
import com.example.application.views.RoleViews.AdminView;
import com.example.application.views.RoleViews.DoctorView;
import com.example.application.views.RoleViews.PharmacistView;
import com.example.application.views.RoleViews.ReceptionistView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Service
public class AuthService {

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) {

    }
    public class AuthException extends Exception{

    }
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) throws SQLException {
        this.userRepository = userRepository;
        userRepository.save(new User("admin","admin",Role.admin));

    }

    public void authenticate (String email, String password) throws AuthException, SQLException {
        User user = userRepository.findByEmail(email);
        if(user != null && user.checkPassword(password)) {
            VaadinSession.getCurrent().setAttribute(User.class, user);
            createRoutes(user.getRole());
        } else {
            throw new AuthException();
        }
    }

    private void createRoutes(Role role) {
        getAuthorizedRoutes(role)
                .forEach(authorizedRoute ->
                        RouteConfiguration.forSessionScope().setRoute(
                                authorizedRoute.route,authorizedRoute.view, MainView.class
                        ));
    }
    public List<AuthorizedRoute> getAuthorizedRoutes(Role role) {
        var routes = new ArrayList<AuthorizedRoute>();
        if (role.equals(Role.admin)) {
            routes.add(new AuthorizedRoute("admin","Admin | Hospital", AdminView.class));
            routes.add(new AuthorizedRoute("logout","Logout", LogoutView.class));
        } else if (role.equals(Role.doctor)) {
            routes.add(new AuthorizedRoute("doctor","Doctor | Hospital", DoctorView.class));
            routes.add(new AuthorizedRoute("logout","Logout", LogoutView.class));
        } else if (role.equals(Role.pharmacist)) {
            routes.add(new AuthorizedRoute("pharmacist","Pharmacist | Hospital", PharmacistView.class));
            routes.add(new AuthorizedRoute("logout","Logout", LogoutView.class));
        } else if (role.equals(Role.receptionist)) {
            routes.add(new AuthorizedRoute("receptionist","Receptionist | Hospital", ReceptionistView.class));
            routes.add(new AuthorizedRoute("logout","Logout", LogoutView.class));
        }
        return routes;
    }
    public Role getUserRole(){
        var user = VaadinSession.getCurrent().getAttribute(User.class);
        return user.getRole();
    }

}
