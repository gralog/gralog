package gralog.gralogfx.threading;

import gralog.gralogfx.StructurePane;

public class ScrollThread extends Thread {

    private StructurePane pane;

    private double verticalSpeed;
    private double horizontalSpeed;

    private static double baselineSpeed = 8;

    public ScrollThread(StructurePane pane, double verticalSpeed, double horizontalSpeed) {
        this.pane = pane;
        this.verticalSpeed = verticalSpeed;
        this.horizontalSpeed = horizontalSpeed;
    }

    @Override
    public void run() {
        double time = System.nanoTime();
        double delta = 0;
        while(!Thread.currentThread().isInterrupted()) {
            delta = (System.nanoTime() - time)/1000000000;
            pane.move(delta * horizontalSpeed,delta * verticalSpeed);
            pane.requestRedraw();
            time = System.nanoTime();
            try {
                Thread.sleep(16);
            }catch(InterruptedException e) {
                //Interrupted the thread
                return;
            }
        }
    }

    /**
     * Creates a vertically scrolling scroll trhead
     */
    public static ScrollThread vertical(StructurePane pane, boolean forward) {
        return new ScrollThread(pane, (forward ? 1 : -1) * baselineSpeed, 0);
    }

    /**
     * Creates a horizontal scrolling scroll trhead
     */
    public static ScrollThread horizontal(StructurePane pane, boolean forward) {
        return new ScrollThread(pane, 0, (forward ? 1 : -1) * baselineSpeed);
    }
}
