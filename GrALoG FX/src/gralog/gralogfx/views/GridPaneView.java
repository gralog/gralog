/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.views;

import gralog.gralogfx.StructurePane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author viktor
 */
public abstract class GridPaneView extends GridPane implements View
{
    protected StructurePane structurePane = null;
    protected Object displayObject = null;

    @Override
    public void Update(Object newObject)
    {
        this.displayObject = newObject;
        Update();
    }
    
    @Override
    public void setStructurePane(StructurePane structurePane) 
    {
        this.structurePane = structurePane;
    }
    
    public void RequestRedraw()
    {
        StructurePane sp = structurePane;
        if(sp != null)
            sp.RequestRedraw();
    }
}
