/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

/**
 *
 */
public class SpringEmbedderParameters extends AlgorithmParameters {

    public Double c_repel                  =     3.0d;
    public Double c_spring                 =     2.0d;
    public Double friction                 =     0.5d; // lower value = more friction (0 = no preservation of momentum)
    public Double unstressed_spring_length =     3.0d;
    public Double delta                    =     0.04d;
    public Double movement_threshold       =     0.01d; // percentage of unstressed_spring_length
    public Integer max_iterations          = 10000;
}
