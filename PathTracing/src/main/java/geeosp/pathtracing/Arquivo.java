package geeosp.pathtracing;



/*
 * Federal University of Pernambuco
 * Computing Center
 * Course: Algorithms and Data Structures
 *
 */
import java.io.*;
//
/**
 * A class that handles input and output of data from a program through
 * files.<br> <br>
 *
 * Usage Example: <br> <br> <code>
 *
 * // Opens the input and output files <br>
 * Arquivo io = new Arquivo("L1Q1.in", "L1Q1.out"); <br> <br>
 *
 * // Reads data from the input file <br>
 * String s = io.readString(); <br>
 * char c = io.readChar(); <br>
 * int i = io.readInt(); <br>
 * double d = io.readDouble(); <br> <br>
 *
 * // Writes data to the output file <br>
 * io.print("Algorithms"); <br>
 * io.print(35); <br>
 * io.println(2.3); <br> <br>
 *
 * // Closes the file after use <br>
 * io.close(); <br> </code>
 *
 * @author   Emannuel Macêdo (egm@cin.ufpe.br)
 *
 */

public class Arquivo implements AutoCloseable {

    private BufferedReader in;
    private PrintWriter out;
    private String[] buffer;
    private int nextChar;
    private int nextTokenLin, nextTokenCol;
    private int primLin, contLin;

