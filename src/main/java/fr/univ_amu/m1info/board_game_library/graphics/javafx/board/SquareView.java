package fr.univ_amu.m1info.board_game_library.graphics.javafx.board;

import fr.univ_amu.m1info.board_game_library.graphics.Shape;
import fr.univ_amu.m1info.board_game_library.graphics.javafx.color.JavaFXColorMapper;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SquareView extends StackPane {
    private final int column;
    private final int row;
    private final Rectangle squareBackground;
    private final Group shapes = new Group();
    private final int squareSize;


    public SquareView(int column, int row, int squareSize) {
        this.column = column;
        this.row = row;
        this.squareSize = squareSize;
        setWidth(squareSize);
        setHeight(squareSize);
        this.squareBackground = new Rectangle(squareSize, squareSize);
        this.getChildren().add(squareBackground);
        this.getChildren().add(shapes);
        squareBackground.setStroke(Color.BLACK);
        setColor(Color.WHITE);
    }

    public void setColor(Color backgroundColor) {
        squareBackground.setFill(backgroundColor);
    }

    public void setAction(BoardActionOnClick positionHandler) {
        this.setOnMouseClicked(_ -> positionHandler.onClick(this.row, this.column));
    }

    void addShape(Shape shape, fr.univ_amu.m1info.board_game_library.graphics.Color color){
        javafx.scene.shape.Shape shapeFX = ShapeFactory.makeShape(shape, squareSize);
        shapeFX.setFill(JavaFXColorMapper.getJavaFXColor(color));
        // Special-case suggestion shapes: make them semi-transparent 
        if (shape == fr.univ_amu.m1info.board_game_library.graphics.Shape.SUGGESTION) {
            shapeFX.setOpacity(0.5);
            shapeFX.setMouseTransparent(true);
            // mark so we can remove suggestion shapes without touching pieces
            shapeFX.setId("suggestion");
        }
        shapes.getChildren().add(shapeFX);
    }

    public void removeShapes() {
        shapes.getChildren().clear();
    }

    /**
     * Remove only suggestion overlay shapes from this square, keeping piece shapes.
     */
    public void removeSuggestionShapes() {
        // removeIf : supprime tous les enfants du groupe shapes dont l'id est "suggestion"
        // Autrement dit, on parcourt tous les éléments graphiques de la case,
        // et on retire uniquement ceux qui ont été marqués comme suggestion (les overlays d'aide).
        // Cela permet de garder les vrais pions et d'effacer seulement les ronds d'aide.
        shapes.getChildren().removeIf(n -> "suggestion".equals(n.getId()));
    }
}
