/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.test;

import gralog.plugins.PluginManager;
import gralog.structure.*;

/**
 *
 * @author viktor
 */
public class GrALoGTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        try {

            if(args.length > 1)
            {
                PluginManager.Initialize();

                String pathToPlugin = "/home/viktor/NetBeansProjects/GrALoG Automaton/dist/GrALoG_Automaton.jar";
                System.out.println("Loading GrALoG Plugin " + pathToPlugin + "...");
                PluginManager.LoadPlugin(pathToPlugin);
                
                
                System.out.println("Available Structure Classes:");
                for(String structure : PluginManager.getStructureClasses())
                    System.out.println("   " + structure);
                    
                
                System.out.println("Loading File " + args[0] + "...");
                Structure s = Structure.LoadFromFile(args[0]);
                
                System.out.println("Writing File " + args[1] + "...");
                s.WriteToFile(args[1]);
            }
            else
            {
                System.out.println("usage: bla <inputfile> <outputfile>");
            }
            
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
}