   /**
    * Arquivo class constructor. Opens the input file in read mode,
    * and the output file in write mode. If the output file already exists,
    * its content is discarded.
    *
    * @param in     name of the input data file
    * @param out    name of the output data file
    *
    */
    public Arquivo(String in, String out) {
        try {
            // opens the input file in read mode
            this.in = new BufferedReader(new FileReader(in));

            // opens the output file in write mode
            this.out = new PrintWriter(new FileWriter(out), true);

            this.initBuffer();

        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

   /** Closes the file after use. */
    @Override
    public void close() {
        try {
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }

            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /* -------------------------------------------- */
    /* ------------ READ FUNCTIONS ------------ */
    /* -------------------------------------------- */

    /** Indicates if the end of file has been reached. */
    public boolean isEndOfFile() {
        return (this.nextTokenLin < 0);
    }

    /** Indicates if the end of line has been reached. */
    public boolean isEndOfLine() {
        return (this.nextTokenLin != this.primLin);
    }

   /**
    * Reads a line from the file. If part of the line has already been read,
    * then the remainder is returned, even if it is a blank line (zero-length string).
    *
    * @return   the next line read from the file, or <code>null</code> if the end
    *           of file is reached
    *
    */
    private String readLine() {
        if (this.contLin <= 0)
            return null;

        String line = this.buffer[this.primLin];
        if (this.nextChar > 0)
            if (this.nextChar >= line.length())
                line = "";
            else
                line = line.substring(this.nextChar, line.length()-1);

        this.buffer[this.primLin] = null;
        this.nextChar = 0;
        this.primLin++;
        this.contLin--;

        if (this.nextTokenLin >= 0 && this.nextTokenLin < this.primLin)
            this.findNext();

        return line;
    }

   /**
    * Reads the next character from the file, including spaces (' ') and line breaks
    * ('\n'). If the end of file is reached, the null character ('\0') is returned.
    *
    * @return   the character read
    */
    private char readChar() {
        if (this.contLin <= 0)
            return '\0';

        char newChar;
        String line = this.buffer[this.primLin];
        if (this.nextChar >= line.length()) {
            newChar = '\n';
            this.readLine();
        } else {
            newChar = line.charAt(this.nextChar++);
            if (newChar != ' ' && this.nextTokenLin >= 0)
                this.findNext();
        }

        return newChar;
    }

   /**
    * Reads a string from the file.
    *
    * @return   the string read
    *
    */
    public String readString() {
        String next = null;

        try {
            this.checkEOF();

            String line = this.buffer[this.nextTokenLin];
            for (int i = this.primLin; i < this.contLin; i++)
                this.buffer[i] = null;
            this.buffer[0] = line;
            this.nextTokenLin = this.primLin = 0;
            this.contLin = 1;

            int i, size = line.length();
            for (i = this.nextTokenCol; i < size; i++)
                if (line.charAt(i) == ' ')
                    break;

            next = line.substring(this.nextTokenCol, i);
            this.nextChar = i;
            this.findNext();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }

        return next;
    }

   /**
    * Reads an integer from the file.
    *
    * @return   the number read
    *
    */
    public int readInt() {
        return Integer.valueOf(this.readString()).intValue();
    }

   /**
    * Reads a double from the file.
    *
    * @return   the number read
    *
    */
    public double readDouble() {
        return Double.valueOf(this.readString()).doubleValue();
    }

    /* -------------------------------------------- */
    /* ------ AUXILIARY READ FUNCTIONS ------- */
    /* -------------------------------------------- */

    /** Prepares the input buffer for use */
    private void initBuffer() throws IOException {
        this.buffer = new String[5];
        this.nextChar = 0;
        this.nextTokenLin = 0;
        this.primLin = this.contLin = 0;

        String line = this.in.readLine();
        if (line == null) {
            this.nextTokenLin = -1;
        } else {
            this.buffer[0] = line;
            this.contLin++;
            this.findNext();
        }
    }

    /** Checks if the end of file has been reached */
    private void checkEOF() throws EOFException {
        if (this.isEndOfFile())
            throw new EOFException();
    }

    /** Adds a line read from the file to the buffer */
    private int appendLine(String str) {
        if (this.contLin == 0)
            this.primLin = 0;

        if (this.primLin + this.contLin >= this.buffer.length) {
            String[] src = this.buffer;
            if (this.contLin >= this.buffer.length)
                this.buffer = new String[2 * this.buffer.length];

            System.arraycopy(src, this.primLin, this.buffer, 0, this.contLin);
            this.nextTokenLin -= this.primLin;
            this.primLin = 0;
        }

        buffer[this.primLin + this.contLin] = str;
        this.contLin++;
        return (this.primLin + this.contLin - 1);
    }

    /** Finds the position of the next token to be read */
    private void findNext() {
        try {
            String line = this.buffer[this.primLin];
            if (line != null) {
                int size = line.length();
                for (int i = this.nextChar; i < size; i++)
                    if (line.charAt(i) != ' ') {
                        this.nextTokenCol = i;
                        return;
                    }
            }

            this.nextTokenLin = this.nextTokenCol = -1;
            while ((line = this.in.readLine()) != null) {
                int size = line.length();
                for (int i = 0; i < size; i++)
                    if (line.charAt(i) != ' ') {
                        this.nextTokenCol = i;
                        this.nextTokenLin = this.appendLine(line);
                        return;
                    }
                this.appendLine(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /* -------------------------------------------- */
    /* ------------ WRITE FUNCTIONS ------------ */
    /* -------------------------------------------- */

   /**
    * Writes a character to the file.
    *
    * @param c     character to be written to the file
    *
    */
    public void print(char c) {
        this.out.print(String.valueOf(c));
    }

   /**
    * Writes a string to the file.
    *
    * @param s     string to be written to the file
    *
    */
    public void print(String s) {
        this.out.print(s);
    }

   /**
    * Writes an integer to the file.
    *
    * @param i     number to be written to the file
    *
    */
    public void print(int i) {
        this.out.print(i);
    }

   /**
    * Writes a double to the file.
    *
    * @param d     number to be written to the file
    *
    */
    public void print(double d) {
        this.out.print(d);
    }

   /**
    * Writes a double to the file with a fixed number of decimal places. A precision
    * less than or equal to zero indicates that only the integer part will be
    * printed (with rounding).
    *
    * @param d     number to be written to the file
    * @param dec   number of decimal places for precision
    * @exception IOException   in case of I/O error
    *
    */
    public void print(double d, int dec) {
        this.out.print(this.formatDouble(d, dec));
    }

   /**
    * Starts a new line in the file.
    *
    */
    public void println() {
        this.out.println();
    }

   /**
    * Writes a character and starts a new line in the file.
    *
    * @param c     character to be written to the file
    *
    */
    public void println(char c) {
        this.out.println(String.valueOf(c));
    }

   /**
    * Writes a string and starts a new line in the file.
    *
    * @param s        string to be written to the file
    *
    */
    public void println(String s) {
        this.out.println(s);
    }

   /**
    * Writes an integer and starts a new line in the file.
    *
    * @param i        number to be written to the file
    *
    */
    public void println(int i) {
        this.out.println(i);
    }

   /**
    * Writes a double and starts a new line in the file.
    *
    * @param d        number to be written to the file
    * @exception IOException   in case of I/O error
    *
    */
    public void println(double d) {
        this.out.println(d);
    }

   /**
    * Writes a double to the file with a fixed number of decimal places and
    * starts a new line in the file. A precision less than or equal to zero
    * indicates that only the integer part will be printed (with rounding).
    *
    * @param d     number to be written to the file
    * @param dec   number of decimal places for precision
    * @exception IOException   in case of I/O error
    *
    */
    public void println(double d, int dec) {
        this.out.println(this.formatDouble(d, dec));
    }

   /**
    * Writes the buffer data to the file. This is done automatically at each
    * line break (<code>println</code>).
    */
    public void flush() {
        this.out.flush();
    }

    /* -------------------------------------------- */
    /* ------ AUXILIARY WRITE FUNCTIONS ------- */
    /* -------------------------------------------- */

	private String formatDouble(double d, int dec) {
        if (dec <= 0) {
            return String.valueOf(Math.round(d));
        }
        StringBuffer res = new StringBuffer();
        long aprox = (int) Math.round(d * Math.pow(10, dec));
        if (d < 0) {
            aprox = -aprox;
            res.append('-');
        }
        String num = String.valueOf(aprox);
        int n = num.length() - dec;
        if (n <= 0) {
            res.append("0.");
            for (int i = 0; i < -n; i++)
                res.append('0');
            res.append(num);
        } else {
            char[] array = num.toCharArray();
            res.append(array, 0, n).append('.').append(array, n, dec);
        }
        return res.toString();
    }

}
