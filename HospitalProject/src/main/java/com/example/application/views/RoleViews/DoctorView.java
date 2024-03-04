package com.example.application.views.RoleViews;

import com.example.application.data.entity.Appointment;
import com.example.application.data.entity.User;
import com.example.application.data.service.AppointmentService;
import com.example.application.data.service.DoctorService;
import com.example.application.data.service.PrescriptionService;
import com.example.application.views.AuthViews.LogoutView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@PageTitle("Doctor | Hospital")
public class DoctorView extends VerticalLayout {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PrescriptionService prescriptionService;

    public DoctorView(@Autowired PrescriptionService prescriptionService, @Autowired AppointmentService appointmentService, @Autowired DoctorService doctorService) {
        this.prescriptionService = prescriptionService;
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        Button viewAppointmentsButton = new Button("View appointments");
        viewAppointmentsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewAppointmentsButton.getStyle().set("width", "25rem");
        viewAppointmentsButton.getStyle().set("height", "5rem");
        viewAppointmentsButton.getStyle().set("font-size", "1rem");
        viewAppointmentsButton.getStyle().set("border-radius", "0.75rem");
        viewAppointmentsButton.addClickListener(e -> showAppointmentsGrid());

        HorizontalLayout buttonLayout = new HorizontalLayout(viewAppointmentsButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setAlignItems(Alignment.CENTER);

        User user = VaadinSession.getCurrent().getAttribute(User.class);
        H2 welcome;
        welcome = new H2("Welcome Dr. " + doctorService.getDoctorFullName(doctorService.getDoctorByUserId(user.getId()).getId()));
        welcome.getStyle().set("margin", "5rem");

        add(new H3("Specialization " + doctorService.getDoctorByUserId(user.getId()).getSpeciality()),welcome, buttonLayout);

        setAlignSelf(Alignment.CENTER, buttonLayout);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        Tab logout = createLogout();
        add(logout);

    }

    public Tab createLogout() {
        Tab tab = new Tab();
        tab.add(new RouterLink("Logout", LogoutView.class));
        ComponentUtil.setData(tab, Class.class, LogoutView.class);
        return tab;
    }

    private void showAppointmentsGrid() {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");

        Grid<Appointment> grid = new Grid<>();


        grid.addClassNames("appointments-grid");
        grid.addColumn(appointment -> doctorService.getDoctorFullName(appointment.getDoctor_id())).setHeader("Doctor");
        grid.addColumn(appointmentService::getPatientFullName).setHeader("Patient");
        grid.addColumn(Appointment::getAppointment_date).setHeader("Date");
        grid.addColumn(Appointment::getAppointment_time).setHeader("Time");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));


        User user = VaadinSession.getCurrent().getAttribute(User.class);
        try {
            List<Appointment> doctorAppointments = appointmentService.getAppointmentsByDoctor(doctorService.getDoctorByUserId(user.getId()).getId());
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            List<Appointment> filteredAppointments = doctorAppointments.stream()
                    .filter(appointment -> {
                        LocalTime appointmentTime = LocalTime.parse(appointment.getAppointment_time());
                        LocalDate appointmentDate = LocalDate.parse(appointment.getAppointment_date());
                        return appointmentDate.isAfter(currentDate)
                                || (appointmentDate.isEqual(currentDate) && appointmentTime.isAfter(currentTime));
                    }).toList();

            ListDataProvider<Appointment> dataProvider = (ListDataProvider<Appointment>) grid.getDataProvider();
            dataProvider.getItems().clear();
            dataProvider.getItems().addAll(filteredAppointments);
            dataProvider.refreshAll();
        } catch (SQLException e) {
            if (e.getMessage().contains("No data found")) {
                dialog.add(new Label("No data found"));
                dialog.open();
            } else {
                throw new RuntimeException(e);
            }
        }

        grid.addComponentColumn(appointment -> {
            Button startButton = new Button("Start");
            startButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            startButton.addClickListener(event -> {
                System.out.println(appointment);
                createPatientFile(appointment);
                ListDataProvider<Appointment> dataProvider = (ListDataProvider<Appointment>) grid.getDataProvider();
                dataProvider.getItems().removeIf(app -> app.getId() == appointment.getId());
                dataProvider.refreshAll();
                dialog.close();
            });
            return startButton;
        }).setHeader("Actions");

