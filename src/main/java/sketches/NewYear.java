package sketches;

import processing.core.PApplet;
import processing.core.PVector;
import traer.physics.Particle;
import traer.physics.ParticleSystem;

public final class NewYear extends PApplet {
    private final int r = 1;
    private final int d = 1<<1;

    private ParticleSystem ps;
    private Particle[][] particles;
    private Particle oh;
    private Particle one;
    private Particle two;
    private Particle nine;

    public void settings(){
        size(1 << 9, 1 << 8, P2D);

        ps = new ParticleSystem();
        ps.setIntegrator(ParticleSystem.MODIFIED_EULER);

        initDigits();
        initFabric();

        dumpParam();
    }

    private void initFabric() {
        float damp = 0.33f;
        float ks = 0.05f;
        particles = new Particle[width / d][height / d];
        for (int x = r, i = 0; x < width; x += d, i++) {
            for (int y = r, j = 0; y < height; y += d, j++) {
                particles[i][j] = ps.makeParticle(1, x, y, 0);
                if (j > 0) {
                    ps.makeSpring(particles[i][j-1], particles[i][j], ks, damp, d);
                }
                if (i > 0) {
                    ps.makeSpring(particles[i-1][j], particles[i][j], ks, damp, d);
                }
                if (i == 0 || x == width - r  //east and west columns
                        || j == 0 || y == height - r) {   //north and south rows
                    particles[i][j].makeFixed();
                } else {
                    ps.makeAttraction(two, particles[i][j], -0.5f, d);
                    ps.makeAttraction(oh, particles[i][j], -0.5f, d);
                    ps.makeAttraction(one, particles[i][j], -0.5f, d);
                    ps.makeAttraction(nine, particles[i][j], -0.5f, d);
                }
            }
        }
    }

    private void initDigits() {
        oh = ps.makeParticle();
        one = ps.makeParticle();
        two = ps.makeParticle();
        nine = ps.makeParticle();
    }

    public void draw(){
        String TITLE = String.format("%2.0f fps  %dx%d  %s", frameRate, width, height, getClass().getSimpleName());
        surface.setTitle(TITLE);

        ps.tick();
        background(0);

        drawFabric();
        drawDigits();
//        saveFrame("frames/frame" + nf(frameCount, 3, 0) + ".png");
    }

    private void drawDigits() {
        final float scale = 80;
        PVector z = drawDigit(0, scale, scale, width * 0.4f, height >>1);
        updateParticlePosition(oh, z);
        PVector o = drawDigit(1, scale * 0.5f, scale * 2, width * 0.6f, height >>2);
        updateParticlePosition(one, o);
        PVector t = drawDigit(2, scale, scale * 2, width * 0.1f, height >>2);
        updateParticlePosition(two, t);
        PVector n = drawDigit(9, scale, scale * 2, width * 0.8f, height >>2);
        updateParticlePosition(nine, n);
    }

    private PVector drawDigit(final int digit, final float sx, final float sy, final float tx, final float ty) {
        stroke(225);
        strokeWeight(2);
        noFill();
        pushMatrix();
        translate(tx, ty);
        PVector t = numeral(digit, 180);
        final float x = t.x * sx;
        final float y = t.y * sy;
        point(x, y);
        popMatrix();
        return new PVector(x + tx, y + ty);
    }

    private void updateParticlePosition(final Particle p, final PVector pos) {
        p.position().setX(pos.x);
        p.position().setY(pos.y);
    }

    private void drawFabric() {
        noStroke();
        for (int i = 4; i < ps.numberOfParticles(); i++) { //assume the first 4 are digits
            Particle p = ps.getParticle(i);
            float px = p.position().x();
            float py = p.position().y();
            fill(55, map(p.force().length(), 0, 1, 225, 205));
            ellipse(px, py, d, d);
        }
    }

    private PVector numeral(final int ordinal, final long dur) {
        int lines = ordinal + 1; //ordinal = amount of angles
        long interval = dur / lines; //equally long duration for individual lines
        long stage = frameCount / interval; //integer division
        stage = stage % lines; //reset from the beginning
        switch(ordinal) {
            case 0:
                float arg = map(frameCount, 0, dur, 0, TWO_PI);
                return new PVector(cos(arg) * 0.5f, sin(arg));
            case 1:
                arg = norm(frameCount % interval, 0, interval);
                switch ((int) stage) {
                    case 0:
                        return new PVector(arg, 0.25f - arg * 0.25f);
                    case 1:
                        return new PVector(1, arg);
                }
            case 2:
                arg = norm(frameCount % interval, 0, interval);
                switch ((int) stage) {
                    case 0:
                        return new PVector(arg, 0);
                    case 1:
                        return new PVector(1 - arg, arg);
                    case 2:
                        return new PVector(arg, 1);
                }
            case 9:
                arg = norm(frameCount % interval, 0, interval);
                switch ((int) stage) {
                    case 0:
                        return new PVector(1 - arg, 0.25f);
                    case 1:
                        return new PVector(0, 0.25f - arg * 0.25f);
                    case 2:
                        return new PVector(arg, 0);
                    case 3:
                        return new PVector(1, arg);
                    case 4:
                        return new PVector(1 - arg, 1);
                    case 5:
                        return new PVector(0, 1 - arg * 0.25f);
                    case 6:
                        return new PVector(arg * 0.75f, 0.75f);
                    case 7:
                        return new PVector(0.75f, 0.75f + arg * 0.12f);
                    case 8:
                        return new PVector(0.75f - arg * 0.5f, 0.75f + 0.12f);
                    case 9:
                        return new PVector(0.25f, 0.87f - arg * 0.05f);
                }
            default:
                return null;
        }
    }

    private void dumpParam() {
        System.out.printf("w x h: %dx%d\ngrid: %dx%d\nparticles: %d\nsprings: %d", width, height,
                particles.length, particles[0].length, particles.length * particles[0].length, ps.numberOfSprings());
    }

}
