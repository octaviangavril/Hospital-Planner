package com.example.application.views.RoleViews;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Pharmacist;
import com.example.application.data.entity.Receptionist;
import com.example.application.data.service.GridService;
import com.example.application.data.service.RegisterService;
import com.example.application.views.AuthViews.LogoutView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

@PageTitle("Admin | Hospital")
public class AdminView extends VerticalLayout {
    private RegisterService registerService;
    private GridService gridService;
    public AdminView(@Autowired RegisterService registerService, @Autowired GridService gridService) {
        this.registerService = registerService;
        this.gridService = gridService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        HorizontalLayout buttonsLayout = new HorizontalLayout();

        VerticalLayout buttonsSet1 = new VerticalLayout();

        Button doctorButton = createButton("Register a doctor");
        doctorButton.addClickListener(e -> showDoctorRegistrationForm());
        Button showDoctors = new Button("View all doctors");
        showDoctors.addClickListener(e -> showDoctorsGrid());
        showDoctors.getStyle().set("width", "15rem");

        buttonsSet1.add(doctorButton,showDoctors);
        buttonsSet1.setAlignItems(Alignment.CENTER);

        VerticalLayout buttonsSet2 = new VerticalLayout();

        Button receptionistButton = createButton("Register a receptionist");
        receptionistButton.addClickListener(e -> showReceptionistRegistrationForm());
        Button showReceptionists = new Button("View all receptionists");
        showReceptionists.addClickListener(e -> showReceptionistsGrid());
        showReceptionists.getStyle().set("width", "15rem");

        buttonsSet2.add(receptionistButton,showReceptionists);
        buttonsSet2.setAlignItems(Alignment.CENTER);


        VerticalLayout buttonsSet3 = new VerticalLayout();

        Button pharmacistButton = createButton("Register a pharmacist");
        pharmacistButton.addClickListener(e -> showPharmacistRegistrationForm());
        Button showPharmacists = new Button("View all pharmacists");
        showPharmacists.addClickListener(e -> showPharmacistsGrid());
        showPharmacists.getStyle().set("width","15rem");

        buttonsSet3.add(pharmacistButton,showPharmacists);
        buttonsSet3.setAlignItems(Alignment.CENTER);

        buttonsLayout.add(buttonsSet1, buttonsSet2, buttonsSet3);
        buttonsLayout.setAlignItems(Alignment.CENTER);

        H2 welcome = new H2("Welcome admin!");
        HorizontalLayout title = new HorizontalLayout(welcome);
        title.setDefaultVerticalComponentAlignment(Alignment.START);

        add(title, buttonsLayout);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        Tab logout = createLogout();
        add(logout);

    }

    public void showDoctorsGrid() {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");

        Grid<Doctor> grid = new Grid<>();

        grid.addClassNames("doctors-grid");
        grid.addColumn(Doctor::getId).setHeader("ID");
        grid.addColumn(Doctor::getFirst_name).setHeader("First Name");
        grid.addColumn(Doctor::getLast_name).setHeader("Last Name");
        grid.addColumn(Doctor::getSpeciality).setHeader("Speciality");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        try {
            grid.setItems(gridService.getAllDoctors());
        } catch (SQLException e) {
            dialog.add(new Label("No data found"));
            dialog.open();
        }

        grid.addComponentColumn(doctor -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(event -> {
                gridService.deleteDoctor(doctor.getId());
                ListDataProvider<Doctor> dataProvider = (ListDataProvider<Doctor>) grid.getDataProvider();
                dataProvider.getItems().removeIf(doc -> doc.getId() == doctor.getId());
                dataProvider.refreshAll();
            });
            return deleteButton;
        }).setHeader("Actions");