        dialog.add(grid);
        dialog.open();
    }

    public void createPatientFile(Appointment appointment) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");

        String patientFullName = appointmentService.getPatientFullName(appointment);
        String patientBirthday = appointmentService.getPatientBirthdate(appointment);
        String[] symptoms;
        try {
            symptoms = appointmentService.getSymptoms(appointment);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        H2 title = new H2("Patient file");
        title.getStyle().set("text-align", "center");

        HorizontalLayout patientData = new HorizontalLayout();

        TextField nameField = new TextField("Full Name");
        nameField.setValue(patientFullName);
        nameField.setReadOnly(true);

        TextField dobField = new TextField("Birthdate");
        dobField.setValue(patientBirthday);
        dobField.setReadOnly(true);

        patientData.add(nameField, dobField);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        contentLayout.add(title, patientData);
        List<String> intensities = new ArrayList<>();

        Arrays.stream(symptoms).forEach(symptom -> {

            HorizontalLayout symptomData = new HorizontalLayout();

            TextField symptomField = new TextField("Symptom");
            symptomField.setValue(symptom);
            symptomField.setReadOnly(true);

            Select<String> intensitySelect = new Select<>();
            intensitySelect.setLabel("Degree of intensity");
            intensitySelect.setItems("Ușor", "Moderat", "Sever");

            Binder<String> binder = new Binder<>();
            binder.forField(intensitySelect)
                    .withValidator(new StringLengthValidator("Select a degree of intensity", 1, null));

            intensitySelect.addValueChangeListener(event -> {
                intensities.add(event.getValue());
            });

            symptomData.add(symptomField, intensitySelect);

            contentLayout.add(symptomData);
        });


        Button createPrescriptionButton = new Button("Create prescription");
        createPrescriptionButton.addClickListener(event -> {
            dialog.close();
            String current_date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String current_time = LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
            User user = VaadinSession.getCurrent().getAttribute(User.class);
            try {
                prescriptionService.createPrescription(patientFullName.split(" ")[0],
                        patientFullName.split(" ")[1],
                        patientBirthday,
                        doctorService.getDoctorByUserId(user.getId()).getId(),
                        current_date, current_time,
                        symptoms, intensities.toArray(new String[intensities.size()]));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            createPrescription(appointment, current_date, current_time, intensities.toArray(new String[intensities.size()]));

        });
        contentLayout.add(createPrescriptionButton);
        dialog.add(contentLayout);

        dialog.open();
    }

    public void createPrescription(Appointment appointment, String prescription_date, String prescription_time, String[] intensities) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("40rem");

        H2 title = new H2("Patient prescription");
        title.getStyle().set("text-align", "center");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        String first_name = appointmentService.getPatientFullName(appointment).split(" ")[0];
        String last_name = appointmentService.getPatientFullName(appointment).split(" ")[1];
        String birthDate = appointmentService.getPatientBirthdate(appointment);

        VerticalLayout patientLayout = new VerticalLayout();
        Label firstNameLabel = new Label("First Name: " + first_name);
        Label lastNameLabel = new Label("Last Name: " + last_name);
        Label birthDateLabel = new Label("Birthdate: " + birthDate);
        patientLayout.add(firstNameLabel, lastNameLabel, birthDateLabel);

        User user = VaadinSession.getCurrent().getAttribute(User.class);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.START);

        headerLayout.add(patientLayout, new H4("PRESCRIPTION ID:" +
                prescriptionService.getPrescriptionId(
                        first_name, last_name, birthDate,
                        doctorService.getDoctorByUserId(user.getId()).getId(),
                        prescription_date, prescription_time
                )));

        headerLayout.setWidth("100%");

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        contentLayout.add(title, headerLayout);

        Map<String, Double> dosages = new HashMap<>();
        Map<String, Integer> durations = new HashMap<>();


        try {
            for (String symptom : appointmentService.getSymptoms(appointment)) {
                VerticalLayout medData = new VerticalLayout();
                String medicine = prescriptionService.getMedicine(first_name,
                        last_name,
                        birthDate,
                        doctorService.getDoctorByUserId(user.getId()).getId(),
                        prescription_date, prescription_time, symptom);

                dosages.put(medicine, 0.5);
                durations.put(medicine, 1);

                H4 med = new H4(titleMed(symptom, medicine, dosages.get(medicine), durations.get(medicine)));
                VerticalLayout medDescription = new VerticalLayout();

                HorizontalLayout dosageLayout = new HorizontalLayout();

                NumberField dosageField = new NumberField("Dosage per day (pills)");
                dosageField.setReadOnly(true);
                dosageField.setValue(0.5);
                dosageField.setStep(0.5);
                dosageField.setWidth("100%");
                Button increaseDosageButton = new Button("+");
                increaseDosageButton.addClickListener(event -> {
                    double currentDosage = dosageField.getValue();
                    dosageField.setValue(currentDosage + 0.5);
                });
                increaseDosageButton.setWidth("1rem");
                Button decreaseDosageButton = new Button("-");
                decreaseDosageButton.addClickListener(event -> {
                    double currentDosage = dosageField.getValue();
                    if (currentDosage > 0.5) {
                        dosageField.setValue(currentDosage - 0.5);
                    }
                });
                dosageLayout.add(dosageField, decreaseDosageButton, increaseDosageButton);
                dosageLayout.setDefaultVerticalComponentAlignment(Alignment.END);

                dosageField.addValueChangeListener(event -> {
                    dosages.put(medicine, event.getValue());
                    med.setText(titleMed(symptom, medicine, dosages.get(medicine), durations.get(medicine)));
                });

                HorizontalLayout durationLayout = new HorizontalLayout();

                IntegerField durationField = new IntegerField("Duration of treatment (days)");
                durationField.setReadOnly(true);
                durationField.setValue(1);
                durationField.setWidth("100%");
                Button increaseDurationButton = new Button("+");
                increaseDurationButton.addClickListener(event -> {
                    int currentDuration = durationField.getValue();
                    durationField.setValue(currentDuration + 1);
                });
                increaseDurationButton.setWidth("1rem");
                Button decreaseDurationButton = new Button("-");
                decreaseDurationButton.addClickListener(event -> {
                    int currentDuration = durationField.getValue();
                    if (currentDuration > 1) {
                        durationField.setValue(currentDuration - 1);
                    }
                });
                durationLayout.add(durationField, decreaseDurationButton, increaseDurationButton);
                durationLayout.setDefaultVerticalComponentAlignment(Alignment.END);
                medDescription.add(dosageLayout, durationLayout);
                medDescription.setAlignItems(Alignment.CENTER);

                durationField.addValueChangeListener(event -> {
                    durations.put(medicine, event.getValue());
                    med.setText(titleMed(symptom, medicine, dosages.get(medicine), durations.get(medicine)));
                });

                medData.add(med, medDescription);

                contentLayout.add(medData);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setAlignItems(FlexComponent.Alignment.START);
        footerLayout.add(new H4("Date and time:\n" + prescription_date + "\n" + prescription_time), new H4("Signature:\n" + doctorService.getDoctorFullName(appointment.getDoctor_id())));
        footerLayout.setWidth("100%");

        contentLayout.add(footerLayout);

        Button createPrescriptionButton = new Button("Submit prescription");
        createPrescriptionButton.addClickListener(event -> {
            appointmentService.deleteAppointment(appointment);
            prescriptionService.finishPrescription(first_name, last_name, birthDate,
                    doctorService.getDoctorByUserId(user.getId()).getId(),
                    prescription_date,
                    prescription_time,
                    dosages, durations);
            dialog.close();
        });
        contentLayout.add(createPrescriptionButton);
        dialog.add(contentLayout);

        dialog.open();
    }

    public String titleMed(String symptom, String medicine, double dosage, int duration) {
        StringBuilder text = new StringBuilder("Pentru " + symptom + ": " + medicine);

        try {
            String[] pharmacies = prescriptionService.getMedicinePharmacy(medicine, dosage, duration);
            text.append(", disponibil în farmaciile ");
            Arrays.stream(pharmacies).forEach(pharmacy -> {
                if (!Objects.equals(pharmacy, pharmacies[pharmacies.length - 1])) {
                    text.append(pharmacy).append(", ");
                } else {
                    text.append(pharmacy).append(".");
                }
            });
        } catch (SQLException e) {
            text.append(" indisponibil!");
        }

        return text.toString();
    }

}
