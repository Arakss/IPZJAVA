/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ipz;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Kamil
 */
public class Okno implements Initializable {
    
    private final ObservableList<Projekt> projektData = FXCollections.observableArrayList();
    public ObservableList<Projekt> getProjektData() {
        return projektData;
    }
    private final ObservableList<Sprint> sprintData = FXCollections.observableArrayList();
    public ObservableList<Sprint> getSprintData() {
        return sprintData;
    }
    private final ObservableList<Zadanie> zadanieZaData = FXCollections.observableArrayList();
    public ObservableList<Zadanie> getZadanieZaData() {
        return zadanieZaData;
    }
    private final ObservableList<Zadanie> zadanieDwData = FXCollections.observableArrayList();
    public ObservableList<Zadanie> getZadanieDwData() {
        return zadanieDwData;
    }
    private final ObservableList<Zadanie> zadanieWtData = FXCollections.observableArrayList();
    public ObservableList<Zadanie> getZadanieWtData() {
        return zadanieWtData;
    }
    private final ObservableList<Zadanie> zadanieData = FXCollections.observableArrayList();
    public ObservableList<Zadanie> getZadanieData() {
        return zadanieData;
    }
    
    @FXML
    private Button pokaz;
    @FXML
    private TableColumn<Zadanie, String> czas;
    @FXML
    private TableColumn<Zadanie, String> opis;
    @FXML
    private TableColumn<Zadanie, String> stan;
    @FXML
    private TableView<Sprint> tabelaS;
    @FXML
    private TableView<Zadanie> tabelaZ;
    @FXML
    private TableColumn<Zadanie, String> zadanie;
    @FXML
    private Label projekt;
    @FXML
    private TableColumn<Sprint, String> dataroz;
    @FXML
    private TableColumn<Sprint, String> datazak;
    @FXML
    private Label info;
    @FXML
    private ComboBox<Projekt> lista;
    @FXML
    private Button sprinton;
    @FXML
    private Button zadanieon;
    @FXML
    private TableColumn<Sprint, String> sprintS;
    @FXML
    private TableColumn<Zadanie, String> sprintZ;
    @FXML
    private CheckBox stan1;
    @FXML
    private CheckBox stan2;
    @FXML
    private CheckBox stan3;
    
    private IPZ podstawa;
           
    private Connection con = null;
    private Statement st = null;
    private ResultSet rs = null;

    private final String url = "jdbc:mysql://mysql8.db4free.net:3307/ipzdb?characterEncoding=UTF-8&useSSL=false";
    private final String user = "ipzuser";
    private final String password = "ipzpassword";
    
    public Okno() throws SQLException {
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        rs = st.executeQuery("SELECT * FROM  `projekt`");
        while(rs.next()) { 
            projektData.add(new Projekt(rs.getString("nazwa"), rs.getString("data_rozpoczecia"), rs.getString("data_zakonczenia"))); 
        }
    }
    
    public void setGlowny(IPZ podstawa) throws SQLException {
        this.podstawa=podstawa;
        lista.setItems(getProjektData());
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        rs = st.executeQuery("SELECT * FROM  `uzytkownik` INNER JOIN  `rola` ON  `uzytkownik`.`id_rola` =  `rola`.`id` WHERE  `login` = \""+podstawa.getLogin()+"\"");
        while(rs.next()) { 
            info.setText("Zalogowany jako: "+rs.getString("imie")+" "+rs.getString("nazwisko")+"\nRola: "+rs.getString("nazwa"));
        }
        if(podstawa.getProjekt()!=null)
        {
            lista.getSelectionModel().select(podstawa.getProjekt());
        }
    }

