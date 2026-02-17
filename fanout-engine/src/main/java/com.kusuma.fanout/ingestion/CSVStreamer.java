package com.kusuma.fanout.ingestion;

import com.kusuma.fanout.model.Record;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

public class CSVStreamer implements Runnable {

    private final String filePath;
    private final BlockingQueue<Record> queue;

    public CSVStreamer(String filePath, BlockingQueue<Record> queue) {
        this.filePath = filePath;
        this.queue = queue;
    }

    @Override
    public void run() {

        System.out.println("Reading file from: " + filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {

                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 4) {
                    continue;
                }

                Record record = new Record(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim()
                );

                System.out.println("Producing record: " + record);

                queue.put(record); // blocks if queue is full (backpressure)
            }

            System.out.println("Finished reading file.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
