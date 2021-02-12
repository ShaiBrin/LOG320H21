package lab1.Huffman;
import lab1.BitInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

// 1. Read input File
// 2. Build frequency table
// 3. Build Huffman Tree
// 4. Encode Huffman Root and Huffman BITS
// 5. Output file



// 1. Read input file
// 2. Build Huffman Tree
// 3. Decode BITS
// 4. Output file

public class Huffman {
    static HuffmanNode root;

    public static void encode(String filePath, String outputFilePath) throws IOException, ClassNotFoundException {
        Map<Byte, String> bytePrefixHashMap = new HashMap<>();


        //// 1. Read input File
        byte[] fileInByteArray = Files.readAllBytes(Paths.get(filePath));

        // 2. Build frequency table
        Map<Byte, Integer> sortedFrequencyTable = createFrequencyTable(fileInByteArray);
        System.out.println(sortedFrequencyTable);
        // 3. Build Huffman Tree
        buildTree(sortedFrequencyTable);
        setPrefixCodes(bytePrefixHashMap, root, new StringBuilder());
        // 4. Encode Huffman Root and Huffman BITS
        System.out.println(bytePrefixHashMap);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < fileInByteArray.length; i++) {
            s.append(bytePrefixHashMap.get(fileInByteArray[i]));
        }
        System.out.println(s.toString());


        ArrayList<Integer> bits = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        EncodeNode(root, bits, sb);

        List<byte[]> byteList = new ArrayList<>();
        // Convert FrequencyMap to Bytes
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(sortedFrequencyTable);
        byteList.add(byteOut.toByteArray());

        // Convert byteString to Bytes
        ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream();
        ObjectOutputStream out2 = new ObjectOutputStream(byteOut2);
        out2.writeObject(s.toString());
        byteList.add(byteOut2.toByteArray());

