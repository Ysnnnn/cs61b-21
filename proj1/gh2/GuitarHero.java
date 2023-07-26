package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
public class GuitarHero {
    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final int KEYS_AMOUNT = KEYBOARD.length();

    public static void main(String[] args) {
        GuitarString[] guitarStrings = new GuitarString[KEYS_AMOUNT];

        for(int i = 0; i < KEYS_AMOUNT; i += 1) {
            double frequency = 440 * Math.pow(2, (i - 24) / 12.0);
            guitarStrings[i] = new GuitarString(frequency);
        }
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyIdex = KEYBOARD.indexOf(key);
                if (key > 0 && key < KEYS_AMOUNT) {
                    guitarStrings[key].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (GuitarString s : guitarStrings) {
                sample += s.sample();
            }

            StdAudio.play(sample);

            for (GuitarString s : guitarStrings) {
                s.tic();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString s : guitarStrings) {
                s.tic();
            }
        }
    }

}
