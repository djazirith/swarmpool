package sketches;

import processing.core.PApplet;
import traer.physics.Particle;
import traer.physics.ParticleSystem;

public final class DarkWater extends PApplet {
    private final int r = 1;
    private final int d = 1<<1;

    private ParticleSystem ps;
    private Particle[][] particles;
    private Particle cursor;

    public void settings(){
        size(1 << 8, 1 << 7, P2D);
        smooth(4);

        ps = new ParticleSystem();
//        ps.setIntegrator(ParticleSystem.MODIFIED_EULER);
        ps.setIntegrator(ParticleSystem.RUNGE_KUTTA);

        float damp = 0.33f;
        float ks = 0.1f;
        cursor = ps.makeParticle(0, width * 0.5f, height * 0.5f, 0);
        cursor.makeFixed();

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
                    ps.makeAttraction(cursor, particles[i][j], -0.9f, d);
                }
            }
        }

        dumpParam();
    }

    public void draw(){
        String TITLE = String.format("%2.0f fps  %dx%d  %s", frameRate, width, height, getClass().getSimpleName());
        surface.setTitle(TITLE);
        background(0);
        noStroke();

        for (int i = 1; i < ps.numberOfParticles(); i++) { //assume cursor is the first particle
            Particle p = ps.getParticle(i);
            float px = p.position().x();
            float py = p.position().y();
            fill(55, map(p.force().length(), 0, 1, 225, 205));
            ellipse(px, py, d, d);
        }

        if (mousePressed) {
            cursor.position().setX(mouseX);
            cursor.position().setY(mouseY);
            fill(255, 0, 0);
            ellipse(cursor.position().x(), cursor.position().y(), r, r);
        }
        ps.tick();
    }

    private void dumpParam() {
        System.out.printf("width x height: %dx%d\ngrid: %dx%d\nparticles: %d\nsprings: %d", width, height, particles.length, particles[0].length, particles.length * particles[0].length, particles.length * particles[0].length * 2);
    }

    public void mousePressed() {
        cursor.setMass(1);
    }

    public void mouseReleased() {
        cursor.setMass(0);
    }

}