        // Write encoded File
        WriteObjectToFile(byteList, outputFilePath);
    }

    public static void decode(String fileInputPath, String fileOutputPath) throws IOException, ClassNotFoundException {
        List<byte[]> byteList = (List<byte[]>) ReadObjectFromFile(fileInputPath);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteList.get(0));
        ObjectInputStream in = new ObjectInputStream(byteIn);
        Map<Byte, Integer> frequencyMap = (Map<Byte, Integer>) in.readObject();
        System.out.println(frequencyMap);

        ByteArrayInputStream byteIn2 = new ByteArrayInputStream(byteList.get(1));
        ObjectInputStream in2 = new ObjectInputStream(byteIn2);
        String stringBytes = (String) in2.readObject();

        // Build the tree (root)
        HuffmanNode root2 = buildTree2(frequencyMap);
        HuffmanNode originalTree = root2;

        BitSet bitset = new BitSet(stringBytes.length());
        for(int i = 0; i < stringBytes.length(); i++) {

                if(Character.getNumericValue(stringBytes.charAt(i)) == 1){
                    bitset.set(i);
                }
        }


        ArrayList<Byte> arrayByte = new ArrayList<Byte>();
        HuffmanNode temps = originalTree;
        for(int i = 0; i < stringBytes.length(); i++) {
//            int bit = Character.getNumericValue(stringBytes.charAt(i));
            int bit = bitset.get(i) ? 1 : 0;

            if(bit == 0) {
                temps = temps.left;
            }
            else{
                temps = temps.right;
            }

            if(temps.right == null && temps.left == null) {
                arrayByte.add(temps.data);
                System.out.println(temps.data);
                temps = originalTree;
            }


        }
        System.out.println(arrayByte);

        Files.write(new File(fileOutputPath).toPath(), buildByteArray(arrayByte));
    }

    //https://examples.javacodegeeks.com/core-java/io/file/how-to-read-an-object-from-file-in-java/
    public static Object ReadObjectFromFile(String filepath) {

        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //javacodegeeks
    public static void WriteObjectToFile(Object serObj, String outputFilePath) {

        try {

            FileOutputStream fileOut = new FileOutputStream(outputFilePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void EncodeNode(HuffmanNode node, ArrayList<Integer> bits, StringBuilder sb){

        if(node.left == null){
            bits.add(1);
            sb.append(1);
            String bitString = String.format("%8s", Integer.toBinaryString(node.data & 0xFF)).replace(' ', '0');
            for(char c : bitString.toCharArray()) {
                bits.add(Character.getNumericValue(c));
                sb.append(Character.getNumericValue(c));
            }
        }
        else{
            bits.add(0);
            sb.append(0);
            EncodeNode(node.left, bits, sb);
            EncodeNode(node.right, bits, sb);
        }
    }


    // Used : https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
    private static void buildTree(Map<Byte, Integer> sortedFrequencyTable){
        int n = sortedFrequencyTable.size();

        PriorityQueue<HuffmanNode> q
                = new PriorityQueue<HuffmanNode>(n, new MyComparator());

        for(Byte key: sortedFrequencyTable.keySet()){

            HuffmanNode hn = new HuffmanNode();

            hn.data = key;
            hn.frequency = sortedFrequencyTable.get(key);

            hn.left = null;
            hn.right = null;

            q.add(hn);
        }

        root = null;

        while (q.size() > 1) {

            HuffmanNode x = q.peek();
            q.poll();
            HuffmanNode y = q.peek();
            q.poll();

            HuffmanNode f = new HuffmanNode();

            f.frequency = x.frequency + y.frequency;
            f.data = '-';
            f.left = x;
            f.right = y;
            root = f;
            q.add(f);
        }
    }

    private static HuffmanNode buildTree2(Map<Byte, Integer> sortedFrequencyTable){
        int n = sortedFrequencyTable.size();
        HuffmanNode rootNode = null;
        PriorityQueue<HuffmanNode> q
                = new PriorityQueue<HuffmanNode>(n, new MyComparator());

        for(Byte key: sortedFrequencyTable.keySet()){

            HuffmanNode hn = new HuffmanNode();

            hn.data = key;
            hn.frequency = sortedFrequencyTable.get(key);

            hn.left = null;
            hn.right = null;

            q.add(hn);
        }

        root = null;

        while (q.size() > 1) {

            HuffmanNode x = q.peek();
            q.poll();
            HuffmanNode y = q.peek();
            q.poll();

            HuffmanNode f = new HuffmanNode();

            f.frequency = x.frequency + y.frequency;
            f.data = '-';
            f.left = x;
            f.right = y;
            rootNode = f;
            q.add(f);
        }
        return rootNode;
    }
    /*
      CODE EMPRUNTÉE :
       Les lignes suivantes sont basées sur une classe provenant du site :
           https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
       (consultée le 20 janvier 2021)
       Ceci n'est pas un code emprunté au complet. Certains ajustement ont été faite
       Nous avons adaptée ce code a notre utilisation.
       Assigner un prefix de code a un caractere. Important pour la construction de l'arbre de Huffman.
    */
    private static void setPrefixCodes(Map<Byte, String> bytePrefixHashMap, HuffmanNode node, StringBuilder prefix) {

        if (node != null) {
            if (node.left == null && node.right == null) {
                bytePrefixHashMap.put(node.data, prefix.toString());

            } else {
                prefix.append('0');
                setPrefixCodes(bytePrefixHashMap, node.left, prefix);
                prefix.deleteCharAt(prefix.length() - 1);

                prefix.append('1');
                setPrefixCodes(bytePrefixHashMap, node.right, prefix);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }
    /* FIN DU CODE EMPRUNTÉ */

    private static int[] readFileToByte(String filePath, int byteSize){
        BitInputStream bitInputStream = new BitInputStream(filePath, byteSize);
        ArrayList<Integer> arrayOfBytes = new ArrayList<>();

        int currentByte = bitInputStream.readBit();
        arrayOfBytes.add(currentByte);
        while(currentByte != -1){
            if(currentByte != -1){
                currentByte = bitInputStream.readBit();
                arrayOfBytes.add(currentByte);
            }

        }
        System.out.println(arrayOfBytes);
        return buildIntArray(arrayOfBytes);

    }

    /*
      CODE EMPRUNTÉE :
       Les lignes suivantes sont basées sur une classe provenant du site :
           https://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array
       (consultée le 20 janvier 2021)
       Conversion d'une ArrayList en type Integer.
       @return tableau en Integer
    */
    private static int[] buildIntArray(ArrayList<Integer> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;
        for (Integer n : integers) {
            ints[i++] = n;
        }
        return ints;
    }
    /* FIN DU CODE EMPRUNTÉ */

    /*
      CODE EMPRUNTÉE :
       Les lignes suivantes sont basées sur une classe provenant du site :
           https://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array
       (consultée le 20 janvier 2021)
       Cette version du code empreinter est adapter a notre implementation
       Conversion d'une ArrayList en type Byte.
       @return tableau en Byte
    */
    private static byte[] buildByteArray(ArrayList<Byte> integers) {
        byte[] bytes = new byte[integers.size()];
        int i = 0;
        for (Byte n : integers) {
            bytes[i++] = n;
        }
        return bytes;
    }
    /* FIN DU CODE EMPRUNTÉ */

    /*
      CODE EMPRUNTÉE :
       Les lignes suivantes sont basées sur une classe provenant du site :
           https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
       (consultée le 20 janvier 2021)
        Création de la table de fréquence pour l'algorithme de Huffman
        Génération des occurences de chacune des caracteres dans le fichier a compresser
    */
    private static Map<Byte, Integer> createFrequencyTable(byte[] array) throws FileNotFoundException,IOException
    {
        Map<Byte, Integer> sortedFrequencyTable = createMapFromBytes(array).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return sortedFrequencyTable;
    }
    /* FIN DU CODE EMPRUNTÉ */


    private static Map<Byte, Integer> createMapFromBytes(byte[] array) throws FileNotFoundException,IOException
    {
        Map<Byte, Integer> map = new HashMap<>();
        for(int i=0; i< array.length; i++){
            count(array[i], map);
        }

        return map;
    }

    /*
      CODE EMPRUNTÉE :
       Les lignes suivantes sont basées sur une classe provenant du site :
           https://stackoverflow.com/questions/15217438/counting-occurrences-of-a-key-in-a-map-in-java
       (consultée le 20 janvier 2021)
        Permert de savoir le nombre d'occurence d'une clé dans une structure de donnée MAP
    */
    private static <K> void count(K key, Map<K, Integer> map) {
        map.merge(key, 1, (currentCount, notUsed) -> ++currentCount);
    }
    /* FIN DU CODE EMPRUNTÉ */
}
