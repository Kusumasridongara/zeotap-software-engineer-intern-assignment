package engine;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class DurableContext {

    private final String workflowId;
    private final AtomicInteger sequence = new AtomicInteger(0);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DurableContext(String workflowId) {
        this.workflowId = workflowId;
    }

    public synchronized <T> T step(String stepId, Callable<T> fn) throws Exception {

        int seq = sequence.incrementAndGet();
        String stepKey = stepId + "_" + seq;

        try (Connection conn = DatabaseManager.getConnection()) {

            // 1️⃣ Check if already completed
            String checkSql = "SELECT status, output FROM steps WHERE workflow_id = ? AND step_key = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, workflowId);
                checkStmt.setString(2, stepKey);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String status = rs.getString("status");

                    if ("COMPLETED".equals(status)) {
                        String json = rs.getString("output");
                        return objectMapper.readValue(json, (Class<T>) Object.class);
                    }

                    // If IN_PROGRESS → treat as failed and re-run
                }
            }

            // 2️⃣ Insert IN_PROGRESS
            String insertSql = "INSERT OR REPLACE INTO steps(workflow_id, step_key, status, output) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, workflowId);
                insertStmt.setString(2, stepKey);
                insertStmt.setString(3, "IN_PROGRESS");
                insertStmt.setString(4, null);
                insertStmt.executeUpdate();
            }

            // 3️⃣ Execute actual step
            T result = fn.call();

            // 4️⃣ Update to COMPLETED
            String updateSql = "UPDATE steps SET status = ?, output = ? WHERE workflow_id = ? AND step_key = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, "COMPLETED");
                updateStmt.setString(2, objectMapper.writeValueAsString(result));
                updateStmt.setString(3, workflowId);
                updateStmt.setString(4, stepKey);
                updateStmt.executeUpdate();
            }

            return result;
        }
    }
}
