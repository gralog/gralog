
package gralog.finitegame.structure;

import gralog.plugins.XmlName;
import gralog.structure.*;


@StructureDescription(
    name="Finite Game",
    text="A Finite, two-player game-graph",
    url="http://mathworld.wolfram.com/FiniteGame.html"
)
@XmlName(name="finitegame")
public class FiniteGame extends Structure<FiniteGamePosition, FiniteGameMove>
{
    @Override
    public FiniteGamePosition CreateVertex()
    {
        return new FiniteGamePosition();
    }
    
    @Override
    public FiniteGameMove CreateEdge()
    {
        return new FiniteGameMove();
    }
}
