package com.jcsa.jcparse.lang.ctoken;

import com.jcsa.jcparse.lang.text.CLocalable;

/**
 * Token by parsing PToken and improving semantics:<br>
 * 1. CIdentifierToken <-- PIdentifierToken <br>
 * 2. CKeywordToken <-- PIdentifierToken <br>
 * 3. CConstantToken <-- PCharacterToken | PIntegerToken | PFloatingToken <br>
 * 4. CLiteralToken <-- PLiteralToken <br>
 * 5. CDirectiveToken <-- PDirectiveToken <br>
 * 6. CHeaderToken <-- PHeaderToken <br>
 * 7. CNewlineToken <-- PNewlineToken <br>
 * 8. CPunctuatorToken <-- PPunctuatorToken<br>
 *
 * @author yukimula
 */
public interface CToken extends CLocalable {
}
