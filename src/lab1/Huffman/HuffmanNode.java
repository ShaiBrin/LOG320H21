package lab1.Huffman;
/*
  CODE EMPRUNTÉE :
   Les lignes suivantes sont basées sur une classe provenant du site :
       https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
   (consultée le 20 janvier 2021)
   Ceci n'est pas un code emprunté au complet. Certains ajustement ont été faite
   Nous avons adaptée ce code a notre utilisation. Nous utilisons plutot les variable int frequency
   et byte data.
*/
import java.io.Serializable;
import java.util.BitSet;


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

/* FIN DU CODE EMPRUNTÉ */