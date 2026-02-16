package examples.onboarding;

import engine.DurableContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmployeeOnboardingWorkflow {

    public static void run(String workflowId) throws Exception {

        DurableContext ctx = new DurableContext(workflowId);

        // Step 1 - Sequential
        String record = ctx.step("create_record", () -> {
            System.out.println("Creating employee record...");
            Thread.sleep(2000);
            return "Record Created";
        });

        // Step 2 & 3 - Parallel
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture<String> laptopFuture =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return ctx.step("provision_laptop", () -> {
                            System.out.println("Provisioning laptop...");
                            Thread.sleep(2000);
                            return "Laptop Provisioned";
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        CompletableFuture<String> accessFuture =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return ctx.step("provision_access", () -> {
                            System.out.println("Provisioning access...");
                            Thread.sleep(2000);
                            return "Access Granted";
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

        CompletableFuture.allOf(laptopFuture, accessFuture).join();

        executor.shutdown();

        // Step 4 - Sequential
        String email = ctx.step("send_email", () -> {
            System.out.println("Sending welcome email...");
            Thread.sleep(2000);
            return "Email Sent";
        });

        System.out.println("Workflow Completed Successfully");
    }
}
