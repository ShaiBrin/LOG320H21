package lab1.Optimized;

import lab1.BitOutputStream;
import lab1.BitInputStream;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OptimizedAlgo {


    private int dictionarySize = 256;
    private static final int INITIAL_DICT_SIZE = 256;
    private static final int MAX_DICT_SIZE = 65536;
    private static final String DELIMITER  = "111111111";
    public  OptimizedAlgo(){}

    /***********************************************************************************/
    /*******************    FONCTIONS D'INITIALISATION   *******************************/
    /***********************************************************************************/

    /**
     * Cette méthode permet d'initialiser le dictionnaire pour la procédure de compression.
     * @return Une map String (key), Integer (value)
     */
    private Map<String, Integer> initDictionary() {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < INITIAL_DICT_SIZE; i++) {
            dictionary.put(Character.toString((char) i), i);
        }
        dictionarySize = INITIAL_DICT_SIZE;
        return dictionary;
    }

    /**
     * Cette méthode permet d'initialiser le dictionnaire pour la procédure de décompression.
     * La différence entre celui-ci et "initDictionary" c'est qu'on a interchangé la clé et la valeur.
     * Dans initDictionary on a une map de String, Integer et dans celui-ci on a Integer,String, car
     * dans la décompression on fait le contraire de la compression.
     * @return Un dictionaire ou plutôt une Map contenant comme clé : Un integer et comme valeur: une chaîne de charactère
     */
    private Map<Integer, String> initDictionaryDecompression() {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < INITIAL_DICT_SIZE; i++) {
            dictionary.put(i, Character.toString((char) i));
        }
        dictionarySize = INITIAL_DICT_SIZE;
        return dictionary;
    }


    /***********************************************************************************/
    /*******************    FONCTIONS D'ENCODAGE ET DÉCODAGE   *************************/
    /***********************************************************************************/

    public void encode(String inputFile, String outputFile) throws IOException {
        Map<String, Integer> dictionary = initDictionary();
        BitOutputStream bitOutputStream = new BitOutputStream(outputFile, 8);
        byte[] data = readInputFile(inputFile);

        int byteToInt = byteToInt(data[0]);

        String symbol = Character.toString((char) byteToInt);
        String character = "";
        String combined = "";
        String encodedBinary = "";
        String paddedBinary = "";
        String delimiter = "";
        int bitSize = 9;
        for (int i = 1; i < data.length; i++) {
            if (dictionarySize >= MAX_DICT_SIZE) {
                // On réinitialise le dictionnaire
                dictionary = initDictionary();
                bitSize = 9;
            }
            byteToInt = byteToInt(data[i]);
            character = Character.toString((char) byteToInt);
            combined = symbol + character;

            if (dictionary.containsKey(combined)) {
                symbol += character;
            } else {
                // Write output symbol
                encodedBinary = Integer.toBinaryString(dictionary.get(symbol));
                paddedBinary = convertToNBit(encodedBinary, bitSize);
                writeToFile(paddedBinary, bitOutputStream);
                dictionary.put((combined), dictionarySize);
                dictionarySize++;
                symbol = character;

                if (dictionarySize == ((Math.pow(2, bitSize)))) {
                    delimiter = Integer.toBinaryString(dictionarySize - 1);
                    writeToFile(delimiter, bitOutputStream);
                    if (bitSize < 16) {
                        bitSize++;
                    }
                }
            }
        }
        encodedBinary = Integer.toBinaryString(dictionary.get(symbol));
        paddedBinary = convertToNBit(encodedBinary, bitSize);
        writeToFile(paddedBinary, bitOutputStream);
        writeToFile("00000000", bitOutputStream); // Fix unbalanced bytes so we can read the last byte properly in the futur
        System.out.println("The file : " + inputFile + " has been compressed");
    }

    /**
     * Cette méthode permet d'exécuter les étapes de décompression
     * @param inputFile Le fichier à être décompressé.
     * @param outputFile Le nom du fichier de sortie.
     * @throws IOException
     */
    public void decode(String inputFile, String outputFile) throws IOException {
        Map<Integer, String> dicitonary = initDictionaryDecompression();
        readAndDecompressLZW(inputFile, dicitonary, outputFile);
        System.out.println("The file : " + inputFile + " has been decompressed");
    }

    /***********************************************************************************/
    /*******************    FONCTIONS DE LECTURE ET D'ÉCRITURE DE FICHIERS  ************/
    /***********************************************************************************/

    /**
     * Cette méthode permet de lire le fichier d'entrée et de remplir un tableau d'octets (byte)
     * @param inputFile Le fichier original à être lu
     * @return Un tableau d'octets.
     * @throws IOException
     */
    private byte[] readInputFile(String inputFile) throws IOException {
        byte[] bytesArray = null;
        File file = new File(inputFile);
        try (FileInputStream fileInputStreamReader = new FileInputStream(file)) {
            bytesArray = new byte[(int) file.length()];
            fileInputStreamReader.read(bytesArray);
        }
        return bytesArray;
    }

    /**
     *
     * Fonction similaire à celui de LZW avec l'ajout d'un délimiteur.
     * Celui-ci permet de reinitialiser le nombre de bits à 9, soit 2^9 (1111 1111), lorsque le code atteint 16 bits, soit le maximum.
     * @param inputFile est le fichier binaire qui doit être décompréssé.
     * @param dictionary est le dictionaire déjà initialisé.
     * @param outputFile est le fichier de sortie.
     * @throws IOException
     */
    private void readAndDecompressLZW(String inputFile, Map<Integer, String> dictionary, String outputFile) throws IOException {
        BitInputStream bitInputStream = new BitInputStream(inputFile, 8);
        DataOutputStream dataOutPutStream = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(outputFile)));
        int result = bitInputStream.readBit();
        String binaryData = "";

        int bitSize = 9;
        String delimiter = DELIMITER;

        String symbol = null;
        String sequence = "";
        String combined = "";
        int dictionaryKey;
        try {
            while (result != -1) {
                binaryData += result;
                if (binaryData.equals(delimiter)) {
                    bitSize++;
                    delimiter += "1";
                    binaryData = "";
                }
                if (bitSize > 16) {
                    bitSize = 9;
                    delimiter = DELIMITER;
                }
                if (binaryData.length() == bitSize) {
                    // Début de l'algorithme de décompression LZW
                    dictionaryKey = Integer.parseInt(binaryData, 2);

                    if (dictionarySize >= MAX_DICT_SIZE) {
                        dictionary = initDictionaryDecompression();
                    }
                    sequence = dictionary.get(dictionaryKey);
                    if (sequence == null) {
                        sequence = symbol + symbol.charAt(0);
                    }
                    // Write output sequence
                    dataOutPutStream.writeBytes(sequence);

                    if (symbol != null) {
                        combined = symbol + sequence.charAt(0);
                        dictionary.put(dictionarySize, combined);
                        dictionarySize++;
                    }
                    symbol = sequence;

                    binaryData = "";
                }
                result = bitInputStream.readBit();
            }
        } catch (IOException e) {
            dataOutPutStream.close();
        } finally {
            if (dataOutPutStream != null)
                dataOutPutStream.close();
        }
    }

    /**
     * La méthode permet d'écrire une chaine de charactères en binaire vers un fichier de sortie.
     * @param binaryString est la chaine de charactères qui représente un binaire.
     * @param bitOutputStream le fichier de sortie stream.
     */
    private void writeToFile(String binaryString, BitOutputStream bitOutputStream){
        for(int i = 0; i < binaryString.length(); i++) {
            bitOutputStream.writeBit(Character.getNumericValue(binaryString.charAt(i)));
        }
    }


    /***********************************************************************************/
    /*******************    FONCTIONS D'ALGORITHMES            *************************/
    /***********************************************************************************/


    /**
     *
     * @param data est l'octet en question
     * @return la valeur numérique en nombre entier.
     */
    private Integer byteToInt(byte data) {
        int byteToInt = data;
        if (byteToInt < 0) {
            byteToInt += 256;
        }
        return  byteToInt;
    }

    /**
     * Cette méthode permet de convertir une chaîne qui a une taille plus petite que 16.
     * @param encodedBinary Une chaîne de charactère (binaire) plus petit que 16.
     * @return Un binaire d'au moins N bits.
     */
    private String convertToNBit(String encodedBinary, int n) {
        while (encodedBinary.length() < n) {
            encodedBinary = "0" + encodedBinary;
        }
        return encodedBinary;
    }
}
