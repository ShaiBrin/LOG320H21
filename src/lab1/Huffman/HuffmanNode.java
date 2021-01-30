package lab1.Huffman;

import java.io.Serializable;
import java.util.BitSet;

// Used : https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
public class HuffmanNode implements Serializable {

    int frequency;
    byte data;

    HuffmanNode left;
    HuffmanNode right;

    BitSet bitSet;

    public HuffmanNode(){
    }

    public HuffmanNode(Byte data, HuffmanNode left, HuffmanNode right){
        this.data = data;
        this.left = left;
        this.right = right;
    }
}