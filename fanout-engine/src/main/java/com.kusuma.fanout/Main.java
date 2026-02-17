package com.kusuma.fanout;

import com.kusuma.fanout.orchestrator.FanOutOrchestrator;

public class Main {

    public static void main(String[] args) {
        new FanOutOrchestrator().start();
    }
}
