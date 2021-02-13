package lab1.Huffman;
/*
  CODE EMPRUNTÉE :
   Les lignes suivantes sont basées sur une classe provenant du site :
       https://www.geeksforgeeks.org/huffman-coding-greedy-algo-3/
   (consultée le 20 janvier 2021)
   Ceci n'est pas un code emprunté au complet. Certains ajustement ont été fait
   Nous avons adaptée ce code a notre utilisation. Nous utilisons plutot les variable int frequency
   et byte data.
*/
import java.io.Serializable;


public class HuffmanNode implements Serializable {

    int frequency;
    byte data;

    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(){
    }

}

/* FIN DU CODE EMPRUNTÉ */