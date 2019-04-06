package code;

import java.util.ArrayList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AutomataGrid {
    private GridPane grid;
    private Label lAlfabeto, lEstados, lEstadosFinales, lDelta;
    public TextField tfAlfabeto;
    public ArrayList<Label> labelsEstados;
    public Button masEstados, menosEstados;
    public ArrayList<RadioButton> rbEstadosFinales;
    private Automata automata;
    private Delta delta;
    private Button verDelta;
    public TextField [] transiciones;
    private int contador;
    
    public AutomataGrid(){
        contador = 0;
        
        grid = new GridPane();
        lAlfabeto = new Label("Alfabeto");
        lEstados = new Label("Estados");
        lEstadosFinales = new Label("Estados Finales");
        lDelta = new Label("δ:");
        
        tfAlfabeto = new TextField();
        labelsEstados = new ArrayList<>();
        masEstados = new Button("+");
        menosEstados = new Button("-");
        
        rbEstadosFinales = new ArrayList<>();
        verDelta = new Button("Ver");
        
    }
    
    public GridPane crearGrid(){
        grid.setHgap(5);
        grid.setVgap(5);
        
        grid.add(lAlfabeto, 0, 1);
        grid.add(tfAlfabeto, 1, 1, 12, 1);
        tfAlfabeto.setPromptText("ej: a, b, c");
        tfAlfabeto.getParent().requestFocus();
        
        grid.add(lEstados, 0, 2);
        crearEstado();
        grid.add(masEstados, 14, 2);
        grid.add(menosEstados, 15, 2);
        grid.add(lEstadosFinales, 0, 3);
        
        
        grid.add(lDelta, 0, 4);
        GridPane.setHalignment(lDelta, HPos.RIGHT);
        
        grid.add(new Label("q"), 1, 4);
        grid.add(new Label("σ"), 2, 4);
        Label del = new Label("δ(q,σ)");
        grid.add(del, 3, 4);
        GridPane.setColumnSpan(del, 4);
        grid.add(verDelta, 14, 4);
        
        masEstados.setOnAction(e -> crearEstado());
        menosEstados.setOnAction(e -> removerEstado());
        verDelta.setOnAction(e -> calcularDelta());
        
        
        return grid;
    }
    
    public void crearEstado(){
        if(contador <= 11){ //Limite de estados de 11 para efectos del diseño
            labelsEstados.add(new Label(getNextChar()));
            contador++;
            grid.add(labelsEstados.get(labelsEstados.size()-1), contador, 2, 1, 1);

            crearRadioButton();
        }
    }
    
    private void removerEstado(){
        if(contador != 1){ // No se puede tener un automata con 0 estados
            contador--;
            grid.getChildren().remove(labelsEstados.get(contador));
            labelsEstados.remove(contador);
            removerRadioButton();
        }
    }
    
    public void removerEstado(String e2){
        for (int i = 0; i < labelsEstados.size(); i++) {
            if(labelsEstados.get(i).getText().equals(e2)){
                contador--;
                grid.getChildren().remove(labelsEstados.get(i));
                labelsEstados.remove(i);
                grid.getChildren().remove(rbEstadosFinales.get(i));
                rbEstadosFinales.remove(i);
            }
        }
    }
    
    private String getNextChar(){
        char ch = 'A';
        ch += contador;
        
        return Character.toString(ch);
    }
    
    private String getNextChar(int i){
        char ch = 'A';
        ch += i;
        return Character.toString(ch);
    }
    
    private void crearRadioButton(){
        rbEstadosFinales.add(new RadioButton());
        grid.add(rbEstadosFinales.get(contador-1), contador, 3,1,1);
    }
    
    private void removerRadioButton(){
        grid.getChildren().remove(rbEstadosFinales.get(contador));
        rbEstadosFinales.remove(contador);
    }
    
    public void calcularDelta(){
        if(tfAlfabeto.getText() != null){
            char [] estados = getEstadosDelta();
            String [] alfabeto = getAlfabetoDelta();

            transiciones = new TextField[labelsEstados.size()*getAlfabeto().length];
            for(int i = 0; i< transiciones.length; i++){
                transiciones[i] = new TextField();
                transiciones[i].setMaxWidth(45);


                grid.add(new Label(String.valueOf(estados[i])), 1, i+5);
                grid.add(new Label(alfabeto[i]), 2, i+5);
                grid.add(transiciones[i], 3, i+5, 2, 1);
            }
        }
        
    }
    
    public char [] getEstados(){
        char [] estados = new char[labelsEstados.size()];
        for(int i = 0; i < labelsEstados.size(); i++){
            estados[i] = (char)(labelsEstados.get(i).getText().toCharArray()[0]);
        }
        return estados;
    }
    
    
    public char [] getEstadosDelta(){ // q0, q1, q2 pero solamente los enteros
        int nEstados = labelsEstados.size();
        int nAlfabeto = getAlfabeto().length;
        char [] arregloEstados = new char[nEstados*nAlfabeto];
        
        int renglon = 0;
        for(int g = 0; g<nEstados; g++){
            for(int i = 0; i < nAlfabeto; i++){
                arregloEstados[renglon] = (char)(labelsEstados.get(g).getText().toCharArray()[0]);
                renglon++;
            }
            
        }
        
        return arregloEstados;
    }
    
    public String [] getAlfabeto(){ //Alfabeto real
        String alfabetoTexto = tfAlfabeto.getText();
        alfabetoTexto.replaceAll(" ", "");
        
        return alfabetoTexto.split(",");
    }
    
    public String [] getAlfabetoDelta(){ //Alfabeto listo para entregar a Delta
        String [] alfabeto = getAlfabeto();
        String [] alfa = new String[alfabeto.length*labelsEstados.size()];
        
        for(int i = 0; i < alfa.length;){
            for(String simbolo: alfabeto){
                alfa[i] = simbolo;
                i++;
            }
        }
        
        return alfa;
    }
    
    private boolean [] getEstadosFinales(){
        boolean [] estados = new boolean[rbEstadosFinales.size()];
        for(int i = 0; i < rbEstadosFinales.size(); i++){
            estados[i] = (rbEstadosFinales.get(i).isSelected())? true: false;
        }
        return estados;
    }
    
    public char [] getTransiciones(){
        char [] transicion = new char[transiciones.length];
        
        for(int i = 0; i < transicion.length; i++){
            transicion[i] = transiciones[i].getText().toCharArray()[0];
        }
        return transicion;
    }
    
    public Automata getAutomata(){
        String [] alfabeto = getAlfabeto();
        char [] estados = getEstados();
        boolean [] estadosFinales = getEstadosFinales();
        delta = new Delta(getEstadosDelta(), getAlfabetoDelta(), getTransiciones());
        
        automata = new Automata(alfabeto, estados, estadosFinales, 0, delta);
        return automata;
    }
    
    
    
}
