package gitlet;

import static gitlet.Utils.*;
public class MyHelperFunction {
    public static void exit(String message, Object... args) {
        message(message, args);
        System.exit(0);
    }
}
