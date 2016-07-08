/*b hy67
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.algorithm;

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
    name="Col2",
    text="",
    url=""
)
public class Col2 extends Algorithm{
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
                        if(e.getSource()==i  && e.getTarget()== j ){
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
    
    DirectedGraph generate(DirectedGraph s,int nVertices,double p){
     Set<Vertex> vertices=s.getVertices();
     Set<Edge> edge=s.getEdges();
     vertices.clear();
     edge.clear();
     Integer coor=1;
     for(Integer i=0;i<nVertices;i++){
         Vertex v=s.CreateVertex();
         v.Coordinates.add(Math.random()*100);
         v.Coordinates.add(Math.random()*100);
         v.Label=Integer.toString(i);
        
         s.AddVertex(v);
         coor=coor+2;
     }
     
     for(Vertex v: vertices){
         for(Vertex w: vertices ){
             if(v!=w) {
                double random=Math.random();
                if(random<=p){
                  Edge ew=s.CreateEdge(v,w);
                  s.AddEdge(ew);
                }
             }
         }
     }
     return s;
    }
   
    private   int contradictions = 1;
    public Object Run(DirectedGraph s,AlgorithmParameters p,ProgressHandler onprogress) throws Exception {
      
        Integer nIter,nVertices;
        double prob;
        Scanner in=new Scanner(System.in);
        System.out.println("enter number of iterations");
        nIter=in.nextInt();
        System.out.println("enter the number of vertices");
        nVertices=in.nextInt();
        System.out.println("enter the edge density");
        prob=in.nextDouble();
        for(int iter=1;iter<=100;iter++){
            s=generate(s,nVertices,prob);
            /*SpringEmbedder embedder = new SpringEmbedder();
            AlgorithmParameters params = embedder.GetParameters(s);
            embedder.Run(s, params, onprogress);
            */Set<Vertex> vertices=s.getVertices();
            
            long i;
            for(i=0;i<Math.pow(3,nVertices);i++){
            
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
                Thread.sleep(3000);
                break;
       //     return "Arrangement possible"; 
            }
            }
              if (onprogress!= null) {
                onprogress.OnProgress(s);
              }
        if(i==Math.pow(3, nVertices)){
         
            s.WriteToFile("contradiction" + contradictions + ".graphml");
            contradictions++;
            return "Contradiction found!!";
        
        }
    }
        return "All checks passed";
    }
}
      

