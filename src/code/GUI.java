package code;

import java.util.ArrayList;
import java.util.stream.Stream;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUI{
    private ScrollPane scroll;
    private VBox vb, vbResultados, vbNuevoGrid;
    private HBox hb, hbComparacion;
    private Automata automata;
    private AutomataGrid ag;
    private GridPane grid;
    private Button reducir, elegirEstados;
    private GridPane tabla;
    private String [] alfabeto;
    private ComboBox cbEstado1, cbEstado2;
    
    
    public GUI(){
        scroll = new ScrollPane();
        vb = new VBox();
        hb = new HBox();
        
        ag = new AutomataGrid();
        grid = ag.crearGrid();
        
        elegirEstados = new Button("Elegir Estados");
    }
    
    public void launch(Stage escenario){
        Scene escena = new Scene(crearScrollPaneHBox(), 800, 600);
        escena.getStylesheets().add(GUI.class.getResource("mainScene.css").toExternalForm());
        escenario.setScene(escena);
        escenario.setTitle("Reductor de Estados en un Automata");
        escenario.show();
    }
    
    private ScrollPane crearScrollPaneHBox(){
        scroll.setContent(vb);
        
        Text letreroPresentacion = new Text("Programa para reducir los estados "
                + "seleccionados de un automata si son compatibles");
        letreroPresentacion.setId("presentacion");
        
        vb.getChildren().add(letreroPresentacion);
        
        Text letreroOriginal = new Text("Automata Original");
        letreroOriginal.setFont(new Font("Arial", 20));
        letreroOriginal.setFill(Color.web("#BA55D3"));
        
        vb.getChildren().add(letreroOriginal);
        vb.getChildren().add(crearHBox());
        vb.getChildren().add(elegirEstados);
        elegirEstados.setOnAction(e -> crearEntradaComparacion());
        
        vb.setSpacing(10);
        vb.setAlignment(Pos.CENTER_LEFT);
        return scroll;
    }
    
    private HBox crearHBox(){
        hb.getChildren().add(grid);
        hb.setAlignment(Pos.CENTER);
        hb.setSpacing(30);
        
        return hb;
    }
    
    private void crearEntradaComparacion(){
        
        automata = ag.getAutomata();
        
        if(hbComparacion == null){
            hbComparacion = new HBox();
            vb.getChildren().add(hbComparacion);
        } else {
            hbComparacion = new HBox();
            vbNuevoGrid = new VBox();
            //vbNuevoGrid.getChildren().add(hbComparacion);
        }
        
        cbEstado1 = new ComboBox();
        cbEstado2 = new ComboBox();
        reducir = new Button("Comparar y reducir");
        
        reducir.setOnAction(e -> agregarComparacion());
        
        hbComparacion.setAlignment(Pos.CENTER_LEFT);
        hbComparacion.setSpacing(10);
        hbComparacion.getChildren().addAll(cbEstado1, cbEstado2, reducir);
        
        cbEstado1.getItems().addAll((Object[])charArrayToStringArray(automata.getEstados()));
        cbEstado2.getItems().addAll((Object[])charArrayToStringArray(automata.getEstados()));
        
        
    }
    
    private String[] charArrayToStringArray(char [] ch){
        String [] s = new String[ch.length];
        for (int i = 0; i < ch.length; i++) {
            s[i] = String.valueOf(ch[i]);
        }
        return s;
    }
    
    private void agregarComparacion(){
        String e1 = cbEstado1.getValue().toString();
        String e2 = cbEstado2.getValue().toString();
        
        vbResultados = new VBox();
        if(e1 != null && e2 != null && !e1.equals(e2)){
            alfabeto = automata.getAlfabeto();

            tabla = new GridPane();
            tabla.setHgap(15);
            tabla.getChildren().clear();
            hb.getChildren().add(vbResultados);

            tabla.add(new Label(subscript("M1M2")), 0, 0);
            for(int i = 0; i < alfabeto.length; i++){
                tabla.add(new Label(alfabeto[i]), i+1, 0);
            }
            comparar(e1, e2);
        
        }
    }
    
    private void comparar(String e1, String e2){
        Delta delta = automata.getDelta();
        
        boolean [] eF = automata.getEstadosFinales();
        char [] t = delta.getΔ();
        
        ArrayList<Label> m1m2 = new ArrayList();
        
        vbResultados.getChildren().add(tabla);
        m1m2.add(new Label(e1 + e2 + "'"));
        tabla.add(m1m2.get(0), 0, 1);
        
        Label notificacion = new Label("Los estados son equivalentes");
        notificacion.setTextFill(Color.BLUE);
        
        char transicion1, transicion2;
        String sTrans1, sTrans2;
        int qProveniente1, qProveniente2;
        Label label;
        boolean terminacionExitosa = true;
        for(int i = 0; i < m1m2.size(); i++){ // i - renglon de m1m2 en el que vamos
                //Estados de donde proviene la transicion
                qProveniente1 = m1m2.get(i).getText().toCharArray()[0] - 65;//65 = char 'A'
                qProveniente2 = m1m2.get(i).getText().toCharArray()[1] - 65;
                if(eF[qProveniente1] != eF[qProveniente2]){
                    terminacionExitosa = false;
                    break;
                }
            for(int k = 0; k < alfabeto.length; k++){ // k - columna del alfabeto en la que vamos
                
                transicion1  = t[qProveniente1 * alfabeto.length + k] ; //posicion en estadosDelta
                transicion2  = t[qProveniente2 * alfabeto.length + k] ; //posicion en estadosDelta
                
                sTrans1 = Character.toString(transicion1);
                sTrans2 = Character.toString(transicion2);
                
                label = new Label(sTrans1 + sTrans2 + "'");
                
                tabla.add(new Label(sTrans1 + sTrans2 + "'"), k+1, i+1);
                if(!contieneLabel(m1m2, label)){
                    m1m2.add(label);
                    tabla.add(new Label(sTrans1 + sTrans2 + "'"), 0, m1m2.size());
                }
            }
        }
        if(terminacionExitosa){
            vbResultados.getChildren().add(notificacion);
            agregarNuevoAutomata();
        }else {
            notificacion.setText("Los estados no son equivalentes");
            vbResultados.getChildren().add(notificacion);
            notificacion.setTextFill(Color.RED);
        }
    }
    
    private void agregarNuevoAutomata(){
        AutomataGrid ag2 = new AutomataGrid();
        
        grid = ag2.crearGrid();
        
        String [] alfabeto = ag.getAlfabeto();
        //Escribir el alfabeto del automata anterior
        ag2.tfAlfabeto.setEditable(false);
        ag2.tfAlfabeto.setText(ag.tfAlfabeto.getText()); 
        
        //Escribir los estados del anterior menos el estado elegido 2 (del ComboBox) que se va a eliminar
        for (int i = 0; i < ag.getEstados().length -1; i++) {
            ag2.crearEstado();
        }
        ag2.removerEstado(cbEstado2.getValue().toString());
        //desarmar los botones no necesarios
        ag2.masEstados.setDisable(true);
        ag2.menosEstados.setDisable(true);
        
        for (int i = 0; i < ag2.labelsEstados.size(); i++) {
            if(!ag2.labelsEstados.get(i).getText().equals(ag.labelsEstados.get(i).getText()))
                for (int j = 0; j < ag.labelsEstados.size(); j++) {
                    if(ag2.labelsEstados.get(i).getText().equals(ag.labelsEstados.get(j).getText())){
                        if(ag.rbEstadosFinales.get(j).isSelected())
                            ag2.rbEstadosFinales.get(i).setSelected(true);
                        break;
                    }
                }
            else if(ag.rbEstadosFinales.get(i).isSelected()) ag2.rbEstadosFinales.get(i).setSelected(true);
            ag2.rbEstadosFinales.get(i).setDisable(true);
        }
        
        //Escribir las transiciones con los nuevos estados redirigiendo la llamada al estado elegido 2 al 1
        ag2.calcularDelta();
        String trans = "";
        String [] estadosD1 = charArrayToStringArray(ag.getEstadosDelta());
        String [] estadosD2 = charArrayToStringArray(ag2.getEstadosDelta());
        for (int i = 0; i < ag2.transiciones.length; i++) {
            ag2.transiciones[i].setEditable(false);
            
            if(estadosD2[i].equals(estadosD1[i]))
                trans = ag.transiciones[i].getText();
            else for (int j = 0; j < estadosD1.length; j++) {
                    if(estadosD2[i].equals(estadosD1[j])){
                        trans = ag.transiciones[j+ (i%alfabeto.length)].getText(); 
                            // mod por la letra del alfabeto en la que vamos
                        break;
                    }
                    
                }
            
            if(!trans.equals(cbEstado2.getValue().toString()))
                ag2.transiciones[i].setText(trans);
            else ag2.transiciones[i].setText(cbEstado1.getValue().toString());
        }
        
        ag = ag2;
        crearEntradaComparacion();
        
        Text nuevoAutomataText = new Text("Nuevo Automata");
        nuevoAutomataText.setFill(Color.CRIMSON);
        nuevoAutomataText.setFont(Font.font("Helvetica", 20));
        
        vbNuevoGrid.getChildren().add(0, nuevoAutomataText);
        vbNuevoGrid.getChildren().add(1, grid);
        hb.getChildren().add(vbNuevoGrid);
    }
    
    private boolean contieneLabel(ArrayList<Label> m1m2, Label label){
        for(Label l: m1m2){
            if(l.getText().equals(label.getText())) return true;
        }
        return false;
    }
    
    public static String superscript(String str) {
        str = str.replaceAll("0", "⁰");
        str = str.replaceAll("1", "¹");
        str = str.replaceAll("2", "²");
        str = str.replaceAll("3", "³");
        str = str.replaceAll("4", "⁴");
        str = str.replaceAll("5", "⁵");
        str = str.replaceAll("6", "⁶");
        str = str.replaceAll("7", "⁷");
        str = str.replaceAll("8", "⁸");
        str = str.replaceAll("9", "⁹");         
        return str;
}

    public static String subscript(String str) {
        str = str.replaceAll("0", "₀");
        str = str.replaceAll("1", "₁");
        str = str.replaceAll("2", "₂");
        str = str.replaceAll("3", "₃");
        str = str.replaceAll("4", "₄");
        str = str.replaceAll("5", "₅");
        str = str.replaceAll("6", "₆");
        str = str.replaceAll("7", "₇");
        str = str.replaceAll("8", "₈");
        str = str.replaceAll("9", "₉");
        return str;
    }
}
