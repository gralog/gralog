/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.views;

import gralog.gralogfx.StructurePane;

/**
 *
 */
public interface View {
    void update();
    void update(Object obj);
    void setStructurePane(StructurePane structurePane);
}
