/*
 * This class implements Run Length Encoding (RLE) compression.
 Input - "aaaabccdeeee"
 Output - "4a1b2c1d4e"

*/
package stringtest;
public static String compress(String input) {
    if (input == null || input.isEmpty()) {
        return input;
    }
    StringBuilder compressed = new StringBuilder();
    int count = 1;
    for(int i = 0; i < len(input); i++){
        if input[i].equals(input[i+1]) {
            count += 1;
        }
        else {
            compressed.append(count);
            compressed.append(input[i]);
            count = 1;
        }
    }
    return compressed.toString();
}
