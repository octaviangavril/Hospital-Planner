package com.example.application.views;

import com.example.application.data.entity.User;
import com.example.application.data.repository.Repository;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Hospital")
public class MainView extends AppLayout {

    public MainView() {
        createHeader();
    }

    private void createHeader() {
        H1 title = new H1("'Dr. C. I. Parhon' hospital Iasi");
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);
        Image logo = new Image("images/header/logo.png","Logo");
        logo.setHeight("2rem");
        var header = new HorizontalLayout( logo, title);


        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding-inline","2rem");
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);
        addToNavbar(header);
    }
}
