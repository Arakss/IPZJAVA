/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ipz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 *
 * @author Kamil
 */
public class Okno {
    
    private final ObservableList<Projekt> projektData = FXCollections.observableArrayList();
    public ObservableList<Projekt> getProjektData() {
        return projektData;
    }
    private final ObservableList<Sprint> sprintData = FXCollections.observableArrayList();
    public ObservableList<Sprint> getSprintData() {
        return sprintData;
    }
    private final ObservableList<Zadanie> zadanieData = FXCollections.observableArrayList();
    public ObservableList<Zadanie> getZadanieData() {
        return zadanieData;
    }
    
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
    private TableColumn<Sprint, String> sprint;
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
    
    public void Setglowny(IPZ podstawa) throws SQLException {
        this.podstawa=podstawa;
        lista.setItems(getProjektData());
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        rs = st.executeQuery("SELECT * FROM  `uzytkownik` INNER JOIN  `rola` ON  `uzytkownik`.`id_rola` =  `rola`.`id` WHERE  `login` = \""+podstawa.getlogin()+"\"");
        while(rs.next()) { 
            info.setText("Zalogowany jako: "+rs.getString("imie")+" "+rs.getString("nazwisko")+"\nRola: "+rs.getString("nazwa"));
        }
    }
    
    @FXML
    private void pokaz(ActionEvent event) throws Exception  {
    }
    public void initialize() {
        lista.getSelectionModel().selectFirst();
        lista.setCellFactory(new Callback<ListView<Projekt>, ListCell<Projekt>>() {
        @Override
        public ListCell<Projekt> call(ListView<Projekt> param) {
  
            return new ListCell<Projekt>(){
              @Override
                public void updateItem(Projekt item, boolean empty){
                    super.updateItem(item, empty);
                    if(!empty) {
                        setText(item.getnazwa());
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
                setText(item.getnazwa());
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
            sprintData.removeAll(sprintData);
            zadanieData.removeAll(zadanieData);
            tabelaS.setItems(getSprintData());
            tabelaZ.setItems(getZadanieData());
            zadanie.setCellValueFactory(cellData -> cellData.getValue().NazwaProperty());
            czas.setCellValueFactory(cellData -> cellData.getValue().CzasProperty());
            opis.setCellValueFactory(cellData -> cellData.getValue().OpisProperty());
            stan.setCellValueFactory(cellData -> cellData.getValue().StanProperty());
            sprint.setCellValueFactory(cellData -> cellData.getValue().NazwaProperty());
            dataroz.setCellValueFactory(cellData -> cellData.getValue().Data_rozpoczeciaProperty());
            datazak.setCellValueFactory(cellData -> cellData.getValue().Data_zakonczeniaProperty());
            podstawa.setnazwaProjekt(newValue.getnazwa());
            try {
                projekt.setText("Nazwa projektu: "+newValue.getnazwa()+"\nData rozpoczęcia: "+newValue.getdata_rozpoczecia()+" Data zakończenia: "+newValue.getdata_zakonczenia());
                con = DriverManager.getConnection(url, user, password);
                rs = st.executeQuery("SELECT * FROM  `sprint_to_projekt` INNER JOIN  `sprint` ON  `sprint_to_projekt`.`id_sprint` =  `sprint`.`id` INNER JOIN  `projekt` ON  `sprint_to_projekt`.`id_projekt` =  `projekt`.`id` WHERE `projekt`.`nazwa`= \""+newValue.getnazwa()+"\"");
                while(rs.next()) { 
                    sprintData.add(new Sprint(rs.getString("nazwa"),rs.getString("data_rozpoczecia"),rs.getString("data_zakonczenia")));   
                }
                rs = st.executeQuery("SELECT * FROM  `zadanie_to_projekt` INNER JOIN  `zadanie` ON  `zadanie_to_projekt`.`id_zadanie` =  `zadanie`.`id` INNER JOIN `stan` ON `zadanie`.`id_stan` = `stan`.`id` INNER JOIN  `projekt` ON  `zadanie_to_projekt`.`id_projekt` =  `projekt`.`id` WHERE `projekt`.`nazwa`= \""+newValue.getnazwa()+"\"");
                while(rs.next()) { 
                    zadanieData.add(new Zadanie(rs.getString("nazwa"),rs.getString("czas")+" h",rs.getString("opis"),rs.getString("stan.nazwa")));   
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
            }
            sprinton.setDisable(false);
            zadanieon.setDisable(false);
        }
        });
    }  

    @FXML
    private void cofnij(ActionEvent event) throws Exception {
        podstawa.Projekty_uzytkownicy();
    }

    @FXML
    private void dodaj_P(ActionEvent event) throws Exception {
        podstawa.showDialogProjekt();
    }

    @FXML
    private void dodaj_S(ActionEvent event) throws Exception {
        podstawa.showDialogSprint();
    }

    @FXML
    private void dodaj_Z(ActionEvent event) throws Exception {
        podstawa.showDialogZadanie();
    }
}
