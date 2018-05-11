package gralog.gralogfx.panels;

import gralog.structure.Highlights;
import gralog.structure.Structure;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.dockfx.DockNode;

public class PluginControlPanel extends Pane implements GralogWindow{

    private ProgressBar pb;
    private Button pause,play,step;

    public PluginControlPanel(){
        setMinWidth(100);

        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.prefWidthProperty().bind(this.widthProperty());
        vbox.prefWidthProperty().bind(this.widthProperty());


        play = createButton("\u25B6");


        step = createButton(">>");


        pause = createButton("||");

        pb = new ProgressBar(0f);

        pb.prefWidthProperty().bind(vbox.widthProperty());
        pb.setPrefHeight(20);

        hbox.getChildren().addAll(pause,play,step,pb);
        vbox.getChildren().addAll(hbox, pb);

        this.getChildren().add(vbox);
    }
    public void setOnPlay(Runnable onPlay){
        play.setOnMouseClicked(event -> onPlay.run());
    }
    public void setOnPause(Runnable onPause){
        pause.setOnMouseClicked(event -> onPause.run());
    }
    public void setOnStep(Runnable onStep){
        step.setOnMouseClicked(event -> onStep.run());
    }

    public void setProgress(double progress){
        if(pb != null){
            pb.setProgress(progress);
        }
    }

    private Button createButton(String label){
        Button b = new Button(label);

        b.prefWidthProperty().bind(widthProperty().divide(3));

        return b;
    }
    @Override
    public void notifyStructureChange(Structure structure) { }

    @Override
    public void notifyHighlightChange(Highlights highlights) { }
}
