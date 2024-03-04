package com.example.application.views.AuthViews;

import com.example.application.data.service.AuthService;
import com.example.application.data.utils.Role;
import com.example.application.views.MainView;
import com.example.application.views.RoleViews.AdminView;
import com.example.application.views.RoleViews.DoctorView;
import com.example.application.views.RoleViews.PharmacistView;
import com.example.application.views.RoleViews.ReceptionistView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

@PageTitle("Login | Hospital")
@Route(value = "login", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
public class LoginView extends Div {

    private final AuthService authService;
    public LoginView(@Autowired AuthService authService){
        this.authService = authService;
        setId("login-view");
        setSizeFull();

        var email = new EmailField("Email");
        email.setWidth("25rem");
        var password = new PasswordField("Password");
        password.setWidth("25rem");

        var loginButton = new Button("Login", event -> {
            try {
                this.authService.authenticate(email.getValue(), password.getValue());
                navigateToViewByRole(this.authService.getUserRole());
            } catch (AuthService.AuthException e) {
                Notification.show("Wrong credentials!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.setWidth("25rem");

        VerticalLayout layout = new VerticalLayout(
                new H2("Welcome to 'Dr. C. I. Parhon' hospital Iasi"),
                email,
                password,
                loginButton
        );
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        add(layout);
    }

    private void navigateToViewByRole(Role role) {
        if (role.equals(Role.admin)) {
            navigateTo(AdminView.class);
        } else if (role.equals(Role.doctor)) {
            navigateTo(DoctorView.class);
        } else if (role.equals(Role.pharmacist)) {
            navigateTo(PharmacistView.class);
        } else {
            navigateTo(ReceptionistView.class);
        }
    }

    private void navigateTo(Class<? extends Component> viewClass) {
        UI.getCurrent().navigate(viewClass);
    }
}
