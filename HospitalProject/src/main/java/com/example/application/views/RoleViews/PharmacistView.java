package com.example.application.views.RoleViews;

import com.example.application.data.entity.User;
import com.example.application.data.service.MedicationStockService;
import com.example.application.data.service.PrescriptionService;
import com.example.application.views.AuthViews.LogoutView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Pharmacist | Hospital")
public class PharmacistView extends VerticalLayout {

    private final PrescriptionService prescriptionService;
    private final MedicationStockService stockService;
    private final TextField searchField;

    public PharmacistView(@Autowired MedicationStockService stockService,@Autowired PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
        this.stockService = stockService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        User user = VaadinSession.getCurrent().getAttribute(User.class);
        H2 welcome;
        welcome = new H2("Welcome pharmacist " + stockService.getPharmacistFullName(user.getId()));
        welcome.getStyle().set("margin", "4rem");

        try {
            add(new H3("Pharmacy " + stockService.getPharmacistByUserId(user.getId()).getPharmacy()),welcome);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        searchField = new TextField("Search Prescription by ID");
        searchField.setWidth("100%");
        searchField.setPattern("\\d+");
        Button stockButton = new Button("View/Update Medication Stocks");

        Button searchButton = new Button("Search");
        searchButton.addClickListener(event -> {
            String input = searchField.getValue();
            if (!input.isEmpty()) {
                if (input.matches(searchField.getPattern())) {
                    long id = Long.parseLong(input);
                    try {
                        seePrescription(id);
                    } catch (SQLException e) {
                        Notification.show("Wrong prescription Id!");
                    }
                    stockButton.setEnabled(true);
                } else {
                    Notification.show("Wrong prescription Id!");
                }
            } else {
                Notification.show("Please enter a prescription Id!");
            }
        });
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);
        searchLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        searchLayout.setWidth("25rem");

        stockButton.addClickListener(e -> viewAndUpdateStocks(user.getId()));
        stockButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stockButton.getStyle().set("width", "25rem");
        stockButton.getStyle().set("height", "5rem");
        stockButton.getStyle().set("font-size", "1rem");
        stockButton.getStyle().set("border-radius", "0.75rem");

        setAlignSelf(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        add(searchLayout,stockButton);
        Tab logout = createLogout();
        add(logout);

      }
    public Tab createLogout(){
        Tab tab = new Tab();
        tab.add(new RouterLink("Logout", LogoutView.class ));
        ComponentUtil.setData(tab, Class.class, LogoutView.class);
        return tab;
    }

    public void seePrescription(long id) throws SQLException {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("30rem");

        H2 title = new H2("Patient prescription");
        title.getStyle().set("text-align", "center");

        Label patientFirstNameLabel = new Label("First Name: " +
                prescriptionService.getPatientFirstName(id));
        Label patientLastNameLabel = new Label("Last Name: " +
                prescriptionService.getPatientLastName(id));
        Label patientBirthDateLabel = new Label("Birthdate: " +
                prescriptionService.getPatientBirthdate(id));

        VerticalLayout patientDate = new VerticalLayout();
        patientDate.add(patientFirstNameLabel,
                patientLastNameLabel,
                patientBirthDateLabel);

        Label prescriptionIdLabel = new Label("Id: " +id);
        Label doctorNameLabel = new Label("Dr. " +
                prescriptionService.getDoctorFirstName(id) +
                " " + prescriptionService.getDoctorLastName(id));

        VerticalLayout prescriptionDate = new VerticalLayout();
        prescriptionDate.add(prescriptionIdLabel,doctorNameLabel);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(patientDate,prescriptionDate);
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        VerticalLayout contentLayout = new VerticalLayout();
        Map<String,String> prescriptionItems;
        Map<String, String> selectedItems = new HashMap<>();
        try {
            prescriptionItems = prescriptionService.getMedicines(id);
            for (Map.Entry<String, String> entry : prescriptionItems.entrySet()) {
                String medicine = entry.getKey();
                String description = entry.getValue();

                HorizontalLayout pairLayout = new HorizontalLayout();

                Checkbox checkboxMed = new Checkbox(medicine + ": " + description);
                checkboxMed.setEnabled(stockService.checkStockMedicine(
                        medicine, user.getId(), description
                ));

                checkboxMed.addValueChangeListener(event -> {
                    if (event.getValue()) {
                        selectedItems.put(medicine, description);
                    } else {
                        selectedItems.remove(medicine);
                    }
                });
                pairLayout.add(checkboxMed);
                pairLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
                contentLayout.add(pairLayout);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Button sellButton = new Button("Sell prescription");
        sellButton.addClickListener(event -> {
            selectedItems.forEach((medicine, description) -> {
                try {
                    stockService.updateStock(medicine,user.getId(),description);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                if(selectedItems.isEmpty()){
                    Notification.show("No medicines selected!");
                } else if(selectedItems.size() == prescriptionItems.size()){
                    prescriptionService.deletePrescription(id);
                    dialog.close();
                } else {
                    selectedItems.forEach((medicine, description) -> {
                        try {
                            prescriptionService.updatePrescriptionMeds(
                                    id,medicine);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    dialog.close();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        contentLayout.setAlignItems(Alignment.START);
        dialog.add(headerLayout,contentLayout,sellButton);
        dialog.open();
    }

    private void viewAndUpdateStocks(long user_id) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("45rem");
        Map<String,String> medicines = new HashMap<>();
        try {
            medicines = stockService.getMedicines(user_id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Grid<Map.Entry<String, String>> grid = new Grid<>();
        grid.setItems(medicines.entrySet());
        grid.addClassNames("medicines-grid");
        grid.addColumn(Map.Entry::getKey).setHeader("Medicine");
        grid.addColumn(Map.Entry::getValue).setHeader("Stock");


        grid.addComponentColumn(entry -> {
            Button refillButton = new Button("Refill");
            refillButton.addClickListener(event -> {
                String medicine = entry.getKey();
                try {
                    stockService.refillMedicine(user_id,medicine);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Notification.show("Stock refilled for: " + medicine);
                try {
                    grid.setItems(stockService.getMedicines(user_id).entrySet());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            return refillButton;
        }).setHeader("Refill Stock");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        dialog.add(grid);
        dialog.open();
    }

}
