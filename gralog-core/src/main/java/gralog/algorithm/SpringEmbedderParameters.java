/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
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
