/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

/**
 *
 */
public class SpringEmbedderParameters extends AlgorithmParameters {

    public Double cRepel = 3.0d;
    public Double cSpring = 2.0d;
    public Double friction = 0.5d; // lower value = more friction (0 = no preservation of momentum)
    public Double unstressedSpringLength = 3.0d;
    public Double delta = 0.04d;
    public Double movementThreshold = 0.01d; // percentage of unstressed_spring_length
    public Integer maxIterations = 10000;
}
