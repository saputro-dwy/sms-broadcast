/*
 * KazaoCalendarDateTimeException.java
 *
 * Created on 20 September 2007, 22:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kazao;

/**
 *
 * @author    Mr. Kazao
 * Email    : m.jumari@gmail.com
 * Website  : http://mr.kazao.net
 * Phone    : +622743251763 +6281904091661
 */
public class KazaoCalendarDateTimeException extends Exception {
    /**
     * Creates a new instance of KazaoCalendarDateTimeException
     */
    public KazaoCalendarDateTimeException() {
        super("Date and time exception");        
    }
    
    public KazaoCalendarDateTimeException(String message) {
        super("Date and time exception\n"+message);        
    }
    
}