    @FXML
    private void pokaz(ActionEvent event) throws Exception  {
        podstawa.Okno_osob_projekt();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL urlx, ResourceBundle rb) {
        // TODO
        lista.setCellFactory(new Callback<ListView<Projekt>, ListCell<Projekt>>() {
        @Override
        public ListCell<Projekt> call(ListView<Projekt> param) {
  
            return new ListCell<Projekt>(){
              @Override
                public void updateItem(Projekt item, boolean empty){
                    super.updateItem(item, empty);
                    if(!empty) {
                        setText(item.getNazwa());
                        setGraphic(null);
                    } 
                    else 
                    {
                        setText(null);
                    }
                }
           };
        }
        });
        lista.setButtonCell(
        new ListCell<Projekt>() {
        @Override
        public void updateItem(Projekt item, boolean empty) {
            super.updateItem(item, empty); 
            if(!empty) {
                setText(item.getNazwa());
            } 
            else 
            {
                setText(null);
            }
        }
        });
        lista.valueProperty().addListener(new ChangeListener<Projekt>() {
        @Override
        public void changed(ObservableValue ov, Projekt oldValue, Projekt newValue)  {
            podstawa.setProjekt(newValue);
            sprintData.removeAll(sprintData);
            zadanieData.removeAll(zadanieData);
            zadanieDwData.removeAll(zadanieDwData);
            zadanieZaData.removeAll(zadanieZaData);
            zadanieWtData.removeAll(zadanieWtData);
            tabelaS.setItems(getSprintData());
            tabelaZ.setItems(getZadanieData());
            zadanie.setCellValueFactory(cellData -> cellData.getValue().NazwaProperty());
            czas.setCellValueFactory(cellData -> cellData.getValue().CzasProperty());
            opis.setCellValueFactory(cellData -> cellData.getValue().OpisProperty());
            stan.setCellValueFactory(cellData -> cellData.getValue().StanProperty());
            sprintS.setCellValueFactory(cellData -> cellData.getValue().NazwaProperty());
            sprintZ.setCellValueFactory(cellData -> cellData.getValue().SprintProperty());
            dataroz.setCellValueFactory(cellData -> cellData.getValue().Data_rozpoczeciaProperty());
            datazak.setCellValueFactory(cellData -> cellData.getValue().Data_zakonczeniaProperty());
            podstawa.setNazwaProjekt(newValue.getNazwa());
            try {
                projekt.setText("Nazwa projektu: "+newValue.getNazwa()+"\nData rozpoczęcia: "+newValue.getData_rozpoczecia()+" Data zakończenia: "+newValue.getData_zakonczenia());
                con = DriverManager.getConnection(url, user, password);
                rs = st.executeQuery("SELECT * FROM  `sprint_to_projekt` INNER JOIN  `sprint` ON  `sprint_to_projekt`.`id_sprint` =  `sprint`.`id` INNER JOIN  `projekt` ON  `sprint_to_projekt`.`id_projekt` =  `projekt`.`id` WHERE `projekt`.`nazwa`= \""+newValue.getNazwa()+"\"");
                while(rs.next()) { 
                    sprintData.add(new Sprint(rs.getString("nazwa"),rs.getString("data_rozpoczecia"),rs.getString("data_zakonczenia")));   
                }
                rs = st.executeQuery("SELECT * FROM `zadanie_to_projekt` LEFT OUTER JOIN `zadanie_to_sprint` ON `zadanie_to_projekt`.`id_zadanie`= `zadanie_to_sprint`.`id_zadanie` LEFT OUTER JOIN `sprint` ON `zadanie_to_sprint`.`id_sprint` = `sprint`.`id` INNER JOIN `zadanie` ON `zadanie_to_projekt`.`id_zadanie` = `zadanie`.`id` INNER JOIN `stan` ON `zadanie`.`id_stan` = `stan`.`id` AND `stan`.`nazwa` = \""+stan3.getText()+"\" INNER JOIN `projekt` ON `zadanie_to_projekt`.`id_projekt` = `projekt`.`id` WHERE `projekt`.`nazwa` = \""+newValue.getNazwa()+"\"");
                while(rs.next()) { 
                    zadanieZaData.add(new Zadanie(rs.getString("zadanie.nazwa"),rs.getString("czas")+" h",rs.getString("opis"),rs.getString("opis_dlugi"),rs.getString("stan.nazwa"),rs.getString("sprint.nazwa")));   
                } 
                rs = st.executeQuery("SELECT * FROM `zadanie_to_projekt` LEFT OUTER JOIN `zadanie_to_sprint` ON `zadanie_to_projekt`.`id_zadanie`= `zadanie_to_sprint`.`id_zadanie` LEFT OUTER JOIN `sprint` ON `zadanie_to_sprint`.`id_sprint` = `sprint`.`id` INNER JOIN `zadanie` ON `zadanie_to_projekt`.`id_zadanie` = `zadanie`.`id` INNER JOIN `stan` ON `zadanie`.`id_stan` = `stan`.`id` AND `stan`.`nazwa` = \""+stan2.getText()+"\" INNER JOIN `projekt` ON `zadanie_to_projekt`.`id_projekt` = `projekt`.`id` WHERE `projekt`.`nazwa` = \""+newValue.getNazwa()+"\"");
                while(rs.next()) { 
                    zadanieWtData.add(new Zadanie(rs.getString("zadanie.nazwa"),rs.getString("czas")+" h",rs.getString("opis"),rs.getString("opis_dlugi"),rs.getString("stan.nazwa"),rs.getString("sprint.nazwa")));   
                } 
                rs = st.executeQuery("SELECT * FROM `zadanie_to_projekt` LEFT OUTER JOIN `zadanie_to_sprint` ON `zadanie_to_projekt`.`id_zadanie`= `zadanie_to_sprint`.`id_zadanie` LEFT OUTER JOIN `sprint` ON `zadanie_to_sprint`.`id_sprint` = `sprint`.`id` INNER JOIN `zadanie` ON `zadanie_to_projekt`.`id_zadanie` = `zadanie`.`id` INNER JOIN `stan` ON `zadanie`.`id_stan` = `stan`.`id` AND `stan`.`nazwa` = \""+stan1.getText()+"\" INNER JOIN `projekt` ON `zadanie_to_projekt`.`id_projekt` = `projekt`.`id` WHERE `projekt`.`nazwa` = \""+newValue.getNazwa()+"\"");
                while(rs.next()) { 
                    zadanieDwData.add(new Zadanie(rs.getString("zadanie.nazwa"),rs.getString("czas")+" h",rs.getString("opis"),rs.getString("opis_dlugi"),rs.getString("stan.nazwa"),rs.getString("sprint.nazwa")));   
                } 
//                rs = st.executeQuery("SELECT * FROM `zadanie_to_projekt` LEFT OUTER JOIN `zadanie_to_sprint` ON `zadanie_to_projekt`.`id_zadanie`= `zadanie_to_sprint`.`id_zadanie` LEFT OUTER JOIN `sprint` ON `zadanie_to_sprint`.`id_sprint` = `sprint`.`id` INNER JOIN `zadanie` ON `zadanie_to_projekt`.`id_zadanie` = `zadanie`.`id` INNER JOIN `stan` ON `zadanie`.`id_stan` = `stan`.`id` INNER JOIN `projekt` ON `zadanie_to_projekt`.`id_projekt` = `projekt`.`id` WHERE `projekt`.`nazwa` = \""+newValue.getNazwa()+"\"");
//                while(rs.next()) { 
//                    zadanieData.add(new Zadanie(rs.getString("zadanie.nazwa"),rs.getString("czas")+" h",rs.getString("opis"),rs.getString("opis_dlugi"),rs.getString("stan.nazwa"),rs.getString("sprint.nazwa")));   
//                }
            } catch (SQLException ex) {
                Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
            }
            zadanieData.addAll(zadanieDwData);
            zadanieData.addAll(zadanieZaData);
            zadanieData.addAll(zadanieWtData);
            sprinton.setDisable(false);
            zadanieon.setDisable(false);
            pokaz.setDisable(false);
            stan1.setDisable(false);
            stan2.setDisable(false);
            stan3.setDisable(false);
            stan1.setSelected(true);
            stan2.setSelected(true);
            stan3.setSelected(true);
        }
        });
    }  

