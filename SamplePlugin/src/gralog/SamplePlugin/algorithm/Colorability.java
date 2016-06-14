/*b hy67
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.SamplePlugin.algorithm;

/**
 *
 * @author Hv
 */
import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;
import gralog.rendering.GralogColor;
import java.util.*;

@AlgorithmDescription(
    name="Colorability",
    text="",
    url=""
)
public class Colorability extends Algorithm{
    public static StringBuilder ConvertToBase3(long n,int nVertices){
        StringBuilder ans=new StringBuilder();
        while(n>0){
            int rem=(int)(n%3);
            n=n/3;
            String temp=  Integer.toString(rem) ;
            ans.append(  temp );
        }
        ans=ans.reverse();
        while(ans.length() <nVertices) {
            ans.append('0');
        }
        return ans;
    }
    boolean check(StringBuilder str,DirectedGraph s){
        Set<Vertex> ver=s.getVertices();
        Set<Edge> edge=s.getEdges();
        for(Vertex i: ver){
            int x=Integer.parseInt(i.Label );
            int cnt=0;
            int size=0;
            for (Vertex j : ver ){
                    for(Edge e : edge){
                        if(e.source==i  && e.target== j ){
                           int y=Integer.parseInt(j.Label);
                            if(str.charAt(x) == str.charAt(y) ){
                            
                                cnt=cnt+1;
                                
                            }
                            size=size+1;
                           
                        }
                    }
                
                    
            }        
             if(cnt>(size/2) ) {
                
           
             return false;
             }
             }
           
        
         
         return true;
    }
    public Object Run(DirectedGraph s,AlgorithmParameters p,ProgressHandler onprogress) throws Exception {
     Set<Vertex> vertices=s.getVertices();
     int nVertices=vertices.size();
     int k=0;
     for(Vertex v: vertices){
         v.Label=Integer.toString(k);
         k++;
     }
    
     for(long i=1;i<=Math.pow(3,nVertices);i++){
        StringBuilder str=ConvertToBase3(i,nVertices);
        if( check(str,s)  == true ){
            int j=0;
            for(Vertex v: vertices){
                String x= Character.toString(str.charAt(j));
                if(x.contentEquals("0") ){
                    v.FillColor=GralogColor.blue;
                }
                else if(x.contentEquals("1") ){
                    v.FillColor=GralogColor.green;
                }
                else{
                    v.FillColor=GralogColor.red;
                }
                j=j+1;
                
            }
            if(onprogress!= null) {
                onprogress.OnProgress(s);
            }
            return "Arrangement possible"; 
        }
     }
     if(onprogress!= null) {
                onprogress.OnProgress(s);
            }
     return "Contradiction found!!";
    }
   
    
}
