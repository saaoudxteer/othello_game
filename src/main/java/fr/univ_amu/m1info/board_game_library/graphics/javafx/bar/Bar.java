package fr.univ_amu.m1info.board_game_library.graphics.javafx.bar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class Bar extends VBox {
    private final Map<String, Labeled> labeledElements = new HashMap<>();
    private final Map<String, Button> buttons = new HashMap<>();

    private final HBox buttonsRow = new HBox();
    private final HBox labelsRow = new HBox();

    public Bar() {
        super();
        setSpacing(2);
        setPadding(new Insets(2));

        buttonsRow.setSpacing(10);
        buttonsRow.setAlignment(Pos.CENTER_LEFT);

        labelsRow.setSpacing(20);
        labelsRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(buttonsRow, labelsRow);
    }

    public synchronized void addLabel(String id, String initialText){
        if(labeledElements.containsKey(id)){
            return;
        }
        Label label = new Label(initialText);
        label.setAlignment(Pos.BASELINE_LEFT);
        labeledElements.put(id, label);
        labelsRow.getChildren().add(label);
    }

    public synchronized void removeLabeledElement(String id){
        if(labeledElements.containsKey(id)){
            Labeled labeled = labeledElements.get(id);
            buttonsRow.getChildren().remove(labeled);
            labelsRow.getChildren().remove(labeled);
            labeledElements.remove(id);
            buttons.remove(id);
        }
    }

    public void setButtonAction(String id, ButtonActionOnClick buttonActionOnClick){
        if(!buttons.containsKey(id)){
            throw new IllegalArgumentException("Button " + id + " does not exist");
        }
        buttons.get(id).setOnAction(_ -> buttonActionOnClick.onClick());
    }

    public synchronized void addButton(String id, String label){
        Button button = new Button(label);
        // Laisser les boutons afficher tout leur texte
        button.setMinWidth(90);
        labeledElements.put(id, button);
        buttons.put(id, button);
        buttonsRow.getChildren().add(button);
    }

    public void updateLabel(String id, String newText){
        if(labeledElements.containsKey(id)){
            labeledElements.get(id).setText(newText);
        }
    }
}
