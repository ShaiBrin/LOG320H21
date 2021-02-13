package lab1.Huffman;

import java.util.Comparator;

// Basé sur : https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
public class MyComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y)
    {
        return x.frequency - y.frequency;
    }
}