package lab1;

public class main {
    static String inputFile;
    static String outputFile;


    public static void main(String[] args) {
        System.out.println("Hello World"); //TODO: Remove this
//        inputFile = args[2];  //TODO: uncomment when ready
//        outputFile = args[3]; //TODO: uncomment when ready

        try {
            if (args[0].equals("-huff") && args[1].equals("-d")) {

            } else if (args[0].equals("-lzw") && args[1].equals("-d")) {

            } else if (args[0].equals("-opt") && args[1].equals("-d")) {

            } else if (args[0].equals("-huff") && args[1].equals("-c")) {

            } else if (args[0].equals("-lzw") && args[1].equals("-c")) {

            } else if (args[0].equals("-opt") && args[1].equals("-c")) {

            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
