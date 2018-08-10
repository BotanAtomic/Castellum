package org.castellum.logger;

import java.util.ArrayDeque;
import java.util.Date;

public class Logger extends Thread {

    private final static Logger instance = new Logger();

    private ArrayDeque<Runnable> actions = new ArrayDeque<>();

    public static void initialize() {
        instance.start();
    }

    @Override
    public void run() {
        while (isAlive()) {
            Runnable runnable = actions.pollFirst();

            if (runnable != null)
                runnable.run();
        }
    }

    public static void writeError(Exception error) {
        error.printStackTrace();
    }

    public static void println(String line, Object... objects) {
        // TODO: 07/08/18 Write handler
        instance.actions.addLast(() -> {
            StringBuilder builder = new StringBuilder();

            int k = 0;
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == '$') {
                    builder.append(k < objects.length ? String.valueOf(objects[k]) : "");
                    k++;
                } else
                    builder.append(line.charAt(j));
            }

            System.out.println(new Date().getTime() + "   /   " + builder.toString());
        });

    }


}
