package lab1;

import lab1.Huffman.Huffman;

public class main {
    static String inputFile;
    static String outputFile;


    public static void main(String[] args) {

        inputFile = args[2];
        outputFile = args[3];

        try {
            if (args[0].equals("-huff") && args[1].equals("-d")) {
                System.out.println("Decompressing huffman file");
                Huffman.decode(inputFile, outputFile);
            } else if (args[0].equals("-lzw") && args[1].equals("-d")) {

            } else if (args[0].equals("-opt") && args[1].equals("-d")) {

            } else if (args[0].equals("-huff") && args[1].equals("-c")) {
                System.out.println("Compressing huffman file");
                Huffman.encode(inputFile, outputFile);


            } else if (args[0].equals("-lzw") && args[1].equals("-c")) {

            } else if (args[0].equals("-opt") && args[1].equals("-c")) {

            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
