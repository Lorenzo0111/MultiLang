package me.lorenzo0111.multilang.tasks;

import me.lorenzo0111.multilang.protocol.adapter.ChatAdapter;

public class RealtimeThread extends Thread {
    private static RealtimeThread instance = null;
    private boolean run = true;
    private final ChatAdapter adapter;

    public RealtimeThread(ChatAdapter adapter) {
        super("MultiLang-RealTime");
        this.adapter = adapter;
    }

    @Override
    public void run() {
        while (run) {
            Runnable poll = adapter.getQueue().poll();
            if (poll != null) {
                poll.run();
            }
        }
    }

    public void end() {
        run = false;
        instance = null;
        this.interrupt();
    }

    public static RealtimeThread getInstance() {
        return instance;
    }
}
