package lab1;

/*
  CODE EMPRUNTÉE :
   Les lignes suivantes sont basées sur une classe provenant du site :
       https://courses.cs.washington.edu/courses/cse143/12sp/homework/ass8/BitInputStream.java
   (consultée le 20 janvier 2021)
*/

import java.io.FileOutputStream;
import java.io.IOException;

// The BitOutputStream and BitInputStream classes provide the ability to
// write and read individual bits to a file in a compact form.  One major
// limitation of this approach is that the resulting file will always have
// a number of bits that is a multiple of 8.  In effect, whatever bits are
// output to the file are padded at the end with 0's to make the total
// number of bits a multiple of 8.
//
// BitOutputStream has the following public methods:
//     public BitOutputStream(String file)
//         opens an output stream with the given file name
//     public void writeBit(int bit)
//         write given bit to output
//     public void close()
//         closes the output, flushing the internal buffer

public class BitOutputStream {
    private FileOutputStream output;
    private int digits;     // a buffer used to build up next set of digits
    private int numDigits;  // how many digits are currently in the buffer

    private int byteSize;  // digits per byte

    // pre : given file name is legal
    // post: creates a BitOutputStream sending output to the file
    public BitOutputStream(String file, int byteSize) {
        try {
            output = new FileOutputStream(file);
            this.byteSize = byteSize;
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        digits = numDigits = 0;
    }

    // post: writes given bit to output
    public void writeBit(int bit) {
        if (bit < 0 || bit > 1)
            throw new IllegalArgumentException("Illegal bit: " + bit);
        digits += bit << numDigits;
        numDigits++;
        if (numDigits == this.byteSize) {
            flush();
        }
    }

    // post: Flushes the buffer.  If numDigits < BYTE_SIZE, this will
    //       effectively pad the output with extra 0's, so this should
    //       be called only when numDigits == BYTE_SIZE or when we are
    //       closing the output.
    private void flush() {
        try {
            output.write(digits);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        digits = 0;
        numDigits = 0;
    }

    // post: output is closed
    public void close() {
        if (numDigits > 0)
            flush();
        try {
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    // included to ensure that the stream is closed
    protected void finalize() {
        close();
    }
}

/* FIN DU CODE EMPRUNTÉ */