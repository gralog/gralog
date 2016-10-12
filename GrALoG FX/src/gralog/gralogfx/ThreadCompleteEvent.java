/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.gralogfx;

/**
 *
 */
public interface ThreadCompleteEvent {
    public void onThreadCompleted(AlgorithmThread sender);
}
