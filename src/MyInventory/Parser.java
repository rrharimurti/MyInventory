package MyInventory;

public class Parser {

    // Integer to String
    public static String toString(int n) {
        boolean isNegative = n < 0; //add a "-" in the end
        StringBuilder result = new StringBuilder(); //use String Builder
        n = Math.abs(n); //turn value non-negative
        do {
            int digit = n % 10; //mod 10 to find units digit
            result.insert(0, (char) (digit + '0'));
            n /= 10; //div 10 to remove units digit
        } while (n > 0); //do until all digits are done

        if (isNegative) {
            result.insert(0, '-');
        }

        return result.toString();
    }
    
    // Boolean to String
    public static String toString(boolean b) {
    	if (b){
    		return "Yes";
    	}
        return "No";
    }

    // String to Integer
    public static int toInt(String s) {
        int result = 0;
        int sign = 1;
        int i = 0;

        //if there is a negative, change sign and move iterator
        if (s.charAt(0) == '-') {
            sign = -1;
            i = 1;
        }

        while (i < s.length()) {
            char c = s.charAt(i); //get char at i
            if (c < '0' || c > '9') { //invalid char
                throw new IllegalArgumentException("Invalid integer format: " + s);
            }
            result = result * 10 + (c - '0'); //multiply current result by 10 and add by char to add a digit
            i++;
        }

        return result * sign; //multiply with sign
    }

    // String to Boolean (For the sake of the program, booleans are represented as "Yes" and "No" in String form)
    public static boolean toBoolean(String s) {
        if ("Yes".equalsIgnoreCase(s.trim())) {
            return true;
        } 
        else if ("No".equalsIgnoreCase(s.trim())) {
            return false;
        } 
        else {
            throw new IllegalArgumentException("Invalid boolean format: " + s);
        }
    }
    
    //Check if Integer
	public static boolean isInteger(String str) {
	    try {
	        Parser.toInt(str); //try calling the method, if it works, return true, if it doesn't return false
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

}

