package com.example.application.views.RoleViews;

import com.example.application.data.entity.Appointment;
import com.example.application.data.entity.User;
import com.example.application.data.service.AppointmentService;
import com.example.application.data.service.DoctorService;
import com.example.application.data.service.RegisterService;
import com.example.application.views.AuthViews.LogoutView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Receptionist | Hospital")
public class ReceptionistView extends VerticalLayout {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final RegisterService registerService;
    public ReceptionistView(@Autowired RegisterService registerService, @Autowired AppointmentService appointmentService, @Autowired DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.registerService = registerService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        User user = VaadinSession.getCurrent().getAttribute(User.class);
        H2 welcome;
        welcome = new H2("Welcome receptionist " + registerService.getReceptionistFullName(user.getId()));
        welcome.getStyle().set("margin", "5rem");

        H3 title = new H3("Make an appointment to a patient");

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(Alignment.CENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("60%");
        formLayout.getStyle().set("max-width", "30rem");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("900px", 3, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1200px", 4, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        TextField firstNameField = createTextField();
        TextField lastNameField = createTextField();
        DatePicker birthdateField = createDatePicker();

        formLayout.addFormItem(firstNameField,new Label("First Name"));
        formLayout.addFormItem(lastNameField, new Label("Last Name"));
        formLayout.addFormItem(birthdateField,new Label("Birthdate"));

        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Febra","Oboseală","Durere","Tuse","Grețuri și vărsături",
                "Pierdere în greutate", "Dificultăți respiratorii", "Umflături și inflamații",
                "Sângerări","Slăbiciune sau amorțeală în membre","Probleme de vedere",
                "Schimbări în tranzitul intestinal","Palpitații","Dureri abdominale");
        comboBox.setWidth("100%");
        formLayout.addFormItem(comboBox,new Label("Symptoms"));

        Button nextButton = new Button("See available appointments");
        nextButton.addClickListener(e -> {
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String birthdate = birthdateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String[] symptoms = comboBox.getValue().toArray(new String[0]);
            try {
                List<Appointment> availableAppointments = appointmentService.getAvailableAppointments(
                        symptoms,
                        birthdate
                );
                createDialog(availableAppointments,birthdate,firstName,lastName, symptoms);
                firstNameField.clear();
                lastNameField.clear();
                birthdateField.clear();
                comboBox.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        formLayout.add(nextButton);
        content.add(formLayout);

        add(welcome,title,content);
        Tab logout = createLogout();
        add(logout);

    }

    private void createDialog(List<Appointment> availableAppointments, String birthdate, String firstName, String lastName, String[] symptoms) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        VerticalLayout patientLayout = new VerticalLayout();
        Label firstNameLabel = new Label("First Name: " + firstName);
        Label lastNameLabel = new Label("Last Name: " + lastName);
        Label birthDateLabel = new Label("Birthdate: " + birthdate);
        patientLayout.add(firstNameLabel, lastNameLabel, birthDateLabel);

        VerticalLayout specializationLayout = new VerticalLayout();
        Label specializationLabel = new Label("Specialization: " + doctorService.getDoctorSpeciality(availableAppointments.get(0).getDoctor_id()));
        specializationLayout.add(specializationLabel);
        specializationLayout.setAlignItems(Alignment.END);

        Grid<Appointment> appointmentGrid = new Grid<>();
        appointmentGrid.setItems(availableAppointments);
        appointmentGrid.addColumn(appointment -> doctorService.getDoctorFullName(appointment.getDoctor_id())).setHeader("Doctor");
        appointmentGrid.addColumn(Appointment::getAppointment_date).setHeader("Appointment date");
        appointmentGrid.addColumn(Appointment::getAppointment_time).setHeader("Appointment time");
        Button submitButton = new Button("Submit");
        submitButton.setEnabled(false);

        appointmentGrid.addSelectionListener(event -> {
            boolean hasSelection = event.getFirstSelectedItem().isPresent();
            submitButton.setEnabled(hasSelection);
        });

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.add(patientLayout, specializationLayout);
        contentLayout.setWidth("100%");

        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton);

        dialogLayout.add(contentLayout, appointmentGrid, buttonLayout);

        submitButton.addClickListener(event -> {
            Appointment selectedAppointment = appointmentGrid.getSelectedItems().iterator().next();
            System.out.println(selectedAppointment + " " + selectedAppointment.getId());
            try {
                appointmentService.saveAppointment(selectedAppointment,firstName,lastName,birthdate,symptoms);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            dialog.close();
        });

        dialog.open();
    }

    public Tab createLogout(){
        Tab tab = new Tab();
        tab.add(new RouterLink("Logout", LogoutView.class ));
        ComponentUtil.setData(tab, Class.class, LogoutView.class);
        return tab;
    }
    private TextField createTextField() {
        TextField textField = new TextField();
        textField.setWidth("100%");
        return textField;
    }

    private DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setWidth("100%");
        return datePicker;
    }
}
