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
                
            } else if (args[0].equals("-huff") && args[1].equals("-c")) {
                System.out.println("Compressing huffman file");
                Huffman.encode(inputFile, outputFile);


            } else if (args[0].equals("-lzw") && args[1].equals("-d")) {
                System.out.println("Decompressing lzw file");
                LZW lzw = new LZW();
                lzw.decode(inputFile, outputFile);
            
            } else if (args[0].equals("-lzw") && args[1].equals("-c")) {
                System.out.println("Compressing lzw file");
                LZW lzw = new LZW();
                lzw.encode(inputFile, outputFile);

            } else if (args[0].equals("-opt") && args[1].equals("-d")) {
                System.out.println("Decompressing opt file");
                OptimizedAlgo opt = new OptimizedAlgo();
                opt.decode(input, output);

            } else if (args[0].equals("-opt") && args[1].equals("-c")) {
                System.out.println("Compressing opt file");
                OptimizedAlgo opt = new OptimizedAlgo();
                opt.encode(input,output);
            }
            
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
