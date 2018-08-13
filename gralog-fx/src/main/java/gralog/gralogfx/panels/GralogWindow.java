package gralog.gralogfx.panels;

import gralog.structure.Highlights;
import gralog.structure.Structure;
import org.dockfx.DockNode;

/**
 * Every UI panel should implement this interface.
 *
 * Only then will it be able to be selected from the drop down window menu
 *
 * @see gralog.gralogfx.Tabs
 */
public interface GralogWindow
{
    /**
     * Implements a reaction to a change of the current
     * structure.
     */
    void notifyStructureChange(Structure structure);

    /**
     * Implements a reaction to a change of currently
     * highlighted objects.
     */
    void notifyHighlightChange(Highlights highlights);

    

}
