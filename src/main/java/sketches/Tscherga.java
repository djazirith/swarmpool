package sketches;

import com.codepoetics.protonpack.StreamUtils;
import geomerative.RFont;
import geomerative.RG;
import geomerative.RPoint;
import geomerative.RShape;
import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.*;
import traer.physics.Particle;
import traer.physics.ParticleSystem;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ffmpeg -ratio 55 -i seq-%04d.tga -c:v huffyuv copy.avi
 * ffmpeg -i copy.avi -i ../src/main/resources/cork-no-pour.wav -c copy right.avi
 *
 * ffmpeg -ratio 45 -i frame%04d.tga -c:v huffyuv video.avi
 * ffmpeg -i audio.wav -i video.avi -c copy AV.avi
 */
public class Tscherga extends PApplet {

    private final int d = 2; //particle diameter

    private ParticleSystem ps;
    private Particle cursor;

    private int[] colors;
    private RShape grp;

    private Letter ch, e, r, g, a;
    private PImage logo;
    SoundFile track;

    public void settings() {
        RG.init(this);
        RG.setPolygonizer(RG.UNIFORMLENGTH);
        grp = RG.getText("черга", "../../resources/marianna.ttf", 300, RFont.CENTER);
        ch = new Other(0);
        e = new E(1);
        r = new Other(2);
        g = new G(3);
        a = new Other(4);

        PImage logo = loadImage("../../resources/menada.png"); // NO ALPHA CHANNEL
        this.logo = filterAndAddAlpha(logo);

        PImage label = loadImage("../../resources/label.png"); //wine bottle label
        colors = new int[label.height];
        int w = (int) (width * 0.125f);
        for (int h = 0; h < label.height; h++) {
            colors[h] = label.get(w, h); //ARGB
        }

        size(1 << 9, 1 << 8, FX2D);
        ps = new ParticleSystem();
        ps.setIntegrator(ParticleSystem.RUNGE_KUTTA);
        cursor = ps.makeParticle(1.68f, width * 0.63f, height * 0.42f, 0);
        cursor.makeFixed();
        float damp = 0.33f;
        float ks = 0.25f;
        Particle[][] particles = new Particle[width / d][height / d];
        int radius = d / 2;
        for (int x = radius, i = 0; x < width; x += d, i++) {
            for (int y = radius, j = 0; y < height; y += d, j++) {
                particles[i][j] = ps.makeParticle(1, x, y, 0);
                if (j > 0) {
                    ps.makeSpring(particles[i][j - 1], particles[i][j], ks, damp, d);
                }
                if (i > 0) {
                    ps.makeSpring(particles[i - 1][j], particles[i][j], ks, damp, d);
                }
                if (i == 0 || x == width - radius  //east and west columns
                        || j == 0 || y == height - radius) {   //north and south rows
                    particles[i][j].makeFixed();
                } else {
                    ps.makeAttraction(cursor, particles[i][j], -0.9f, d);
                }
            }
        }

        //https://freesound.org/people/Greencouch/sounds/106265/
        //https://freesound.org/people/HDM2013/sounds/179437/
        track = new SoundFile(this, "../../resources/cork.wav");
        track.play();
    }

    private Set<Integer> rr = new HashSet<>(Arrays.asList(0xfffcfefc, 0xffd4d2d4, 0xffeceeec, 0xffdcdadc, 0xffd4d6d4, 0xffdcdedc, 0xffc4c2c4, 0xffbcbebc, 0xfff4f6f4, 0xfff4f2f4, 0xffcccecc, 0x9c9e9c, 0xffa4a6a4, 0xffa4a2a4));

    private PImage filterAndAddAlpha(PImage image) {
        PImage newImage = createImage(image.width, image.height, ARGB);
        newImage.loadPixels();
        image.loadPixels();
        IntStream.range(0, image.pixels.length).forEach(i -> newImage.pixels[i] = rr.contains(image.pixels[i]) ? 0 : image.pixels[i] | 0xff000000);
        newImage.updatePixels();
        return newImage;
    }

