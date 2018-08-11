package at.markusmeierhofer.digialbum;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MM on 12.02.2017.
 */
public class MultiImageAnimator extends Thread {
    private List<ImageView> imageViews = new ArrayList<>();
    private double endWidth;
    private double endHeight;
    private final static int ZOOM_ITERATIONS = 30;
    private final static int ANIMATION_TIME = 1000;

    public MultiImageAnimator(double endWidth, double endHeight, ImageView... imageViews) {
        this.endWidth = endWidth;
        this.endHeight = endHeight;
        Collections.addAll(this.imageViews, imageViews);
    }

    @Override
    public void run() {
        double startWidth = endWidth / ZOOM_ITERATIONS;
        double startHeight = endHeight / ZOOM_ITERATIONS;
        int sleepTime = ANIMATION_TIME / ZOOM_ITERATIONS;

        for (int i = 1; i <= ZOOM_ITERATIONS; i++) {
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
