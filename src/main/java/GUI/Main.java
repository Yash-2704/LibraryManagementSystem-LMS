package GUI;
// import GUI.LoginFrame; // Keep this commented if LoginFrame itself has issues, otherwise uncomment

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Effective Classpath ---");
        System.out.println(System.getProperty("java.class.path"));
        System.out.println("---------------------------");

        // Ensure LoginFrame can be instantiated without other complex dependencies for this test
        // For now, let's just test if Main runs and prints classpath.
        // If LoginFrame is stable, uncomment the line below.
        new GUI.LoginFrame(); 
    }
}