    @FXML
    private void cofnij(ActionEvent event) throws Exception { 
        podstawa.Projekty_uzytkownicy();
    }

    @FXML
    private void dodaj_P(ActionEvent event) throws Exception {
        Projekt tempProjekt = new Projekt();
        boolean okClicked = podstawa.showDialogProjekt(tempProjekt);
        if (okClicked) {
            getProjektData().add(tempProjekt);
        }
    }

    @FXML
    private void dodaj_S(ActionEvent event) throws Exception {
        Sprint tempSprint = new Sprint();
        boolean okClicked = podstawa.showDialogSprint(tempSprint);
        if (okClicked) {
            getSprintData().add(tempSprint);
        }
    }

    @FXML
    private void dodaj_Z(ActionEvent event) throws Exception {
        Zadanie tempZadanie = new Zadanie();
        boolean okClicked = podstawa.showDialogZadanie(tempZadanie);
        if (okClicked) {
            getZadanieData().add(tempZadanie);
        }
    }

    @FXML
    private void zadaniatosprint(MouseEvent event) throws Exception {
        if (event.getClickCount() == 2 && event.isPrimaryButtonDown() && tabelaS.getSelectionModel().getSelectedItem()!=null) {
            podstawa.setNazwaSprint(tabelaS.getSelectionModel().getSelectedItem().NazwaProperty().get());
            podstawa.Zadania_sprinty();
        }
    }
    