        dialog.add(grid);
        dialog.open();
    }

    private void showReceptionistsGrid(){
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");

        Grid<Receptionist> grid = new Grid<>();

        grid.addClassNames("receptionists-grid");
        grid.addColumn(Receptionist::getId).setHeader("ID");
        grid.addColumn(Receptionist::getFirst_name).setHeader("First Name");
        grid.addColumn(Receptionist::getLast_name).setHeader("Last Name");
        grid.addColumn(Receptionist::getDesk_id).setHeader("Desk ID");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        try {
            grid.setItems(gridService.getAllReceptionists());
        } catch (SQLException e) {
            dialog.add(new Label("No data found"));
            dialog.open();
        }

        grid.addComponentColumn(receptionist -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(event -> {
                gridService.deleteReceptionist(receptionist.getId());
                ListDataProvider<Receptionist> dataProvider = (ListDataProvider<Receptionist>) grid.getDataProvider();
                dataProvider.getItems().removeIf(doc -> doc.getId() == receptionist.getId());
                dataProvider.refreshAll();
            });
            return deleteButton;
        }).setHeader("Actions");

        dialog.add(grid);
        dialog.open();
    }

    private void showPharmacistsGrid(){
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");

        Grid<Pharmacist> grid = new Grid<>();

        grid.addClassNames("pharmacists-grid");
        grid.addColumn(Pharmacist::getId).setHeader("ID");
        grid.addColumn(Pharmacist::getFirst_name).setHeader("First Name");
        grid.addColumn(Pharmacist::getLast_name).setHeader("Last Name");
        grid.addColumn(Pharmacist::getPharmacy).setHeader("Pharmacy");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        try {
            grid.setItems(gridService.getAllPharmacists());
        } catch (SQLException e) {
            dialog.add(new Label("No data found"));
            dialog.open();
        }

        grid.addComponentColumn(pharmacist -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(event -> {
                gridService.deletePharmacist(pharmacist.getId());
                ListDataProvider<Pharmacist> dataProvider = (ListDataProvider<Pharmacist>) grid.getDataProvider();
                dataProvider.getItems().removeIf(pharm -> pharm.getId() == pharmacist.getId());
                dataProvider.refreshAll();
            });
            return deleteButton;
        }).setHeader("Actions");

        dialog.add(grid);
        dialog.open();
    }
    public Tab createLogout(){
        Tab tab = new Tab();
        tab.add(new RouterLink("Logout", LogoutView.class ));
        ComponentUtil.setData(tab, Class.class, LogoutView.class);
        return tab;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("width", "15rem");
        button.getStyle().set("height", "15rem");
        button.getStyle().set("font-size", "1rem");
        button.getStyle().set("border-radius", "0.75");

        return button;
    }

    private void showDoctorRegistrationForm() {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField firstNameField = createTextField();
        TextField lastNameField = createTextField();
        ComboBox<String> specializationField = new ComboBox<>();
        EmailField emailField = createEmailField();
        PasswordField passwordField = createPasswordField();

        specializationField.setItems("Medicină Generală", "Pediatrie", "Chirurgie Generală", "Medicină Internă", "Obstetrică și Ginecologie", "Cardiologie", "Neurologie", "Oftalmologie");
        specializationField.setPlaceholder("Select speciality");
        specializationField.setClearButtonVisible(true);
        specializationField.setAllowCustomValue(false);
        specializationField.setWidth("100%");

        formLayout.addFormItem(firstNameField, new Label("First Name"));
        formLayout.addFormItem(lastNameField, new Label("Last Name"));
        formLayout.addFormItem(specializationField, new Label("Speciality"));
        formLayout.addFormItem(emailField, new Label("Email"));
        formLayout.addFormItem(passwordField, new Label("Password"));

        Button registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String speciality = specializationField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();

            try {
                registerService.registerDoctor(firstName, lastName, speciality, email, password);
                Notification.show("The doctor has been successfully added");
            } catch (Exception ex) {
                if(ex.getMessage().contains("already exists")) {
                    Notification.show("Doctor already exists!");
                } else {
                    System.out.println(ex.getMessage());
                }
            }

            dialog.close();
        });
        registerButton.addClickShortcut(Key.ENTER);
        formLayout.add(registerButton);
        dialog.add(formLayout);
        dialog.open();
    }

    public void showReceptionistRegistrationForm() {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));


        TextField firstNameField = createTextField();
        TextField lastNameField = createTextField();
        ComboBox<String> deskIdField = new ComboBox<>();
        EmailField emailField = createEmailField();
        PasswordField passwordField = createPasswordField();

        deskIdField.setItems("1", "2", "3", "4");
        deskIdField.setPlaceholder("Select desk ID");
        deskIdField.setClearButtonVisible(true);
        deskIdField.setAllowCustomValue(false);
        deskIdField.setWidth("100%");

        formLayout.addFormItem(firstNameField,new Label("First Name"));
        formLayout.addFormItem(lastNameField, new Label("Last Name"));
        formLayout.addFormItem(deskIdField,new Label("Desk ID"));
        formLayout.addFormItem(emailField, new Label("Email"));
        formLayout.addFormItem(passwordField, new Label("Password"));

        Button registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            Long deskId = Long.parseLong(deskIdField.getValue());
            String email = emailField.getValue();
            String password = passwordField.getValue();

            try {
                registerService.registerReceptionist(firstName, lastName, deskId, email, password);
                Notification.show("The receptionist has been successfully added");
            } catch (Exception ex) {
                if(ex.getMessage().contains("already exists")) {
                    Notification.show("Receptionist already exists!");
                } else {
                    System.out.println(ex.getMessage());
                }
            }
            dialog.close();
        });
        registerButton.addClickShortcut(Key.ENTER);
        formLayout.add(registerButton);
        dialog.add(formLayout);
        dialog.open();
    }

    public void showPharmacistRegistrationForm(){
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField firstNameField = createTextField();
        TextField lastNameField = createTextField();
        ComboBox<String> pharmacyField = new ComboBox<>();
        EmailField emailField = createEmailField();
        PasswordField passwordField = createPasswordField();

        pharmacyField.setItems("Catena", "DoctorMax", "Ropharma");
        pharmacyField.setPlaceholder("Select pharmacy");
        pharmacyField.setClearButtonVisible(true);
        pharmacyField.setAllowCustomValue(false);
        pharmacyField.setWidth("100%");

        formLayout.addFormItem(firstNameField,new Label("First Name"));
        formLayout.addFormItem(lastNameField, new Label("Last Name"));
        formLayout.addFormItem(pharmacyField,new Label("Pharmacy"));
        formLayout.addFormItem(emailField, new Label("Email"));
        formLayout.addFormItem(passwordField, new Label("Password"));

        Button registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String pharmacy = pharmacyField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();

            try {
                registerService.registerPharmacist(firstName, lastName, pharmacy, email, password);
                Notification.show("The pharmacist has been successfully added");
            } catch (Exception ex) {
                if(ex.getMessage().contains("already exists")) {
                    Notification.show("Pharmacist already exists!");
                } else {
                    System.out.println(ex.getMessage());
                }
            }
            dialog.close();
        });
        registerButton.addClickShortcut(Key.ENTER);

        formLayout.add(registerButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private TextField createTextField() {
        TextField textField = new TextField();
        textField.setWidth("100%");
        return textField;
    }

    private EmailField createEmailField() {
        EmailField emailField = new EmailField();
        emailField.setWidth("100%");
        return emailField;
    }

    private PasswordField createPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setWidth("100%");
        return passwordField;
    }
}
