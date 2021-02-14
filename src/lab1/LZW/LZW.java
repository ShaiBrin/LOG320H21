package lab1.LZW;



import java.io.*;
import java.util.*;
import lab1.BitInputStream;
import lab1.BitOutputStream;

public class LZW {


    private int dictionarySize = 256;
    private static final int INITIAL_DICT_SIZE = 256;
    private static final int MAX_DICT_SIZE = 65536;


    /**
     * Constructeur par défaut.
     */
    public LZW(){}


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
     * La méthode readAndDecompress lit le fichier d'entrée et décompresse vers le fichier de sortie.
     * Chaque code est décompressé lorsqu'il atteint une taille de 16 bits.
     * @param inputFile est le fichier binaire qui doit être décompressé.
     * @param dictionary est un dicionnaire de code.
     * @param outputFile est le fichier de sortie.
     * @throws IOException
     */
    private void readAndDecompressLZW(String inputFile, Map<Integer, String> dictionary, String outputFile) throws IOException{
        BitInputStream inputStream = new BitInputStream(inputFile,8);
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(outputFile)
        ));
        int resultBinary = inputStream.readBit();

        String symbol = null;
        String combined = "";
        String binaryInfo = "";
        String sequence = "";

        int dictionaryKey = 0;

        try{
            while (resultBinary != -1){
                binaryInfo += resultBinary;
                if(binaryInfo.length() == 16){


                    //Transformation du nombre binaire en nombre entier.
                    dictionaryKey = Integer.parseInt(binaryInfo, 2);

                    // Si la taille du dictionnaire dépasse les 256 charactères.
                    if(dictionarySize >= MAX_DICT_SIZE){
                        dictionary = initDictionaryDecompression();
                    }

                    sequence = dictionary.get(dictionaryKey);


                    //Si la séquence est vide, alors il faut recommencer à la première charactère.
                    if(sequence == null){
                        sequence = symbol + symbol.charAt(0);
                    }

                    //Écriture de la séquence output.
                    outputStream.writeBytes(sequence);

                    if(symbol!=null){
                        combined = symbol + sequence.charAt(0);
                        dictionary.put(dictionarySize, combined);
                        dictionarySize++;
                    }

                    symbol = sequence;
                    binaryInfo = "";
                }
                resultBinary = inputStream.readBit();
            }
        }catch (IOException e){
            outputStream.close();

        }finally {
            if(outputStream != null){
                outputStream.close();
            }
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
    /*******************    FONCTIONS D'ENCODAGE ET DÉCODAGE   *************************/
    /***********************************************************************************/

    /**
     *
     * @param inputFile est le fichier d'entrée en String.
     * @param outputFile est le fichier de sortie et octets.
     * @throws IOException
     */
    public void encode(String inputFile, String outputFile) throws IOException {
        Map<String, Integer> dictionary = initDictionary();
        BitOutputStream binaryOutPutStream = new BitOutputStream(outputFile, 8);
        byte[] data = readInputFile(inputFile);


        //Transformation d'un octet en valeur numérique.
        int byteToInt = byteToInt(data[0]);


        String symbol = Character.toString((char) byteToInt);
        String character = "";
        String combined = "";
        String encodedBinary = "";
        String paddedBinary = "";

        for (int i = 1; i < data.length; i++) {
            if (dictionarySize >= MAX_DICT_SIZE) {
                //Le dictionnaire est reinitialisé si la taille dépasse le maximum.
                dictionary = initDictionary();
            }
            byteToInt = byteToInt(data[i]);
            character = Character.toString((char) byteToInt);
            combined = symbol + character;

            if (dictionary.containsKey(combined)) {
                symbol += character;
            } else {
                // Write output symbol
                encodedBinary = Integer.toBinaryString(dictionary.get(symbol));
                paddedBinary = convertTo16Bit(encodedBinary);
                writeToFile(paddedBinary, binaryOutPutStream);

                dictionary.put((combined), dictionarySize);
                dictionarySize++;

                symbol = character;
            }
        }
        encodedBinary = Integer.toBinaryString(dictionary.get(symbol));
        paddedBinary = convertTo16Bit(encodedBinary);
        writeToFile(paddedBinary, binaryOutPutStream);
        System.out.println("The file : " + inputFile + " has been compressed");
    }


    /**
     * Méthode pour décompresser le fichier.
     * @param inputFile le fichier qui doit être décompressé.
     * @param outputFile le fichier de sortie.
     * @throws IOException
     */
    public void decode(String inputFile, String outputFile) throws IOException {
        Map<Integer, String> dicitonary = initDictionaryDecompression();
        readAndDecompressLZW(inputFile, dicitonary, outputFile);
        System.out.println("The file : " + inputFile + " has been decompressed");
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
     * @return Un binaire d'au moins 16 bits.
     */
    private String convertTo16Bit(String encodedBinary) {
        while (encodedBinary.length() < 16) {
            encodedBinary = "0" + encodedBinary;
        }
        return encodedBinary;
    }
}
