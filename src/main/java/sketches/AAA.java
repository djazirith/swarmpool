package sketches;

import geomerative.RFont;
import geomerative.RG;
import geomerative.RPoint;
import geomerative.RShape;
import processing.core.PApplet;

import java.util.ArrayList;

public class AAA extends PApplet {
    private RShape grp;
    private Letter a0, a1, a2;

    public void settings() {
        RG.init(this);
        RG.setPolygonizer(RG.UNIFORMLENGTH);
        grp = RG.getText("aaa", "../../resources/marianna.ttf", 300, RFont.CENTER);
        a0 = new Other(0);
        a1 = new Other(1);
        a2 = new Other(2);

        size(1 << 9, 1 << 8, FX2D);
    }

    class Letter {
        ArrayList<RPoint> contourA = new ArrayList<>();
        ArrayList<RPoint> contourB = new ArrayList<>();
        static final long dur = 90; //[frames] (essentially bar length)
        final int ordinal; //∈ℕ₀ (basically bar number)

        Letter(final int ordinal) {
            this.ordinal = ordinal;
        }

        void draw() {
            pushMatrix();
            translate(width * 0.5f, height * 0.5f);
            colorMode(GRAY);
            stroke(25, 55);
            strokeWeight(6);
            noFill();
            int lower = contourA.size() < contourB.size() ? contourA.size() : contourB.size();
            int limit;
            switch (Long.compare(frameCount / dur, ordinal)) {
                case -1:
                    limit = 0;
                    break;
                case 0:
                    limit = (int) map(frameCount % dur, 0, dur, 0, lower);
                    RPoint v0 = contourA.get(limit);
                    RPoint v1 = contourB.get(limit);
                    break;
                default:
                    limit = lower;
            }

            for (int i = 0; i < limit; i++) {
                RPoint v0 = contourA.get(i);
                RPoint v1 = contourB.get(i);
                line(v0.x, v0.y, v1.x, v1.y);
            }

            popMatrix();
        }
    }

    class Other extends Letter {
        Other(final int ordinal) {
            super(ordinal);
            RPoint[] points = grp.children[ordinal].getPoints();
            for (int l = points.length / 2 - 1, r = points.length / 2;
                 l >= 0 && r < points.length;
                 l--, r++) {
                contourA.add(points[l]);
                contourB.add(points[r]);
            }
        }
    }

    public void draw(){
        background(225);
        noFill();
        strokeWeight(1);
        a0.draw();
        a1.draw();
        a2.draw();

        saveFrame("/cc/seq-####.png");
    }
}
