package com.yugabyte.com.views.main;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.yugabyte.com.PointOfInterest;
import com.yugabyte.com.TripsAdvisorService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@PageTitle("TripOnBudget")
@Route("")
public class PointsOfInterestView extends VerticalLayout {

    private final TripsAdvisorService tripsAdvisorService;
    private final Grid<PointOfInterest> grid;
    private final Binder<SearchCriteria> binder;

    public PointsOfInterestView(TripsAdvisorService tripsAdvisorService) {
        this.tripsAdvisorService = tripsAdvisorService;

        UI.getCurrent().getElement().setAttribute("theme", Lumo.DARK);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setAlignItems(Alignment.CENTER);

        Label title = new Label("Trip On Budget");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("font-size", "20px");

        titleLayout.add(title);

        // Create the binder for the search criteria
        binder = new Binder<>(SearchCriteria.class);

        HorizontalLayout userInputLayout = new HorizontalLayout();

        // Create the text fields for the search criteria
        TextField cityField = new TextField("City you want to go to:");
        cityField.setWidth("300px");
        cityField.getStyle().set("margin-right", "10px");
        binder.forField(cityField)
                .asRequired("City name is required")
                .bind(SearchCriteria::getCity, SearchCriteria::setCity);

        IntegerField budgetField = new IntegerField("Your budget in dollars");
        budgetField.setWidth("300px");
        budgetField.getStyle().set("margin-right", "10px");
        binder.forField(budgetField)
                .asRequired("Budget is required")
                .withValidator(budget -> budget > 0, "Budget must be greater than zero")
                .bind(SearchCriteria::getBudget, SearchCriteria::setBudget);

        userInputLayout.add(cityField, budgetField);

        // Create the search button
        Button searchButton = new Button("Show me places!");
        searchButton.getStyle().set("margin-top", "10px");
        searchButton.addClickListener(e -> searchPointsOfInterest());

        add(titleLayout, userInputLayout, searchButton);

        // Create the grid to display the points of interest
        grid = new Grid<>();
        grid.addColumn(PointOfInterest::getName).setHeader("Place").setFlexGrow(1);
        grid.addColumn(PointOfInterest::getInfo).setHeader("Info").setFlexGrow(2);
        grid.addColumn(this::renderCost).setHeader("Price").setFlexGrow(0);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        add(grid);
    }

    private String renderCost(PointOfInterest pointOfInterest) {
        String formattedCost = NumberFormat.getCurrencyInstance(Locale.US).format(pointOfInterest.getCost());
        return formattedCost.replaceAll("\\.00", "");
    }

    private void searchPointsOfInterest() {
        // Bind the search criteria to the binder
        SearchCriteria searchCriteria = new SearchCriteria();

        if (binder.writeBeanIfValid(searchCriteria)) {
            // Call the suggestPointsOfInterest method and update the grid with the results
            Optional<List<PointOfInterest>> optionalPointsOfInterest = tripsAdvisorService
                    .suggestPointsOfInterest(searchCriteria.getCity(), searchCriteria.getBudget());
            if (optionalPointsOfInterest.isPresent()) {
                List<PointOfInterest> pointsOfInterest = optionalPointsOfInterest.get();
                grid.setItems(pointsOfInterest);
            }
        }
    }

    private static class SearchCriteria {
        private String city;
        private int budget;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getBudget() {
            return budget;
        }

        public void setBudget(int budget) {
            this.budget = budget;
        }
    }

}
