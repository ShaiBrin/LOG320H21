package lab1;

/*
  CODE EMPRUNTÉE :
   Les lignes suivantes sont basées sur une classe provenant du site :
       https://courses.cs.washington.edu/courses/cse143/12sp/homework/ass8/BitInputStream.java
   (consultée le 20 janvier 2021)
*/


// The BitOutputStream and BitInputStream classes provide the ability to
// write and read individual bits to a file in a compact form.  One major
// limitation of this approach is that the resulting file will always have
// a number of bits that is a multiple of 8.  In effect, whatever bits are
// output to the file are padded at the end with 0's to make the total
// number of bits a multiple of 8.
//
// BitInputStream has the following public methods:
//     public BitInputStream(String file)
//         opens an input stream with the given file name
//     public int readBit()
//         reads the next bit from input (-1 if at end of file)
//     public void close()
//         closes the input

import java.io.*;

public class BitInputStream {
    private FileInputStream input;
    private int digits;     // next set of digits (buffer)
    private int numDigits;  // how many digits from buffer have been used

    private int byteSize;  // digits per byte

    // pre : given file name is legal
    // post: creates a BitInputStream reading input from the file
    public BitInputStream(String file, int byteSize) {
        try {
            input = new FileInputStream(file);
            this.byteSize = byteSize;
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        nextByte();
    }

    // post: reads next bit from input (-1 if at end of file)
    public int readBit() {
        // if at eof, return -1
        if (digits == -1)
            return -1;
        int result = digits % 2;
        digits /= 2;
        numDigits++;
        if (numDigits == this.byteSize){
            nextByte();
        }
        return result;
    }

    // post: refreshes the internal buffer with the next BYTE_SIZE bits
    private void nextByte() {
        try {
            digits = input.read();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        numDigits = 0;
    }

    // post: input is closed
    public void close() {
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    // included to ensure that the stream is closed
    protected void finalize() {
        close();
    }
}

/*FIN DU CODE EMPRUNTÉE*/