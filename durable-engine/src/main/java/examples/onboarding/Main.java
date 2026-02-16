package examples.onboarding;

public class Main {

    public static void main(String[] args) throws Exception {

        String workflowId = "emp-123";

        EmployeeOnboardingWorkflow.run(workflowId);
    }
}
