package at.markusmeierhofer.digialbum.gui.helpers;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MM on 12.02.2017.
 */
public class MultiImageAnimator implements Runnable {
    private final static int ZOOM_ITERATIONS = 30;
    private final static int ANIMATION_TIME = 1000;

    private List<ImageView> imageViews = new ArrayList<>();
    private double endWidth;
    private double endHeight;
    private boolean stopped;

    public MultiImageAnimator(double endWidth, double endHeight, ImageView... imageViews) {
        this.endWidth = endWidth;
        this.endHeight = endHeight;
        Collections.addAll(this.imageViews, imageViews);
    }

    public double getStartWidth() {
        return endWidth / ZOOM_ITERATIONS;
    }

    public double getStartHeight() {
        return endHeight / ZOOM_ITERATIONS;
    }

    @Override
    public void run() {
        double startWidth = getStartWidth();
        double startHeight = getStartHeight();
        int sleepTime = ANIMATION_TIME / ZOOM_ITERATIONS;

        for (int i = 1; i <= ZOOM_ITERATIONS; i++) {
            if (!stopped) {
                for (ImageView imageView : imageViews) {
                    if (imageView != null) {
                        imageView.setFitWidth(startWidth * i);
                        imageView.setFitHeight(startHeight * i);
                    }
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        stopped = true;
    }
}