    //                  {0xFF0C0A0C, 0xFFD4D2D4, 0xFF5C5A5C, 0xFFECEEEC, 0xFF444244, 0xFF5C5E5C, 0xFF949294, 0xFFECEAEC, 0xFF0C0E0C, 0xFF6C6A6C, 0xFF949694, 0xFFDCDADC, 0xFF6C6E6C, 0xFFD4D6D4, 0xFFDCDEDC, 0xFF4C4A4C, 0xFFC4C6C4, 0xFF343234, 0xFF4C4E4C, 0xFFC4C2C4, 0xFF343634, 0xFF040204, 0xFF7C7A7C, 0xFF9C9A9C, 0xFF040604, 0xFF7C7E7C, 0xFFBCBEBC, 0xFF545654, 0xFF1C1E1C, 0xFFBCBABC, 0xFF545254, 0xFF848684, 0xFFF4F6F4, 0xFF848284, 0xFFF4F2F4, 0xFF1C1A1C, 0xFF747274, 0xFF8C8E8C, 0xFF242624, 0xFF3C3E3C, 0xFFCCCECC, 0xFF242224, 0xFF747674, 0xFF2C2A2C, 0xFF2C2E2C, 0xFF9C9E9C, 0xFFA4A6A4, 0xFF646264, 0xFF3C3A3C, 0xFFA4A2A4, 0xFF646664, 0xFFACAAAC, 0xFFACAEAC, 0xFFE4E2E4, 0xFFE4E6E4, 0xFF141214, 0xFFCCCACC, 0xFF444644, 0xFFB4B2B4, 0xFFB4B6B4, 0xFF141614, 0xFFFCFAFC, 0xFF8C8A8C};

//    HashSet<String> distinctColors(PImage image) {
//        HashSet<String> result = new HashSet<>();
//        Arrays.stream(image.pixels).forEach(pixel -> result.add(Integer.toHexString(pixel)));
//        return result;
//    }
//
//    PImage clearColor(PImage image, int maskColor) {
//        PImage newImage = createImage(image.width, image.height, ARGB);
//        image.loadPixels();
//        newImage.loadPixels();
//        for(int n = 0; n < newImage.pixels.length; n++)
//            newImage.pixels[n] = image.pixels[n] == maskColor ? 0x00000000 : image.pixels[n] | 0xff000000;
//        newImage.updatePixels();
//        return newImage;
//    }


    /**
        Each letter should be animated in its designated bar, remain silent in prior bars and sustain at bars to come.
     */
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
                    //midpoint
                    float x = (v0.x + v1.x) * 0.5f + width * 0.5f;
                    float y = (v0.y + v1.y) * 0.5f + height * 0.5f;
                    cursor.position().setX(x);
                    cursor.position().setY(y);
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

    class E extends Letter {
        E(final int ordinal) {
            super(ordinal);
            RPoint[] points = grp.children[1].getPoints();

            contourA = Arrays.stream(points)
                    .limit(points.length / 2)
                    .collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(contourA);

            LinkedList<RPoint> middle = Arrays.stream(points)
                    .skip(3 * points.length / 4)
                    .limit(13 * points.length / 16)
                    .collect(Collectors.toCollection(LinkedList::new));
            LinkedList<RPoint> rightE = middle.stream()
                    .skip(middle.size() / 2)
                    .collect(Collectors.toCollection(LinkedList::new));
            LinkedList<RPoint> leftE = middle.stream()
                    .limit(middle.size() / 2)
                    .collect(Collectors.toCollection(LinkedList::new));
            contourB.addAll(rightE);
            contourB.addAll(leftE);

            LinkedList<RPoint> tailTop = Arrays.stream(points)
                    .limit(3 * points.length / 4 - 1)
                    .skip(9 * points.length / 16 - 2)  //limit/skip order matters
                    .collect(Collectors.toCollection(LinkedList::new));
            contourB.addAll(tailTop);
        }
    }

    class G extends Letter {
        G(final int ordinal) {
            super(ordinal);
            RPoint[] points = grp.children[3].getPoints();
            contourA = Arrays.stream(points)
                    .limit(points.length - (points.length / 6))
                    .skip((points.length / 5) * 2)
                    .collect(Collectors.toCollection(ArrayList::new));
            contourB = Arrays.stream(points)
                    .skip((points.length / 5) * 5 - 5)
                    .collect(Collectors.toCollection(ArrayList::new));
            contourB.addAll(Arrays.stream(points)
                    .limit((points.length / 5) * 2 - 5)
                    .collect(Collectors.toCollection(ArrayList::new)));
            Collections.reverse(contourB);
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
        ps.tick();

        surface.setTitle(String.format("%2.0f fps  %dx%d  %s %d", frameRate, width, height, getClass().getSimpleName(), frameCount));
        colorMode(RGB);
        int brownish = color(139, 83, 37);
        background(brownish);  //brownish

        noStroke();
        //fill the fabric with themed colors
        for (int i = 1; i < ps.numberOfParticles(); i++) { //assume cursor is the first particle
            Particle p = ps.getParticle(i);
            float px = p.position().x();
            float py = p.position().y();
            int colorIndex = i % (ps.numberOfParticles() / height);
            fill(colors[colorIndex], map(p.force().length(), 0, 1, 255, 205));
            ellipse(px, py, d, d);
        }

        ch.draw();
        e.draw();
        r.draw();
        g.draw();
        a.draw();

        if (frameCount > 5 * Letter.dur) {
            cursor.setMass(0);
            transparency = transparency > 0 ? transparency + 4 : transparency;
            tint(brownish, transparency);
            image(logo, width *.55f, height *.64f);
        }

        textSize(18);
        noStroke();
        colorMode(GRAY);
        StreamUtils.zipWithIndex("Copyright Anton Borisov Georgiev 2018".chars()
                .mapToObj(value -> String.valueOf((char) value)))
                .forEach(c -> {
                    fill(colors[(int) c.getIndex()]);
                    float w = map(c.getIndex(), 0, 37, 0, 400);
                    text(c.getValue(), w, height *.94f);
                });

        saveFrame("/frames/seq-####.tga");
    }

    private float transparency = 1; //0 causes it to never appear

}
