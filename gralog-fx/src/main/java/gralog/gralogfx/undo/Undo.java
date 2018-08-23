package gralog.gralogfx.undo;

import com.rits.cloning.Cloner;
import gralog.structure.Structure;

import java.util.HashMap;
import java.util.Stack;

/**
 * Holds copies of structures in a stack. Use Record/Revert/Redo
 * to navigate between saved checkpoints of a structure (Ctrl+Z)
 */
public class Undo {
    private static final int MAX_NR = 200;

    private static HashMap<Structure, FixedQueue<Structure>> structureStack;
    private static Cloner cloner;

    static{
        structureStack = new HashMap<>();
        cloner = new com.rits.cloning.Cloner();
    }

    /**
     * Call this method BEFORE changing a structure to create a checkpoint.
     * Now you can come back to this checkpoint by calling Revert
     * @param structure The structure to save to the Undo-stack
     */
    public static void Record(Structure structure){
        if(!structureStack.containsKey(structure)){
            structureStack.put(structure, new FixedQueue<>(MAX_NR));
        }
        System.out.println("done");
        FixedQueue<Structure> stack = structureStack.get(structure);
        stack.push(cloner.deepClone(structure));
        System.out.println(stack.count());
    }

    public static void Redo(Structure structure){
        if(!structureStack.containsKey(structure)){
            return;
        }

        FixedQueue<Structure> stack = structureStack.get(structure);
        if(stack.count() != 0){
            Structure reference = stack.revertPop();
            reference = cloner.deepClone(reference);
            if(reference != null){
                structure.__SET_VERTICES_T(reference.__GET_VERTICES_T());
                structure.__SET_EDGES_T(reference.__GET_EDGES_T());
            }
        }
    }
    /**
     * Reverts changes to a structure to the last created checkpoint
     * To create a checkpoint use Undo.Record(..)
     *
     * If no saved checkpoint, nothing happens
     * @param structure The structure to revert back
     */
    public static void Revert(Structure structure){
        if(!structureStack.containsKey(structure)){
            // no saved checkpoints
            return;
        }

        FixedQueue<Structure> stack = structureStack.get(structure);
        if(stack.count() != 0){
            stack.pop();
            Structure reference = stack.last();
            if(reference != null){
                reference = cloner.deepClone(reference);
                structure.__SET_VERTICES_T(reference.__GET_VERTICES_T());
                structure.__SET_EDGES_T(reference.__GET_EDGES_T())  ;
            }
        }
    }

}
