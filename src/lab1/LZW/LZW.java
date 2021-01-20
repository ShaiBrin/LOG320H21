package lab1.LZW;



import java.io.*;
import java.util.*;


public class LZW {


    private int dictionarySize = 256;
    private static final int INITIAL_DICT_SIZE = 256;
    private static final int MAX_DICT_SIZE = 65536;


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
