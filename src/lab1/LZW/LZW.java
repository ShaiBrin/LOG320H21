package lab1.LZW;



import java.io.*;
import java.util.*;
import lab1.BitInputStream;
import lab1.BitOutputStream;

public class LZW {


    private int CURR_DICT_SIZE = 256;
    private static final int START_DICT_SIZE = 256;
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
        for (int i = 0; i < START_DICT_SIZE; i++) {
            dictionary.put(Character.toString((char) i), i);
        }
        CURR_DICT_SIZE = START_DICT_SIZE;
        return dictionary;
    }

    /**
     * Cette méthode permet d'initialiser le dictionnaire pour la procédure de décompression.
     * La différence entre celui-ci et "initDictionary" c'est qu'on a interchangé la clé et la valeur.
     * Dans initDictionary on a une map de String, Integer et dans celui-ci on a Integer,String, car
     * dans la décompression on fait le contraire de la compression.
     * @return Un dictionaire ou plutôt une Map contenant comme clé : Un integer et comme valeur: une chaîne de charactère
     */
    private Map<Integer, String> initDictDecompression() {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < START_DICT_SIZE; i++) {
            dictionary.put(i, Character.toString((char) i));
        }
        CURR_DICT_SIZE = START_DICT_SIZE;
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


        String s = Character.toString((char) byteToInt);
        String c = "";
        String combined = "";


        for (int i = 1; i < data.length; i++) {
            if (CURR_DICT_SIZE >= MAX_DICT_SIZE) {
                //Le dictionnaire est reinitialisé si la taille dépasse le maximum.
                dictionary = initDictionary();
            }
            byteToInt = byteToInt(data[i]);
            c = Character.toString((char) byteToInt);
            combined = s + c;

            if (dictionary.containsKey(combined)) {
                s += c;
            } else {
                // Write output s
                writeCompressedFile(dictionary, s, binaryOutPutStream);
                dictionary.put((combined), CURR_DICT_SIZE);
                CURR_DICT_SIZE++;

                s = c;
            }
        }
        writeCompressedFile(dictionary, s, binaryOutPutStream);
    }

    private void writeCompressedFile(Map<String, Integer> dictionary, String s, BitOutputStream binaryOutPutStream){
        String encoded = Integer.toBinaryString(dictionary.get(s));
        String padding = convertTo16Bit(encoded);

        writeToFile(padding, binaryOutPutStream);
    }

    /**
     * Méthode pour décompresser le fichier.
     * @param inputFile le fichier qui doit être décompressé.
     * @param outputFile le fichier de sortie.
     * @throws IOException
     */
    public void decode(String inputFile, String outputFile) throws IOException {
        Map<Integer, String> dictionary = initDictDecompression();
        BitInputStream inputStream = new BitInputStream(inputFile,8);
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(outputFile)
        ));
        int resultBinary = inputStream.readBit();

        String s = null;
        String combined = "";
        String binary = "";
        String seq = "";

        int dictionaryKey = 0;

        try{
            while (resultBinary != -1){
                binary += resultBinary;
                if(binary.length() == 16){


                    //Transformation du nombre binaire en nombre entier.
                    dictionaryKey = Integer.parseInt(binary, 2);

                    // Si la taille du dictionnaire dépasse les 256 charactères.
                    if(CURR_DICT_SIZE >= MAX_DICT_SIZE){
                        dictionary = initDictDecompression();
                    }

                    seq = dictionary.get(dictionaryKey);

                    if(seq == null){
                        seq = s + s.charAt(0);
                    }

                    outputStream.writeBytes(seq);

                    if(s!=null){
                        combined = s + seq.charAt(0);
                        dictionary.put(CURR_DICT_SIZE, combined);
                        CURR_DICT_SIZE++;
                    }

                    s = seq;
                    binary = "";
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
