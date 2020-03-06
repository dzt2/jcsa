package com.jcsa.jcparse.lang.ptoken;

import com.jcsa.jcparse.lang.text.CLocalable;

/**
 * Token at preprocessing phasis, including: <br>
 * 1. PIdentifierToken<br>
 * 2. PCharacterToken <br>
 * 3. PIntegerToken <br>
 * 4. PFloatingToken <br>
 * 5. PLiteralToken <br>
 * 6. PPunctuatorToken <br>
 * 7. PDirectiveToken <br>
 * 8. PHeaderToken <br>
 * 9. PNewlineToken <br>
 * 10. PCommentToken <br>
 * 
 * @author yukimula
 *
 */
public interface PToken extends CLocalable {
}