    @FXML
    private void zadanietozadanie(MouseEvent event) throws Exception {
        if (event.getClickCount() == 2 && event.isPrimaryButtonDown() && tabelaZ.getSelectionModel().getSelectedItem()!=null) {       
            podstawa.setNazwaZadanie(tabelaZ.getSelectionModel().getSelectedItem().NazwaProperty().get());
            podstawa.ZadanietoZadanie();
        }
    }
    
    @FXML
    private void stany(ActionEvent event) {
        if(stan1.isSelected() && stan2.isSelected() && stan3.isSelected()) {
            getZadanieData().removeAll(getZadanieDwData());
            getZadanieData().removeAll(getZadanieWtData());
            getZadanieData().removeAll(getZadanieZaData());
            getZadanieData().addAll(getZadanieDwData());
            getZadanieData().addAll(getZadanieWtData());
            getZadanieData().addAll(getZadanieZaData());
        }
        else if(stan1.isSelected() && stan2.isSelected()) {
            getZadanieData().removeAll(getZadanieDwData());
            getZadanieData().removeAll(getZadanieWtData());
            getZadanieData().addAll(getZadanieDwData());
            getZadanieData().addAll(getZadanieWtData());
        }
        else if(stan2.isSelected() && stan3.isSelected()) {
            getZadanieData().removeAll(getZadanieWtData());
            getZadanieData().removeAll(getZadanieZaData());
            getZadanieData().addAll(getZadanieZaData());
            getZadanieData().addAll(getZadanieWtData());
        }
        else if(stan1.isSelected() && stan3.isSelected()) {
            getZadanieData().removeAll(getZadanieDwData());
            getZadanieData().removeAll(getZadanieZaData());
            getZadanieData().addAll(getZadanieDwData());
            getZadanieData().addAll(getZadanieZaData());
        }
        else if(stan1.isSelected()) {
            getZadanieData().removeAll(getZadanieDwData());
            getZadanieData().addAll(getZadanieDwData());
        }
        else if(stan2.isSelected()) {
            getZadanieData().removeAll(getZadanieWtData());
            getZadanieData().addAll(getZadanieWtData());
        }
        else if(stan3.isSelected()) {
            getZadanieData().removeAll(getZadanieZaData());
            getZadanieData().addAll(getZadanieZaData());
        }
        
        if(!stan1.isSelected()) {
            getZadanieData().removeAll(getZadanieDwData());
        } 
        if(!stan2.isSelected()) {
            getZadanieData().removeAll(getZadanieWtData());
        }
        if(!stan3.isSelected()) {
            getZadanieData().removeAll(getZadanieZaData());
        } 
    }
}
